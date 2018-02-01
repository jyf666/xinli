package persistence;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;
import play.mvc.Http;

/**
 * Created by XIAODA on 2015/8/5.
 */
public class DynamicSpecifications {

    public static <T> Specification<T> fromRequest(Http.Request request, Class<T> clazz) {
        List<SearchFilter> filters = SearchFilter.fromSearchParams(request, clazz);
        return bySearchFilter(filters);
    }

    public static <T> Specification<T> bySearchFilter(final SearchFilter... filters) {
        return bySearchFilter(Lists.newArrayList(filters));
    }

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters) {
        return new Specification<T>() {
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (filters != null && !filters.isEmpty()) {
                    List<Predicate> predicates = buildPredicates(filters, root, builder);
                    // 将所有条件用 and 联合起来
                    if (predicates.size() > 0) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }
                return builder.conjunction();
            }
        };
    }

    public static <T> List<Predicate> buildPredicates(Collection<SearchFilter> filters, Root<T> root, CriteriaBuilder builder){
        List<Predicate> predicates = Lists.newArrayList();
        for (SearchFilter filter : filters) {
            Predicate predicate = buildPredicate(filter, root, builder);
            if(predicate != null){
                predicates.add(predicate);
            }
        }
        return predicates;
    }

    public static <T> Predicate buildPredicate(SearchFilter filter, Root<T> root, CriteriaBuilder builder){
        // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
        String[] names = StringUtils.split(filter.fieldName, ".");
        Path expression = root.get(names[0]);
        for (int i = 1; i < names.length; i++) {
            expression = expression.get(names[i]);
        }
        // logic operator
        switch (filter.operator) {
            case EQ:
                return builder.equal(expression, filter.value);
            case NE:
                return builder.notEqual(expression, filter.value);
            case LIKE:
                String value = ObjectUtils.toString(filter.value);
                if(StringUtils.isBlank(value) || StringUtils.equals(value, "%")){
                    break;
                }
                return builder.like(expression, value);
            case GT:
                return builder.greaterThan(expression, (Comparable) filter.value);
            case LT:
                return builder.lessThan(expression, (Comparable) filter.value);
            case GTE:
                return builder.greaterThanOrEqualTo(expression, (Comparable) filter.value);
            case LTE:
                return builder.lessThanOrEqualTo(expression, (Comparable) filter.value);
            case ISNULL:
                return builder.isNull(expression);
            case ISNOTNULL:
                return builder.isNotNull(expression);
            case ISEMPTY:
                return builder.isEmpty(expression);
            case IN:
                return buildIn(builder, expression, filter);
        }
        return null;
    }

    private static In buildIn(CriteriaBuilder builder, Path expression, SearchFilter filter){
        In predicate = null;
        if(filter.value instanceof Object[]){
            Object[] values = (Object[])filter.value;
            for(Object value : values){
                if(predicate == null){
                    predicate = builder.in(expression).value(value);
                }else{
                    predicate = predicate.value(value);
                }
            }
        }else if(filter.value instanceof Iterable){
            Iterable iterable = (Iterable)filter.value;
            for(Object value : iterable){
                if(predicate == null){
                    predicate = builder.in(expression).value(value);
                }else{
                    predicate = predicate.value(value);
                }
            }
        }else{
            predicate = builder.in(expression).value(filter.value);
        }
        return predicate;
    }
}

