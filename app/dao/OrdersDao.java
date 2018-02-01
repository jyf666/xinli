package dao;

import models.Orders;
import models.vo.OrdersVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by mare on 15/7/15.
 */
@Repository
public interface OrdersDao extends GenericRepository<Orders, Integer> {

    @Query("select a from Orders a where a.orgCode=:orgCode and a.status='1' order by a.id desc")
    public Page<Orders> findByOrgCode(@Param("orgCode")Integer orgCode,Pageable pageable);

}
