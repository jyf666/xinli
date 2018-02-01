package utils;

import play.cache.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/7/27.
 */
public class SystemConstant {

    public static long startup_date; //系统启动时间

    /** 邮箱常量 */
    public static final String EMAIL_SMTP_ADDRESS = "smtp.exmail.qq.com"; //邮件发送服务器
    public static final String EMAIL_SEND_FROM_USER = "ceping@fcbrains.com";//发件人地址
    public static final String EMAIL_SUBJECT = "审核通过！！";//邮件主题
    public static final String EMAIL_LOGIN_ACCOUNT = "ceping@fcbrains.com";//邮箱登录帐号
    public static final String EMAIL_LOGIN_PWD = "putian123";//邮件登录密码

    public static final String PUBLIC_DIE_PREFIX = "http://localhost";

    /** 分页大小 */
    public final static Integer PAGESIZE = 15;

    /** 考试题类型 */
    public final static Integer QUESTION_TYPE_MATERIAL_MEMORY = 1;// 材料记忆
    public final static Integer QUESTION_TYPE_SYMBOLIC_OPERATION = 2;// 符号运算
    public final static Integer QUESTION_TYPE_SPATIAL_MEMORY = 3;// 顺序记忆
    public final static Integer QUESTION_TYPE_GRAPF_SEARCH = 4;// 图案搜索
    public final static Integer QUESTION_TYPE_SHAPE_LINKING = 5;// 图形连线
    public final static Integer QUESTION_TYPE_PARAGRAPH_REASONING = 6;// 段落推理
    public final static Integer QUESTION_TYPE_MATERIAL_EXTRACT = 7;// 材料回忆
    public final static Integer QUESTION_TYPE_ANALOGIC_REASONING = 8;// 类比推理
    public final static Integer QUESTION_TYPE_MATRIX_REASONING = 9;// 矩阵推理
    public final static Integer QUESTION_TYPE_PERSONALITY = 10;// 人格
    public final static Integer QUESTION_TYPE_FAMILY_QUESTIONNAIRE = 11;// 家庭问卷
    public final static Integer QUESTION_TYPE_SPACE_ROTATION = 12;// 空间旋转
    public final static Integer QUESTION_TYPE_EMOTION_RECOGNITION = 13;// 情绪识别
    public final static Integer QUESTION_TYPE_EMOTION_UNDERSTANDING = 14;// 情绪理解
    public final static Integer QUESTION_TYPE_REMOTE_ASSOCIATION = 15;// 远距离联想
    public final static Integer QUESTION_TYPE_CRITICALTHINKING_ABILITY = 16;// 批判性思维-能力
    public final static Integer QUESTION_TYPE_CRITICALTHINKING_TENDENCY = 17;// 批判性思维-倾向

    /** 选项存储类型 */
    public final static Integer CHOICE_TYPE_JSON = 1;// JSON类型
    public final static Integer CHOICE_TYPE_STRING = 2;// String类型
    public final static Integer CHOICE_TYPE_JSONLIKE = 3;// 类JSON类型

    /** 选项数组 */
    public final static List<String> CHOICE_LIST = new ArrayList() {{
        add("A");
        add("B");
        add("C");
        add("D");
        add("E");
        add("F");
        add("G");
        add("H");
        add("I");
        add("J");
        add("K");
        add("L");
        add("M");
        add("N");
        add("O");
        add("P");
        add("Q");
        add("R");
        add("S");
        add("T");
        add("U");
        add("V");
        add("W");
        add("X");
        add("Y");
        add("Z");
    }};

    public static Map QUESTION_TYPE_TIME_MAP = new HashMap();//

    /** 考试题 是否为练习题 */
    public final static String QUESTION_ISPRACTICE_YES = "1";// 练习题
    public final static String QUESTION_ISPRACTICE_NO = "0";// 考试题

    public final static List<String> INSTRUCTIONS_HEAD_CACHE = new ArrayList() {
        {
            add("/assets/javascripts/jquery-1.9.0.min.js");
            add("/assets/javascripts/question/question.js");
            add("https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js");
            add("https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js");
            add("/assets/stylesheets/bootstrap.min.css");
            add("/assets/stylesheets/exam/instructions.css");
        }
    };

    public final static List<String> PRACTICE_HEAD_CACHE = new ArrayList() {
        {
            add("/assets/javascripts/jquery-1.9.0.min.js");
            add("/assets/javascripts/question/question.js");
            add("https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js");
            add("https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js");
            add("/assets/stylesheets/bootstrap.min.css");
            add("/assets/stylesheets/exam/question.css");
            add("/assets/stylesheets/exam/instructions.css");
        }
    };

    public final static List<String> TEST_HEAD_CACHE = new ArrayList() {
        {
            add("/assets/javascripts/jquery-1.9.0.min.js");
            add("/assets/javascripts/layer.min.js");
            add("/assets/javascripts/question/question.js");
            add("/assets/javascripts/exam/time.js");
            add("https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js");
            add("https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js");
            add("/assets/stylesheets/exam/question.css");
            add("/assets/stylesheets/exam/time.css");
            add("/assets/stylesheets/bootstrap.min.css");
            add("/assets/javascripts/skin/layer.css");
        }
    };

    public final static Map EMOTION_MAP = new HashMap() {{
        put("a", "悲伤");
        put("b", "愤怒");
        put("c", "惊讶");
        put("d", "恐惧");
        put("e", "快乐");
        put("f", "厌恶");
        put("g", "兴奋");
        put("h", "爱");
        put("i", "不满");
        put("j", "担心");
        put("k", "担忧");
        put("l", "烦恼");
        put("m", "尴尬");
        put("n", "感动");
        put("o", "感激");
        put("p", "害羞");
        put("q", "好奇");
        put("r", "后悔");
        put("s", "怀念");
        put("t", "怀疑");
        put("u", "激动");
        put("v", "嫉妒");
        put("w", "焦急");
        put("x", "焦虑");
        put("y", "紧张");
        put("z", "敬佩");
        put("ab", "沮丧");
        put("bc", "绝望");
        put("cd", "困惑");
        put("de", "满足");
        put("ef", "内疚");
        put("fg", "期待");
        put("gh", "轻蔑");
        put("hi", "轻松");
        put("ij", "庆幸");
        put("jk", "失望");
        put("lm", "同情");
        put("mn", "委屈");
        put("no", "温暖");
        put("op", "无奈");
        put("pq", "欣慰");
        put("qr", "羞愧");
        put("rs", "遗憾");
        put("st", "疑惑");
        put("tu", "自豪");
        put("uv", "自责");
    }};

    /** 考生是否完成指定的考试 */
    public final static String USER_TEST_ISOVER_NO = "0";// 未完成
    public final static String USER_TEST_ISOVER_YES = "1";// 已完成

    public final static String SUCCESS = "1";// 成功
    public final static String FAIL = "0";// 失败

    public final static String USE_STATUS_YES = "1";// 正在使用
    public final static String USE_STATUS_NO = "0";// 不再使用
}
