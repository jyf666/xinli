package utils;

/**
 * Created by XIAODA on 2015/8/5.
 */
public class ConvertUtils {

    public static Object convertIfNecessary(Object value, Class targetType){
        Class sourceType = value == null ? null : value.getClass();
        if(sourceType == targetType){
            return value;
        }
        return org.apache.commons.beanutils.ConvertUtils.convert(value, targetType);
    }
}
