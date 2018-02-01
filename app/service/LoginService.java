package service;

import dao.AdminDao;
import dao.AdmissionInfoDao;
import dao.UserDao;
import dao.UserTestDao;
import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.SearchFilter;
import play.cache.Cache;
import play.mvc.Http;
import service.SessionService.SessionItemMark;
import utils.DateUtils;
import utils.MD5Utils;
import utils.SystemConstant;

import java.util.Date;
import java.util.List;

/**
 * Created by mare on 15/7/13.
 */

@Service("loginService")
public class LoginService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AdmissionInfoDao admissionInfoDao;
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private UserTestDao userTestDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private TestpaperQuestiontypeService testpaperQuestiontypeService;
    @Autowired
    private QuestionTypeService questionTypeService;

    public enum LoginResult {
        USER_NONEXISTING, // 用户不存在
        ADMIN_NONEXISTIING,

        LOGIN_SUCCESS,
        LOGOUT_SUCCESS,
        WRONG_PASSWORD,// 密码错误

        NOT_CORRECR_USER,//IP验证错误

        NO_ACTIVE_USER_TEST, // 考生没有在考试开始半小时内登录系统
        FINISH_TEST // 本时段的考试已经考完
    }

    //private static final Long INACTIVE_TIME_SPAN = 3600L * 1000;
    private static final Long INACTIVE_TIME_SPAN = 150L * 60 * 1000;
    private static final Long USER_LOGIN_SPAN = 30L * 60 * 1000;

    /**
     * 登陆考试系统
     * @param account
     * @param password
     * @return
     */
    public LoginResult userLogin(String account, String password, String ip) {

        User user = userDao.findByAccount(account);// 验证用户
        /* 用户不存在 */
        if (user == null){
            return LoginResult.USER_NONEXISTING;
        }
        /* 密码错误 */
        if (!user.getPassword().equals(MD5Utils.getMD5Code(password))) {
            return LoginResult.WRONG_PASSWORD;
        }
        /* 验证考生IP信息 */
        AdmissionInfo admissionInfo = admissionInfoDao.findOne(SearchFilter.eq("uid", user.getId()));
        if (org.apache.commons.lang3.StringUtils.isNotBlank(admissionInfo.getIp())) {
            admissionInfo = admissionInfoDao.findOne(SearchFilter.eq("uid", user.getId()), SearchFilter.eq("ip", ip));
            if (admissionInfo == null){
                return LoginResult.NOT_CORRECR_USER;
            }
        }
        Test activeTest = checkLoginTime(user);// 验证考生是否在指定的考试时间范围内登录的
        /* 考生没有在考试开始半小时内登录系统 */
        if (activeTest == null) {
            return LoginResult.NO_ACTIVE_USER_TEST;
        }
        /* 验证考生是否考过指定的考试 */
        if(isFinishTest(user, activeTest)){
            return LoginResult.FINISH_TEST;
        };

        user.setLastLogintime(SessionService.curTimestamp());// 记录考生的末次登录时间
        userDao.save(user);

        String qtypeListJson = testpaperQuestiontypeService.findQtidListByTpid(activeTest.getPid());// 获取考试题型顺序
        sessionService.initUserSessionState(user, activeTest, qtypeListJson);// 初始化session数据
        questionTypeService.updateQtypeTimeEachMapCache();
        return LoginResult.LOGIN_SUCCESS;// 登录成功
    }

    /**
     * 登出考试系统
     * @return
     */
    public LoginResult userLogout() {
        String account = sessionService.getSessionItem(SessionItemMark.LOGIN_ACCOUNT);
        LoginResult result;
        if (account == null){
            result = LoginResult.USER_NONEXISTING;
        } else {
            result = LoginResult.LOGOUT_SUCCESS;
        }
        sessionService.clearUserSessionState();
        Cache.remove("user_" + account);
        return result;
    }

    /**
     * 登陆管理系统
     * @param loginName
     * @param password
     * @return
     */
    public LoginResult adminLogin(String loginName, String password) {
        Admin admin = adminDao.findByLoginName(loginName);
        LoginResult result;
        if (admin == null) {
            result = LoginResult.ADMIN_NONEXISTIING;// 用户不存在
        } else if (!admin.getPassword().equals(password)) {
            result = LoginResult.WRONG_PASSWORD;// 密码错误
        } else {
            admin.setLastLogintime(SessionService.curTimestamp());
            adminDao.save(admin);// 更新末次登录时间
            sessionService.initAdminSessionState(admin);// 初始化session
            result = LoginResult.LOGIN_SUCCESS;// 登陆成功
        }
        return result;
    }

    /**
     * 登出管理系统
     * @return
     */
    public LoginResult adminLogout() {
        String loginName = sessionService.getSessionItem(SessionItemMark.LOGIN_NAME);
        LoginResult result;
        if (loginName == null) {
            result = LoginResult.ADMIN_NONEXISTIING;
        } else {
            result = LoginResult.LOGOUT_SUCCESS;
        }
        sessionService.clearAdminSessionState();
        return result;
    }

    public Boolean validUserLogin() {
        String latestActiveTime = sessionService.getSessionItem(SessionItemMark.LATEST_ACTIVE_TIME);// 获取session中的末次访问时间
        if (sessionService.getSessionItem(SessionItemMark.LOGIN_ACCOUNT) == null || latestActiveTime == null) {
            return false;
        }
        Long curTime = SessionService.curTime();
        if (Long.valueOf(latestActiveTime) + INACTIVE_TIME_SPAN > curTime) {
            sessionService.saveSessionItem(SessionItemMark.LATEST_ACTIVE_TIME, curTime.toString());
            return true;
        } else {
            return false;
        }
    }

    public Boolean validAdminLogin() {
        String latestActiveTime = sessionService.getSessionItem(SessionItemMark.LATEST_ACTIVE_TIME);// 获取session中的末次访问时间
        if (sessionService.getSessionItem(SessionItemMark.LOGIN_NAME) == null || latestActiveTime == null) {
            return false;
        }
        Long curTime = SessionService.curTime();
        if (Long.valueOf(latestActiveTime) + INACTIVE_TIME_SPAN > curTime) {
            sessionService.saveSessionItem(SessionItemMark.LATEST_ACTIVE_TIME, curTime.toString());
            return true;
        } else {
            return false;
        }
    }

    public User checkUserInfoByIp(String ip) {
        AdmissionInfo admissionInfo = admissionInfoDao.findOne(SearchFilter.eq("ip", ip));
        if (admissionInfo != null){
            User user = userDao.findById(admissionInfo.getUid());
            return user;
        }else{
            return null;
        }
    }

    /**
     * 验证考生是否在指定的考试时间范围内登录的,如果是，将本次考试信息返回
     * @param user
     * @return
     */
    private Test checkLoginTime(User user) {
        List<Test> tests = userService.findRelatedTests(user);// 通过用户获取考试信息
        Long curTime = SessionService.curTime();// 当前系统时间
        Long currentDay = DateUtils.getCurrentDay(new Date());
        for (Test test: tests){
            Long loginStart = test.getStartTime().getTime();
//            Long loginEnd = loginStart + USER_LOGIN_SPAN;
//            if (loginStart <= curTime && curTime <= loginEnd) {
//            if (loginStart <= curTime && curTime <= currentDay*1000 + 3600 * 24 * 1000) {
            if (loginStart <= curTime && curTime <= test.getEndTime().getTime()) {
                return test;
            }
        }
        return null;
    }

    /**
     * 验证考生是否考过指定的考试
     * @param user
     * @param test
     * @return
     */
    private boolean isFinishTest(User user, Test test) {
        UserTest userTest = userTestDao.findOne(SearchFilter.eq("tid", test.getId()), SearchFilter.eq("uid", user.getId()));
        if(SystemConstant.USER_TEST_ISOVER_YES.equals(userTest.getIsover())){
            return true;
        } else {
            return false;
        }
    }
}
