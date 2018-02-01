package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XIAODA on 2015/7/16.
 */
public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String dateFormatPattern = "yyyy-MM-dd";
    public static final String dateTimeFormatPattern = "yyyy-MM-dd HH:mm:ss";
    public static final String dateTimeFormatPattern2 = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 根据出生日期，计算年龄
     * @param birthDate 出生日期
     * @return 年龄
     * @author lcd
     */
    public static int getAge(Date birthDate){
        Period period = new Period(new DateTime(birthDate), new DateTime());
        return period.getYears();
    }

    /**
     * 根据出生日期，计算年龄
     * @param birthDate 出生日期
     * @return 年龄
     * @author lcd
     */
    public static int getAge(String birthDate){
        Period period = new Period(new DateTime(birthDate), new DateTime());
        return period.getYears();
    }

    /**
     * 以yyyy-MM-dd格式，格式化日期
     * @param date
     * @return
     * @author lcd
     */
    public static String formatDate(Date date){
        if(date != null){
            return DateFormatUtils.format(date, dateFormatPattern);
        }
        return "";
    }

    /**
     * 以yyyy-MM-dd格式，格式化日期
     * @param date
     * @return
     * @author lcd
     */
    public static String formatDate(Calendar date){
        if(date != null){
            return DateFormatUtils.format(date, dateFormatPattern);
        }
        return "";
    }

    /**
     * 将字符串yyyy-MM-dd,yyyy-MM-dd HH:mm:ss转为Calendar类型
     */
    public static Calendar parseCalendar(String dateStr) {
        if(StringUtils.isNotBlank(dateStr)){
            try {
                java.util.Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, new String[]{dateFormatPattern, dateTimeFormatPattern});
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 将字符串yyyy-MM-dd,yyyy-MM-dd HH:mm:ss转为Date类型
     */
    public static Date parseDate(String dateStr) {
        if(StringUtils.isNotBlank(dateStr)){
            try {
                return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, new String[]{dateFormatPattern, dateTimeFormatPattern, "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.S"});
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 将字符串yyyy-MM-dd HH:mm:ss.S转为精确到毫秒的Date类型
     */
    public static Date parseDateTime(String dateStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取当前季度
     * @author lcd
     */
    public static int getQuarter(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return month / 3 + 1;
    }

    /**
     * 获取当前年的第一天
     * @author lcd
     */
    public static Date getCurrYearFirst(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取当前年的最后一天
     * @author lcd
     */
    public static Date getCurrYearLast(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }

    /**
     * 通过年龄算出生年
     *
     * @param age
     * @param month
     *            0 表示1月
     * @param day
     *            1 表示1日
     * @return
     */
    public static java.util.Date getAge(Integer age, int month, int day) {
        try {
            Calendar current = Calendar.getInstance();
            current.add(Calendar.YEAR, -1 * age);
            current.set(Calendar.MONTH, month);
            current.set(Calendar.DATE, day);
            return current.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取所传日期的时间戳
     * @param date
     * @return
     */
    public static long getCurrentDay(Date date) {
        try {
            String currentDay = formatDate(date);
            return parseDate(currentDay).getTime()/1000;
        } catch (Exception e) {
            return 0;
        }
    }
}

