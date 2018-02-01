/***********************************************************************
 * Module:  SchoolDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class SchoolDao
 ***********************************************************************/

package dao;

import models.EduExperience;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** 考生学校历史信息Dao
 * 
 * @pdOid 61e227ad-0306-4349-823b-124ea25f8292 */
@Repository
public interface EduExperienceDao extends GenericRepository<EduExperience, Integer> {

   /** 根据所传id查找考生学校历史信息
    * 
    * @param schoolId
    * @pdOid 971784f0-e63d-4b5b-bd6e-5e049e0b0a0b */
   public EduExperience findById(String schoolId);

   /** 根据用户id和使用状态查找学校信息
    * 
    * @param userId 
    * @param useStatus
    * @pdOid 91dfdaca-1ce1-4112-987b-2383042b7321 */
   public EduExperience findByUidAndUseStatus(Integer userId, String useStatus);

   @Query("select edu from EduExperience edu where edu.schoolCode=:orgCode group by edu.grade order by edu.grade asc")
   public List<EduExperience>  findGradeByOrgCode(@Param("orgCode") String orgCode);


   @Query("select edu from EduExperience edu where edu.schoolCode=:orgCode and edu.grade=:grade group by edu.class_ order by edu.class_ asc")
   public List<EduExperience>  findClassesByOrgCodeAndGrade(@Param("orgCode") String orgCode,@Param("grade") String grade);

}