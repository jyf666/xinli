package utils;

import utils.enums.DimensionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 2015/9/6.
 */
public class StringUtils {

    /**
     * 字符串转数字 只支持100以内的字符串 包含大小写
     * @param string 字符串
     * @return
     */
    public static Integer stringToNumber(String string){

        string = string.substring(1,string.length()-1);
        Integer hundrend = getHundrendDigit(string);
        Integer ten = getTenDigit(string);
        Integer single = getSingleDigit(string);

        return hundrend * 100 + ten * 10 + single;
    }
    /**
     * 获取百位数
     * @param string 字符串
     * @return
     */
    private static Integer getHundrendDigit(String string){
        Integer hundrendIndex = string.indexOf("百");
        if(hundrendIndex < 0)
            return 0;
        return stringToDigit(string.charAt(hundrendIndex-1));
    }
    /**
     * 获取十位数
     * @param string 字符串
     * @return
     */
    private static Integer getTenDigit(String string){
        Integer tenIndex = string.indexOf("十");
        if(tenIndex < 0)
            return 0;
        if(tenIndex == 0)
            return 1;
        return stringToDigit(string.charAt(tenIndex-1));
    }
    /**
     * 获取个位数
     * @param string 字符串
     * @return
     */
    public static Integer getSingleDigit(String string){
        Integer hundrendIndex = string.indexOf("百");
        Integer tenIndex = string.indexOf("十");
        Integer zeroIndex = string.indexOf("零");
        if(hundrendIndex > 0 && string.length()==2)
            return 0;
        if(tenIndex == string.length()-1)
            return 0;
        if(zeroIndex > 0)
            return stringToDigit(string.charAt(zeroIndex+1));

        return stringToDigit(string.charAt(string.length()-1));

    }

    /**
     * 字符转换成数字
     * @param digitChar 数字字符
     * @return
     */
    private static Integer stringToDigit(char digitChar){
        char[] numbers= "零一二三四五六七八九十".toCharArray();
        for (int i = 0; i < numbers.length; i++) {
            if(digitChar == numbers[i])
                return i;
        }
        return null;
    }



    /**
     * 数字转字符串 只支持1000以内的数字
     * @param number 数字
     * @return
     */
    public static String numberToString(Integer number){
        if(number < 10)
            return "第" + singleDigitToString(number) + "轮";

        if(number < 100){
            return "第" + tenDigitToString(number) + "轮";
        }
        if(number < 1000)
            return "第" + hundrendDigitToString(number) + "轮";
        return null;
    }
    /**
     * 百位数字转换
     * @param number 数字
     * @return
     */
    private static String hundrendDigitToString(Integer number){
        String result = "";
        Integer tenDigit = number%100;
        Integer hundrendDigit = number/100;

        result = digitToString(hundrendDigit) + "百";
        if(tenDigit == 0 && tenDigit/10==0){
            return result;
        }
        if(tenDigit < 10) {
            result += "零" + singleDigitToString(tenDigit);
        }else {
            if(tenDigit/10 == 1){
                result += "一" + tenDigitToString(tenDigit);
            }else{
                result += tenDigitToString(tenDigit);
            }
        }
        return result;
    }

    /**
     * 十位数字转换
     * @param number 数字
     * @return
     */
    private static String tenDigitToString(Integer number){
        String result = "";
        Integer singleDigit = number%10;
        Integer tenDigit = number/10;

        if(tenDigit !=1) {
            result = digitToString(tenDigit) + "十";
        }else{
            result = "十";
        }

        if(singleDigit != 0) {
            result +=singleDigitToString(singleDigit);
        }
        return result;
    }
    /**
     * 个位数字转换
     * @param number 数字
     * @return
     */
    private static String singleDigitToString(Integer number){
        return digitToString(number);
    }

    /**
     * 数字转换
     * @param number 数字
     * @return
     */
    private static String digitToString(Integer number){
        String[] numbers = {"零","一","二","三","四","五","六","七","八","九"};
        return numbers[number];
    }

    public static void main(String[] args) {
        String reg = "^(第[一二三四五六七八九]百轮)|(第[一二三四五六七八九]百[一二三四五六七八九]十[一二三四五六七八九]?轮)|(第[一二三四五六七八九]百零[一二三四五六七八九]轮)" +
                "|(第[二三四五六七八九]?十[一二三四五六七八九]?轮)|(第[一二三四五六七八九]轮)$";
        String a = "第一百二十一轮";
        String b = "第一百二十轮";
        String c = "第十一轮";
        String d = "第一轮";
        String dd = "第一十一轮";
        String ddd = "第二十一轮";
        System.out.println(a.matches(reg));
        System.out.println(b.matches(reg));
        System.out.println(c.matches(reg));
        System.out.println(d.matches(reg));
        System.out.println(dd.matches(reg));
        System.out.println(ddd.matches(reg));
    }
}
