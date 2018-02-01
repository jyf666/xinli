package service;

import models.Admin;
import models.Test;
import models.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import play.mvc.Http.Session;
import play.mvc.Http.Context;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mare on 15/7/20.
 */
@Service
public class SessionService {

    public enum SessionItemMark {
        IS_OFFLINE,
        QTYPE_LIST_JSON,

        LATEST_ACTIVE_TIME,
        ORG_CODE,
        ORG_NAME,

        USER_TEST_START_TIME,
        LOGIN_NAME,
        USER_ID,
        USER_NAME,// 考生姓名
        TEST_ID,
        TPID,

        LOGIN_ACCOUNT,
        ADMIN_ID;
    }

    public void initUserSessionState(User user, Test activeTest, String qtypeListJson) {
        saveSessionItem(SessionItemMark.IS_OFFLINE, "1");
        saveSessionItem(SessionItemMark.LOGIN_ACCOUNT, user.getAccount());
        saveSessionItem(SessionItemMark.LATEST_ACTIVE_TIME, dateToString(user.getLastLogintime()));
        saveSessionItem(SessionItemMark.USER_TEST_START_TIME, dateToString(activeTest.getStartTime()));
        saveSessionItem(SessionItemMark.USER_ID, String.valueOf(user.getId()));
        saveSessionItem(SessionItemMark.USER_NAME, String.valueOf(user.getName()));
        saveSessionItem(SessionItemMark.TEST_ID, String.valueOf(activeTest.getId()));
        saveSessionItem(SessionItemMark.TPID, String.valueOf(activeTest.getPid()));
        saveSessionItem(SessionItemMark.ORG_CODE,  String.valueOf(activeTest.getOrgCode()));
        saveSessionItem(SessionItemMark.ORG_NAME,  activeTest.getName());
        saveSessionItem(SessionItemMark.QTYPE_LIST_JSON,  qtypeListJson);
    }

    public void clearUserSessionState() {
        session().clear();
//        clearSessionItem(SessionItemMark.IS_OFFLINE);
//        clearSessionItem(SessionItemMark.LOGIN_ACCOUNT);
//        clearSessionItem(SessionItemMark.LATEST_ACTIVE_TIME);
//        clearSessionItem(SessionItemMark.USER_TEST_START_TIME);
//        clearSessionItem(SessionItemMark.USER_ID);
//        clearSessionItem(SessionItemMark.TEST_ID);
//        clearSessionItem(SessionItemMark.TPID);
//        clearSessionItem(SessionItemMark.ORG_CODE);
//        clearSessionItem(SessionItemMark.QTYPE_LIST_JSON);
    }

    public void initAdminSessionState(Admin admin) {
        saveSessionItem(SessionItemMark.LOGIN_NAME, admin.getLoginName());
        saveSessionItem(SessionItemMark.LATEST_ACTIVE_TIME, dateToString(admin.getLastLogintime()));
        saveSessionItem(SessionItemMark.ADMIN_ID, String.valueOf(admin.getId()));
        saveSessionItem(SessionItemMark.ORG_CODE, String.valueOf(admin.getOrgCode()));
        saveSessionItem(SessionItemMark.ORG_NAME, admin.getOrgName());
    }

    public void clearAdminSessionState() {
        clearSessionItem(SessionItemMark.LOGIN_NAME);
        clearSessionItem(SessionItemMark.LATEST_ACTIVE_TIME);
        clearSessionItem(SessionItemMark.ADMIN_ID);
        clearSessionItem(SessionItemMark.ORG_CODE);
    }

    public static Session session() {
        return Context.current().session();
    }

    public static String getSessionItem(SessionItemMark sessionItemMark) {
        return session().get(sessionItemMark.name());
    }

    public static Integer getSessionItemInteger(SessionItemMark sessionItemMark) {
        String value = session().get(sessionItemMark.name());
        if(StringUtils.isNotBlank(value)){
            return Integer.valueOf(value);
        }
        return 0;
    }

    public static void saveSessionItem(SessionItemMark sessionItemMark, String value) {
        session().put(sessionItemMark.name(), value);
    }

    public void clearSessionItem(SessionItemMark sessionItemMark) {
        session().remove(sessionItemMark.name());
    }

    public static Long curTime() { return System.currentTimeMillis(); }

    public static Timestamp curTimestamp() {
        return new Timestamp(curTime());
    }

    public static String dateToString(Date date) {
        return String.valueOf(date.getTime());
    }
}
