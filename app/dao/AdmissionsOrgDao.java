/***********************************************************************
 * Module:  AdmissionsOrgDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdmissionsOrgDao
 ***********************************************************************/

package dao;

import models.AdmissionsOrg;
import models.vo.AdmissionsOrgVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;
import org.springframework.data.repository.query.Param;

/** 招生机构Dao
 * 
 * @pdOid f48e19ca-c9f3-42ab-9b4d-be7574b6c548 */
@Repository
public interface AdmissionsOrgDao extends GenericRepository<AdmissionsOrg, Integer> {

   @Query("select new models.vo.AdmissionsOrgVo(ao.id,ao.orgName,a.name,ao.address,ao.description) from Admin a,AdmissionsOrg ao where a.orgCode = ao.id and a.useStatus='1' and a.useStatus=ao.useStatus")
   public Page<AdmissionsOrgVo> findAllAdmissionsOrg(Pageable pageable);
   
   /** 根据招生机构Id查找招生机构
    *
    * @param id
    * @pdOid b6efbf3b-0eee-46d1-ba0b-382c98ceadba */
   @Query("select new models.vo.AdmissionsOrgVo(ao.id,ao.orgName,a.name,ao.address,ao.description) from Admin a,AdmissionsOrg ao where a.orgCode = ao.id and a.useStatus='1' and a.useStatus=ao.useStatus and ao.id=:id" )
   public AdmissionsOrgVo findVoById(Integer id);

   @Query("select new models.vo.AdmissionsOrgVo(ao.id,a.id,ao.orgName,ao.property,a.name,a.duty,a.email,a.phone) from Admin a,AdmissionsOrg ao where a.orgCode = ao.id and a.useStatus='2' and a.useStatus=ao.useStatus")
   public Page<AdmissionsOrgVo> findOrgByReg(Pageable pageable);

}