package dao;

import models.Test;
import models.vo.TestVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by mare on 15/7/15.
 */
@Repository
public interface TestDao extends GenericRepository<Test, Integer> {

    @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Query("select test from Test test, UserTest userTest where userTest.uid = :userId and test.id = userTest.tid and test.useStatus = '1'")
    public List<Test> findByUserId(@Param("userId") Integer userId);

    @Query("select  new models.vo.TestVo(t.id,t.pid,t.name,t.startTime,t.useStatus,ao.orgName,ao.id,t.status,t.population,t.turn,t.report) from Test t,AdmissionsOrg ao where t.orgCode = ao.id and ao.id=:orgId and t.useStatus='1' order by t.id desc")
    public List<TestVo> findTestByOrgCode(@Param("orgId") Integer orgId);

    @Query("select  new models.vo.TestVo(t.id,t.pid,t.name,t.startTime,t.useStatus,ao.orgName,ao.id,t.status,t.population,t.turn,t.report) from Test t,AdmissionsOrg ao ,UserTest ut where t.orgCode = ao.id and ut.tid = t.id and ut.uid=:uid  and t.useStatus='1'")
    public Page<TestVo> findTestByUid(@Param("uid") Integer uid,Pageable pageable);
}
