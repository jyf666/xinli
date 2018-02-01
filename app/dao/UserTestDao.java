/***********************************************************************
 * Module:  ScoreDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class ScoreDao
 ***********************************************************************/

package dao;

import models.UserTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

import java.util.List;

/** 考生成绩Dao
 * 
 * @pdOid 58e592e8-8870-48d6-a30d-821ac3e31e22 */
@Repository
public interface UserTestDao extends GenericRepository<UserTest, Integer> {
   /** 保存
    * 
    * @param scoe
    * @pdOid 7ad5e31e-026e-4b6b-ac79-b768a5fa8c65 */
   public UserTest save(UserTest scoe);

   public UserTest findByTidAndUid(Integer tid,Integer uid);

   @Query("select  ut from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode")
   public List<UserTest> findUserTestByTidAndOrgCode(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);
}