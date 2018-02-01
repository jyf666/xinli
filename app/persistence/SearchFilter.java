package persistence;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import play.mvc.Http;
import utils.ConvertUtils;

/**
 * Created by XIAODA on 2015/8/5.
 */
public class SearchFilter {

    private static final String VALUE_SEPARATOR = "_AND_";

    public enum Operator {
        EQ, NE, LIKE, GT, LT, GTE, LTE, IN, ISNULL, ISNOTNULL, ISEMPTY
    }

    public String fieldName;
    public Object value;
    public Operator operator;

    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public SearchFilter(String fieldName, Operator operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public static List<SearchFilter> fromSearchParams(Http.Request request, Class<?> clazz) {
        Map<String, String[]> paramsMap =  request.queryString();

        Map<String, String> params = new TreeMap<String, String>();
        for (java.util.Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if(key.startsWith("search_")){
                String unprefixed = key.substring("search_".length());
                if ((value == null) || (value.length == 0)) {
                    // Do nothing, no values found at all.
                } else {
                    params.put(unprefixed, value[0]);
                }
            }
        }
        return fromSearchParams(clazz, params);
    }

    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME，
     * value的格式为value1_AND_value2
     */
    public static List<SearchFilter> fromSearchParams(Class<?> clazz, Map<String, String> searchParams) {
        List<SearchFilter> filters = Lists.newArrayList();

        if(searchParams != null){
            for (Entry<String, String> entry : searchParams.entrySet()) {
                // 过滤掉空值
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNotBlank(value)) {
                    // 拆分operator与filedAttribute
                    String[] names = StringUtils.split(key, "_");
                    if (names.length != 2) {
                        throw new IllegalArgumentException(key + " is not a valid search filter name");
                    }
                    String filedName = names[1];
                    Operator operator = Operator.valueOf(names[0]);

                    SearchFilter searchFilter = null;
                    switch (operator) {
                        case NE:case EQ:case LIKE:case GT:case LT:case GTE:case LTE:case IN:
                            searchFilter = buildSearchFilter(clazz, filedName, operator, value);
                            break;
                        case ISNULL:case ISNOTNULL:case ISEMPTY:
                            searchFilter = new SearchFilter(filedName, operator);
                            break;
                        default:
                            break;
                    }

                    filters.add(searchFilter);
                }
            }
        }
        return filters;
    }


    private static SearchFilter buildSearchFilter(Class<?> clazz, String filedName, Operator operator, String value){
        //将value进行类型转换，获取其真实类型
        Field field = ReflectionUtils.findField(clazz, filedName);
        Object realValue = null;
        if(StringUtils.contains(value, VALUE_SEPARATOR) || operator.equals(Operator.IN)){//说明有多个值
            List<Object> valueList = Lists.newArrayList();
            for(String v : StringUtils.split(value, VALUE_SEPARATOR)){
                valueList.add(ConvertUtils.convertIfNecessary(v, field.getType()));
            }
            realValue = valueList;
        }else{
            realValue = ConvertUtils.convertIfNecessary(value, field.getType());
        }
        return new SearchFilter(filedName, operator, realValue);
    }

    public static SearchFilter eq(String filedName, Object value){
        return new SearchFilter(filedName, Operator.EQ, value);
    }

    public static SearchFilter ne(String filedName, Object value){
        return new SearchFilter(filedName, Operator.NE, value);
    }

    public static SearchFilter like(String filedName, Object value){
        return new SearchFilter(filedName, Operator.LIKE, value);
    }

    public static SearchFilter gt(String filedName, Object value){
        return new SearchFilter(filedName, Operator.GT, value);
    }

    public static SearchFilter gte(String filedName, Object value){
        return new SearchFilter(filedName, Operator.GTE, value);
    }

    public static SearchFilter lt(String filedName, Object value){
        return new SearchFilter(filedName, Operator.LT, value);
    }

    public static SearchFilter lte(String filedName, Object value){
        return new SearchFilter(filedName, Operator.LTE, value);
    }

    public static SearchFilter in(String filedName, Object... values){
        return new SearchFilter(filedName, Operator.IN, values);
    }

    public static SearchFilter isNull(String filedName){
        return new SearchFilter(filedName, Operator.ISNULL);
    }

    public static SearchFilter isNotNull(String filedName){
        return new SearchFilter(filedName, Operator.ISNOTNULL);
    }

    public static SearchFilter isEmpty(String filedName){
        return new SearchFilter(filedName, Operator.ISEMPTY);
    }
}

