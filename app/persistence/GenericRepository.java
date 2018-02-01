package persistence;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by XIAODA on 2015/8/5.
 */
@NoRepositoryBean
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 按条件查询一条数据
     * @author lcd
     * @since 2015-08-05 13:52:23
     */
    T findOne(SearchFilter... filters);

    /**
     * 按条件查询数据，增加排序功能，返回排序后的第一条数据
     * @author lcd
     * @since 2015-08-05 13:52:40
     */
    T findOne(Sort sort, SearchFilter... filters);

    /**
     * 按条件查询所有数据
     * @author lcd
     * @since 2015-08-05 13:46:39
     */
    List<T> findAll(SearchFilter... filters);

    /**
     * 按条件查询所有数据，并增加排序功能
     * @author lcd
     * @since 2015-08-05 13:46:59
     */
    List<T> findAll(Sort sort, SearchFilter... filters);

    /**
     * 按条件查询数据，返回前limit条
     * @author lcd
     * @since 2015-08-05 13:47:24
     */
    List<T> findAll(int limit, SearchFilter... filters);

    /**
     * 按条件查询数据，返回start到start+limit行数据
     * @author lcd
     * @since 2015-08-05 13:48:10
     */
    List<T> findAll(int start, int limit, SearchFilter... filters);

    /**
     * 按条件查询数据，增加排序功能，返回前limit条
     * @author lcd
     * @since 2015-08-05 13:50:34
     */
    List<T> findAll(int limit, Sort sort, SearchFilter... filters);

    /**
     * 按条件查询数据，增加排序功能，返回start到start+limit行数据
     * @author lcd
     * @since 2015-08-05 13:51:12
     */
    List<T> findAll(int start, int limit, Sort sort, SearchFilter... filters);

    /**
     * 功能描述：[根据jpql查询数据]
     *
     * 创建者：李承达
     * 创建时间: May 16, 2014 11:29:27 AM
     * @param jpql
     * @param values
     * @return
     */
    List<T> findAll(String jpql, Object...values);
    List findAll(Class clasz, String jpql, Object...values);
    List<T> findAllBySql(String sql, Object...values);

    /**
     * 按条件查询分页数据
     * @author lcd
     * @since 2015-08-05 13:51:40
     */
    Page<T> findAll(Pageable pageable, SearchFilter... filters);

    /**
     * 按条件查询分页数据
     * @author lcd
     * @since 2014-01-06 12:31:55
     */
    Page<T> findAll(Pageable pageable, String jpql, Object...values);
    Page findAll(Class clasz,Pageable pageable, String jpql, Object...values);
    Page findAllBySql(Pageable pageable, String sql, Object...values);
    /**
     * 按条件查询数据总条数
     * @author lcd
     * @since 2015-08-05 13:51:54
     */
    long count(SearchFilter... filters);
    boolean exists(SearchFilter... filters);

    void delete(SearchFilter...searchFilters);
}
