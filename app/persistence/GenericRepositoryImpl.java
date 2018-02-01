package persistence;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;
import utils.Reflections;


/**
 * Created by XIAODA on 2015/8/5.
 */
@NoRepositoryBean
public class GenericRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID> {

    private final Class<T> domainClass;
    private final EntityManager entityManager;

    public GenericRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);

        this.domainClass = domainClass;
        this.entityManager = entityManager;
    }

    public T findOne(SearchFilter... filters) {
        return findOne(null, filters);
    }

    public T findOne(Sort sort, SearchFilter... filters) {
        List<T> resultList = findAll(0, 1, sort, filters);
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

    public List<T> findAll(SearchFilter... filters) {
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        return findAll(spec);
    }

    public List<T> findAll(int start, int limit, SearchFilter... filters) {
        return findAll(start, limit, null, filters);
    }

    public List<T> findAll(int limit, SearchFilter... filters) {
        return findAll(0, limit, filters);
    }

    public List<T> findAll(int limit, Sort sort, SearchFilter... filters) {
        return findAll(0, limit, sort, filters);
    }

    public List<T> findAll(Sort sort, SearchFilter... filters) {
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        return findAll(spec, sort);
    }

    public List<T> findAll(int start, int limit, Sort sort, SearchFilter... filters) {
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);

        Root<T> root = applySpecificationToCriteria(spec, query);
        query.select(root);


        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(start);
        typedQuery.setMaxResults(limit);

        return typedQuery.getResultList();
    }

    public List<T> findAll(String jpql, Object...values) {
        TypedQuery<T> typedQuery = entityManager.createQuery(jpql, domainClass);
        for(int i = 0; i < values.length; i ++){
            typedQuery.setParameter(i + 1, values[i]);
        }
        return typedQuery.getResultList();
    }

    public List findAll(Class clasz, String jpql, Object...values) {
        TypedQuery typedQuery = entityManager.createQuery(jpql, clasz);
        for(int i = 0; i < values.length; i ++){
            typedQuery.setParameter(i + 1, values[i]);
        }
        return typedQuery.getResultList();
    }

    public List<T> findAllBySql(String sql, Object...values) {
        Query query = entityManager.createNativeQuery(sql, domainClass);
        for(int i = 0; i < values.length; i ++){
            query.setParameter(i + 1, values[i]);
        }
        return query.getResultList();
    }

    public Page<T> findAll(Pageable pageable, SearchFilter... filters) {
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        return findAll(spec, pageable);
    }

    public Page<T> findAll(Pageable pageable, String jpql, Object...values) {
        TypedQuery<T> typedQuery = entityManager.createQuery(jpql, domainClass);
        typedQuery.setFirstResult(pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        for(int i = 0; i < values.length; i ++){
            typedQuery.setParameter(i + 1, values[i]);
        }

        Long total = executeCountQuery(jpql, values);
        List<T> content = total > pageable.getOffset() ? typedQuery.getResultList() : Collections.<T> emptyList();
        return new PageImpl<T>(content, pageable, total);
    }

    public Page findAllBySql(Pageable pageable, String sql, Object...values) {
        Query query = entityManager.createNativeQuery(sql);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        for(int i = 0; i < values.length; i ++){
            query.setParameter(i + 1, values[i]);
        }

        Long total = executeCountQueryBySql(sql, values);
        List content = total > pageable.getOffset() ? query.getResultList() : Collections.emptyList();
        return new PageImpl(content, pageable, total);
    }

    public Page findAll(Class clasz, Pageable pageable, String jpql, Object...values) {
        TypedQuery typedQuery = entityManager.createQuery(jpql, clasz);
        typedQuery.setFirstResult(pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        for(int i = 0; i < values.length; i ++){
            typedQuery.setParameter(i + 1, values[i]);
        }

        Long total = executeCountQuery(jpql, values);
        List<T> content = total > pageable.getOffset() ? typedQuery.getResultList() : Collections.<T> emptyList();
        return new PageImpl<T>(content, pageable, total);
    }

    private Long executeCountQuery(String jpql, Object...values){
        String fromHql = jpql;
        //select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "FROM");
        fromHql = StringUtils.substringBefore(fromHql, "ORDER BY");

        String countHql = "select count(*) " + fromHql;

        TypedQuery<Long> query = entityManager.createQuery(countHql, Long.class);
        for(int i = 0; i < values.length; i ++){
            query.setParameter(i + 1, values[i]);
        }

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private Long executeCountQueryBySql(String sql, Object...values){
        String countSql = "select count(*) from (" + sql + ") cnt";

        Query query = entityManager.createNativeQuery(countSql);
        for(int i = 0; i < values.length; i ++){
            query.setParameter(i + 1, values[i]);
        }

        List<BigInteger> totals = query.getResultList();
        Long total = 0L;

        for (BigInteger element : totals) {
            total += element == null ? 0 : element.longValue();
        }

        return total;
    }

    public long count(SearchFilter... filters) {
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        return count(spec);
    }

    @SuppressWarnings("unchecked")
    public <F> F max(String fieldName, SearchFilter... filters) {
        Class<F> fieldClazz = (Class<F>) Reflections.getAccessibleField(domainClass, fieldName).getType();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<F> query = builder.createQuery(fieldClazz);
        Root<T> root = query.from(domainClass);
        Path expression = root.get(fieldName);
        query.select(builder.max(expression));

        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        query.where(spec.toPredicate(root, query, builder));

        return entityManager.createQuery(query).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public <F> F min(String fieldName, SearchFilter... filters) {
        Class<F> fieldClazz = (Class<F>)Reflections.getAccessibleField(domainClass, fieldName).getType();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<F> query = builder.createQuery(fieldClazz);
        Root<T> root = query.from(domainClass);
        Path expression = root.get(fieldName);
        query.select(builder.min(expression));

        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters);
        query.where(spec.toPredicate(root, query, builder));

        return entityManager.createQuery(query).getSingleResult();
    }

    public boolean exists(SearchFilter... filters) {
        return count(filters) > 0;
    }

    public void delete(SearchFilter... searchFilters) {
        List<T> entities = findAll(searchFilters);
        if (entities != null && !entities.isEmpty()) {
            delete(entities);
        }
    }

    /**
     * Applies the given {@link Specification} to the given {@link CriteriaQuery}.
     *
     * @param spec can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {
        Assert.notNull(query);
        Root<T> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

}