/***********************************************************************
 * Module:  ScoreDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class ScoreDao
 ***********************************************************************/

package dao;

import models.Score;
import models.vo.ScoreVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import java.util.*;

/** 考生成绩Dao
 * 
 * @pdOid 58e592e8-8870-48d6-a30d-821ac3e31e22 */
@Repository
public interface ScoreDao extends GenericRepository<Score, Integer> {
   
   /** 根据用户id，查询考生成绩
    * 
    * @param uid
    * @pdOid e50b05f7-f649-4194-a1eb-8585f03e99c5 */

   @Query("select new models.vo.ScoreVo(s.qtype,qt.name,qt.introduce,s.originalScore,s.standardScore,s.zScore,s.dimension,s.rank) from Score s,Questiontype qt,Test t,Testpaper tp,TestpaperQuestiontype tq where s.uid=:uid and s.tid=:tid and s.qtype=qt.id and s.tid=t.id and t.pid=tp.id and tp.id=tq.tpid and tq.qtid=qt.id order by tq.seq asc ")
   public List<ScoreVo> findByUidAndTidOrderByQtypeAsc(@Param("uid") Integer uid,@Param("tid") Integer tid);

   @Query("select s from Score s where s.orgCode=:orgCode ")
   public List<Score> findByOrgCode(@Param("orgCode") Integer orgCode);

   @Query("select  new models.vo.ScoreVo(u.id,u.name,qt.id,qt.name,s.standardScore,s.zScore,s.dimension,s.rank) from Score s,User u,Questiontype qt where qt.id=s.qtype and s.orgCode=:orgCode and u.id=s.uid and u.name not like '测试%' order by s.uid,s.qtype,s.dimension asc")
   public List<ScoreVo> findScoreVoByOrgCode(@Param("orgCode") Integer orgCode);


   @Query("select s from Score s where s.orgCode=:orgCode and s.qtype=0")
   public List<Score> findTotalScoreByOrgCode(@Param("orgCode") Integer orgCode);

   @Query("select s from Score s where s.orgCode=:orgCode and s.qtype=:qType order by  CONVERT(s.standardScore,SIGNED) desc ")
   public List<Score> findByOrgCodeAndQtype(@Param("orgCode")Integer orgCode,@Param("qType")Integer qType);

   @Query("select s from Score s where s.orgCode=:orgCode and s.tid=:tid and s.qtype=:qType and s.dimension=:dimension order by  CONVERT(s.standardScore,SIGNED) desc ")
   public List<Score> findByTidAndOrgCodeAndQtypeAndDimension(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode,@Param("qType")Integer qType,@Param("dimension")String dimension);

   @Query("select s from Score s where s.orgCode=:orgCode and s.tid=:tid and s.uid=:uid order by CONVERT(s.standardScore,SIGNED) desc")
   public List<Score> findByUidAndTidAndOrgCode(@Param("uid") Integer uid,@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);

   @Query("select new models.vo.ScoreVo(qt.id,qt.name,qt.introduce,s.standardScore,s.zScore, s.dimension, s.rank) from Score s,Questiontype qt where s.orgCode=:orgCode and s.tid=:tid and s.uid=:uid  and qt.id=s.qtype")
   public List<ScoreVo> findScoreVoByUidAndTidAndOrgCode(@Param("uid") Integer uid,@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);

   @Query("select s from User u,AdmissionInfo ai,Score s,EduExperience edu where ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype and edu.uid=u.id and edu.grade=:grade")
   public List<Score> findByGrade(@Param("grade") String grade,@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select s from User u,AdmissionInfo ai,Score s,EduExperience edu where ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype and edu.uid=u.id and edu.grade=:grade and u.sex=:sex")
   public List<Score> findByGradeAndGender(@Param("grade") String grade,@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode,@Param("sex") String sex);


   @Query("select s from User u,AdmissionInfo ai,Score s,EduExperience edu where ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype and edu.uid=u.id and edu.grade=:grade and edu.class_=:class_")
   public List<Score> findByGradeAndClass(@Param("grade") String grade,@Param("class_") String class_,@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select new models.vo.ScoreVo(s.qtype,qt.name,qt.introduce,s.originalScore,s.standardScore,s.zScore,s.dimension,s.rank) from User u,AdmissionInfo ai,Score s,EduExperience edu,Questiontype qt where qt.id=s.qtype and ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype and edu.uid=u.id and edu.grade=:grade")
   public List<ScoreVo> findScoreVoByGrade(@Param("grade") String grade,@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select new models.vo.ScoreVo(s.qtype,qt.name,qt.introduce,s.originalScore,s.standardScore,s.zScore,s.dimension,s.rank) from User u,AdmissionInfo ai,Score s,EduExperience edu,Questiontype qt where qt.id=s.qtype and ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype and edu.uid=u.id and edu.grade=:grade and edu.class_=:class_")
   public List<ScoreVo> findScoreVoByGradeAndClass(@Param("grade") String grade,@Param("class_") String class_,@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select s from User u,AdmissionInfo ai,Score s,EduExperience edu where ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=0 and edu.uid=u.id and edu.grade=:grade and edu.class_=:class_")
   public List<Score> findByGradeAndClassHaveTotalScore(@Param("grade") String grade,@Param("class_") String class_,@Param("orgCode") Integer orgCode);

   @Query("select s from User u,AdmissionInfo ai,Score s,EduExperience edu where ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=0 and edu.uid=u.id and edu.grade=:grade")
   public List<Score> findByGradeHaveTotalScore(@Param("grade") String grade,@Param("orgCode") Integer orgCode);

   @Query("select new models.vo.ScoreVo(s.qtype,qt.name,qt.introduce,s.originalScore,s.standardScore,s.zScore,s.dimension,s.rank) from User u,AdmissionInfo ai,Score s,Questiontype qt where qt.id=s.qtype and ai.orgCode=:orgCode and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype")
   public List<ScoreVo> findScoreVoByOrgCode(@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select s from Score s where s.qtype=:qtype and s.orgCode=:orgCode")
   public List<Score> findByOrgCode(@Param("qtype") Integer qtype,@Param("orgCode") Integer orgCode);

   @Query("select new models.vo.ScoreVo(s.qtype,qt.name,qt.introduce,s.originalScore,s.standardScore,s.zScore,s.dimension,s.rank) from User u,AdmissionInfo ai,Score s,Questiontype qt,AdmissionsOrg ao where ao.id=s.orgCode and ao.town=:town and qt.id=s.qtype and ai.uid=u.id and u.useStatus='1' and u.useStatus=ai.useStatus and u.id=s.uid and s.qtype=:qtype")
   public List<ScoreVo> findScoreVoByTown(@Param("town") String town,@Param("qtype") Integer qtype);

   @Query("select s from Score s,AdmissionsOrg ao where ao.town=:town and ao.id=s.orgCode and ao.useStatus='1' and s.qtype=:qtype")
   public List<Score> findByTown(@Param("town") String town,@Param("qtype") Integer qtype);

   @Query("select s from Score s where s.uid=:uid order by CONVERT(s.standardScore,SIGNED) asc")
   public List<Score> findByUidOrderByStandardAsc(@Param("uid") Integer uid);


   @Query("SELECT s FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=(SELECT aog.province from AdmissionsOrg aog  where aog.id=:orgCode)and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype!=0")
   public List<Score> findByProvinceAndOrgCode(@Param("orgCode")Integer orgCode);



   @Query("SELECT s FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=(SELECT aog.province from AdmissionsOrg aog  where aog.id=:orgCode)and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
   public List<Score> findTotalByProvience(@Param("orgCode")Integer orgCode);

   @Query("SELECT s FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=:provience and ao.city=:city and ao.town=:town and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
   public List<Score> findTotalByProvienceAndCityAndTown(@Param("provience")String provience,@Param("city")String city, @Param("town")String town);

   @Query("SELECT s FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=:provience and ao.city=:city and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
   public List<Score> findTotalByProvienceAndCity(@Param("provience")String provience,@Param("city")String city);


   @Query("SELECT new models.vo.ScoreVo(u.id,u.name,qt.id,qt.name,s.standardScore,s.dimension,s.rank) FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,Questiontype qt WHERE qt.id=s.qtype and ao.province=(SELECT aog.province from AdmissionsOrg aog  where aog.id=:orgCode)and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype!=0")
   public List<ScoreVo> findByProvience(@Param("orgCode")Integer orgCode);

   @Query("SELECT new models.vo.ScoreVo(u.id,u.name,qt.id,qt.name,s.standardScore,s.dimension,s.rank) FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,Questiontype qt WHERE qt.id=s.qtype and ao.province=:provience and ao.city=:city and ao.town=:town and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype!=0")
   public List<ScoreVo> findByProvienceAndCityAndTown(@Param("provience")String provience,@Param("city")String city, @Param("town")String town);

   @Query("SELECT new models.vo.ScoreVo(u.id,u.name,qt.id,qt.name,s.standardScore,s.dimension,s.rank) FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,Questiontype qt WHERE qt.id=s.qtype and ao.province=:provience and ao.city=:city and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype!=0")
   public List<ScoreVo> findByProvienceAndCity(@Param("provience")String provience,@Param("city")String city);


   @Query("select new models.vo.ScoreVo(qt.id,qt.name,qt.introduce,s.originalScore, s.standardScore,s.zScore, s.dimension, s.rank) from Score s,Questiontype qt where s.orgCode=:orgCode and s.uid=:uid  and qt.id=s.qtype order by s.qtype")
   public List<ScoreVo> findScoreVoByUidAndOrgCode(@Param("uid") Integer uid,@Param("orgCode")Integer orgCode);




   @Query("SELECT s FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=(SELECT aog.province from AdmissionsOrg aog  where aog.id=:orgCode)and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype!=0 group by s.uid")
   public List<Score> findByProvinceAndOrgCodeGroupByUid(@Param("orgCode")Integer orgCode);
}