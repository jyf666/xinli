/***********************************************************************
 * Module:  LoginController.java
 * Author:  XIAODA
 * Purpose: Defines the Class LoginController
 ***********************************************************************/

package controllers;

import interactModels.AdminLoginData;
import interactModels.UserLoginData;
import models.Test;
import models.User;
import models.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import play.cache.Cache;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.SystemConstant;
import views.html.index;
import views.html.login;
import views.html.admin.adminLogin;
import views.html.common.modal.userinfo;
import service.LoginService.LoginResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 登录控制
 */
@org.springframework.stereotype.Controller
public class LoginController extends Controller {

   private static Logger logger = LoggerFactory.getLogger(LoginController.class);
   @Autowired
   private LoginService loginService;
   @Autowired
   private UserService userService;
   @Autowired
   private SessionService sessionService;
   /* 演示用 start*/
   @Autowired
   private TestService testService;
   @Autowired
   private TestpaperQuestiontypeService testpaperQuestiontypeService;
   /* 演示用 end*/

   private Form<UserLoginData> userLoginForm = Form.form(UserLoginData.class);

   private Form<AdminLoginData> adminLoginForm = Form.form(AdminLoginData.class);

   /**
    * 登录：校验用户名密码，如登录成功，更新末次登陆时间。如登录失败，跳转至相应页面，提示用户错误信息。
    * @return
    */
   public Result userLogin() throws UnsupportedEncodingException {

      UserLoginData userLoginData = userLoginForm.bindFromRequest().get();
      String account = userLoginData.getAccount();
      String password = userLoginData.getPassword();
      String ipaddress = request().remoteAddress();
      Result result = null;
      if (StringUtils.isBlank(account) || StringUtils.isBlank(password)){
         result = ok(login.render("账号或密码不能为空！"));
         logger.debug("账号或密码不能为空！");
      } else {
         if(Cache.get("user_" + account) != null){
            Cache.remove("user_" + account);
//            return ok(adminLogin.render("账号异常，请联系管理员！"));
         } else {
            Cache.set("user_" + account, true);
         }
         LoginResult loginResult = loginService.userLogin(account, password, ipaddress);
         if (loginResult == LoginResult.LOGIN_SUCCESS) {
            UserVo userVo = userService.findUserVoByAccount(userLoginData.getAccount());
            response().setCookie("uid", session("USER_ID"));
            response().setCookie("tid", session("TEST_ID"));
            response().setCookie("isOffLine", "1");
            response().setCookie("userName", URLEncoder.encode(userVo.getName(), "UTF-8"));
//            response().setCookie("sex", userVo.getSex());
//            response().setCookie("admissionId", userVo.getTestRoom() + "-" + userVo.getTestNum());
            result = ok(index.render(userVo));
            logger.debug("登陆成功！");
         } else if (loginResult == LoginResult.USER_NONEXISTING || loginResult == LoginResult.WRONG_PASSWORD) {
            result = ok(login.render("账号或密码错误，请重新输入！"));
            logger.debug("账号或密码错误，请重新输入！");
         } else if (loginResult == LoginResult.NO_ACTIVE_USER_TEST) {
            result = ok(login.render("请在考试开始后半小时内登录系统！"));
            logger.debug("请在考试开始后半小时内登录系统！");
         } else if (loginResult == LoginResult.FINISH_TEST) {
            result = ok(login.render("本时段的考试已经考完,您不能在此时间段登陆！"));
            logger.debug("本时段的考试已经考完,您不能在此时间段登陆！");
         } else if (loginResult == LoginResult.NOT_CORRECR_USER){
            result = ok(login.render("您的考试座位错误，请查找正确的座位!"));
            logger.debug("您的考试座位错误，请查找正确的座位!");
         }
      }

      if (result == null) {
         result = ok("God, fatal error!");
      }
      return result;
   }

   /**
    * 登录：测试使用(演示用)
    * @return
    */
   public Result userLoginShow() {
      User user = userService.findById(1);
      user.setLastLogintime(SessionService.curTimestamp());// 记录考生的末次登录时间
      Test test = testService.findById(337);
//      Test test = testService.findById(303);
      String qtypeListJson = testpaperQuestiontypeService.findQtidListByTpid(test.getPid());// 获取考试题型顺序
      sessionService.initUserSessionState(user, test, qtypeListJson);// 初始化session数据
      response().setCookie("uid", "1");
      response().setCookie("tid", test.getId().toString());
      response().setCookie("isOffLine", "1");
      return redirect("/instructions/index/1?tid=" + test.getId().toString());
   }

   public Result adminLogin() {
      AdminLoginData adminLoginData = adminLoginForm.bindFromRequest().get();
      String account = adminLoginData.getAccount();
      String password = adminLoginData.getPassword();
      if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
         return ok(adminLogin.render("账号或密码不能为空！"));
      } else {
         LoginResult loginResult = loginService.adminLogin(account, password);
         if (loginResult == LoginResult.LOGIN_SUCCESS) {
            Integer org_code = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
            return ok(views.html.admin.index.render(org_code));
         } else if (loginResult == LoginResult.ADMIN_NONEXISTIING || loginResult == LoginResult.WRONG_PASSWORD) {
            return ok(adminLogin.render("账号或密码错误，请重新输入！"));
         }
      }
      return ok();
   }

   /** 登出
    * 
    * @pdOid b0c78f93-831d-44ea-8084-138451381704 */
   public Result userlogout() {

      LoginResult loginResult = loginService.userLogout();
      Result result = null;
      if (loginResult == LoginResult.LOGOUT_SUCCESS) {
         response().discardCookie("uid");
         response().discardCookie("tid");
         response().discardCookie("isOffLine");
         response().discardCookie("userName");
         response().discardCookie("testingPage", "/materialMemory/", (String)null, false);
         result = ok(login.render(""));
      } else if (loginResult == LoginResult.USER_NONEXISTING) {
         result = ok("<h1>Hah, account does not exist!<h1>");
      }
      if (result == null) {
         result = ok("<h1>Some mistakes occur!<h1>");
      }
      return result;
   }

   /** 登出
    *
    * @pdOid b0c78f93-831d-44ea-8084-138451381704 */
   public Result adminlogout() {
      LoginResult loginResult = loginService.adminLogout();
      Result result = null;
      if (loginResult == LoginResult.LOGOUT_SUCCESS){
         result = ok(adminLogin.render(""));
      } else if (loginResult == LoginResult.ADMIN_NONEXISTIING){
         result = ok(adminLogin.render(""));
      }
      if (result == null){
         result = ok("<h1>Some mistakes occur!<h1>");
      }
      return result;
   }

   public Result showUserLoginPage() {
      if (loginService.validUserLogin()) {

         String account = sessionService.getSessionItem(SessionService.SessionItemMark.LOGIN_ACCOUNT);
         UserVo userVo = userService.findUserVoByAccount(account);
         return ok(index.render(userVo));
//         return ok(login.render("你已经登录！"));
      } else {
         return ok(login.render(""));
      }
   }

   public Result showAdmissionInfo(){
      String ipaddress = request().remoteAddress();
      User user = loginService.checkUserInfoByIp(ipaddress);
      if (user==null){
         return ok();
      }else{
         return ok(userinfo.render(user));
      }
   }

//   public Result getAdmissionInfo() {
//      Result result = null;
//      UserVo userVo = userService.findUserVoByAccount(SessionService.getSessionItem(SessionService.SessionItemMark.LOGIN_ACCOUNT).toString());
//      if (userVo!=null){
//         result = ok(Json.toJson(userVo));
//      }else{
//         result = ok();
//      }
//      return result;
//   }

   public Result showAdminLoginPage() {
      Result result;
      Integer org_code = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
      if (loginService.validAdminLogin())
         result = ok(views.html.admin.index.render(org_code));
      else
         result = ok(adminLogin.render(""));

      return result;
   }

}