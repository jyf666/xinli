package dao;

import models.AdmissionInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by XIAODA on 2015/7/28.
 */
@Repository
public interface AdmissionInfoDao extends GenericRepository<AdmissionInfo, Integer> {

    @Query("select  ai from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode")
    public List<AdmissionInfo> findAdmissionInfoByTidAndOrgCode(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);
}
