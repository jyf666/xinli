/***********************************************************************
 * Module:  UserDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class UserDao
 ***********************************************************************/

package dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import persistence.GenericRepository;
import models.User;
import models.vo.UserVo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 考生Dao
 * 
 * @pdOid 8f4f491f-6e4c-463b-b61c-b1e52d53a2ad */
@Repository
public interface UserDao extends GenericRepository<User, Integer> {

    @Query("select us from User us, AdmissionInfo ai where  us.id = ai.uid and ai.useStatus = '1' and us.useStatus=ai.useStatus and us.account = :account")
    public User findByAccount(@Param("account")String account);

    @Query("select  us from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut,Score s where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode and s.uid=us.id group by us.account")
    public List<User> findUserByTidAndOrgCode(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);


    @Query("select u from User u,AdmissionInfo ai,EduExperience edu,Score s where u.useStatus='1' and ai.useStatus=u.useStatus and ai.orgCode=:orgCode and ai.uid=u.id and u.id=edu.uid and edu.class_=:class_ and edu.grade=:grade and s.uid=u.id group by u.account")
    public List<User> findUserByOrgCodeAndGradeAndClass(@Param("orgCode")Integer orgCode,@Param("grade")String grade,@Param("class_")String class_);


    @Query("select u from User u,AdmissionInfo ai,EduExperience edu,Score s where u.useStatus='1' and ai.useStatus=u.useStatus and ai.orgCode=:orgCode and ai.uid=u.id and u.id=edu.uid and edu.grade=:grade and s.uid=u.id group by u.account")
    public List<User> findUserByOrgCodeAndGrade(@Param("orgCode")Integer orgCode,@Param("grade")String grade);

    public User findById(Integer id);


    @Query("select new models.vo.UserVo(us.id,us.sex,us.name,us.idCard,ao.orgName,us.birthday,edu.grade,edu.class_) from User us,EduExperience edu,AdmissionsOrg ao where edu.schoolCode=ao.id and  us.id = edu.uid and us.useStatus='1' and us.id=:uid ")
    public UserVo findUserVoByUidHaveGradeAndClass(@Param("uid") Integer uid);


    @Query("select new models.vo.UserVo(us.name,us.sex,us.birthday,us.email,ai.testRoom,ai.testNum, us.idCard) from User us, AdmissionInfo ai where  us.id = ai.uid and ai.useStatus = '1' and us.account = :account")
    public UserVo findUserVoByAccount(@Param("account") String account);

    @Query("select new models.vo.UserVo(us.account,us.name,us.sex,us.birthday,us.address,us.phone,us.email,us.idCard,ai.testRoom,ai.testNum,ao.orgName) from User us, AdmissionsOrg ao, AdmissionInfo ai where  us.id = ai.uid and ai.useStatus = '1' and us.useStatus=ai.useStatus  and us.id = :uid and ai.orgCode = ao.id")
    public UserVo findUserVoByUid(@Param("uid") Integer uid);

    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName,us.answerCommit) from UserView us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode")
    public Page<UserVo> findUserVoByTidAndOrgCode(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode,Pageable pageable);
    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut,Answer an where an.uid=us.id and us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode group by us.account")
    public List<UserVo> findUserVoByTidAndOrgCode(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);

    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode and ut.isover=1")
    public List<UserVo> findUserVoByTidAndOrgCodeAndIsOver(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);


    @Query("select  new models.vo.UserVo(us.name,edu.grade,edu.class_,us.account,us.password,us.email,t.turn,us.idCard,us.birthday,us.sex,us.phone,ai.testRoom,ai.testNum,us.address) from User us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut,Test t,EduExperience edu where edu.uid=us.id and us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode and t.id =ut.tid")
    public List<UserVo> findUserVo(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode);


    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) from User us, AdmissionsOrg ao,AdmissionInfo ai  where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId")
    public Page<UserVo> findUserVoByOrgCode(@Param("orgId")Integer orgId,Pageable pageable);
    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) from User us, AdmissionsOrg ao,AdmissionInfo ai  where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId")
    public List<UserVo> findUserVoByOrgCode(@Param("orgId")Integer orgId);

    @Query("select  new models.vo.UserVo(us.id,us.account,edu.class_,us.name,us.sex,us.birthday,edu.grade) from User us, AdmissionsOrg ao,AdmissionInfo ai,EduExperience edu  where edu.uid=us.id and us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId and not exists (select s.uid from Score s where s.orgCode=:orgId and s.uid=us.id and s.uid is not null group by s.uid) and us.name not like '测试%' group by us.id")
    public List<UserVo> findAbsentUserVoByOrgCode(@Param("orgId")Integer orgId);


    @Query("select  new models.vo.UserVo(us.id,us.account,edu.class_,us.name,us.sex,us.birthday,edu.grade) from User us, AdmissionsOrg ao,AdmissionInfo ai,EduExperience edu  where edu.uid=us.id and us.id in (select s.uid from Score s where s.tid=:tid )and us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId group by us.account")
    public List<UserVo> findUserVoByOrgCodeAndTidHaveScore(@Param("orgId")Integer orgId, @Param("tid")Integer tid);

    @Query("select  new models.vo.UserVo(us.id,us.account,edu.class_,us.name,us.sex,us.birthday,edu.grade) from User us, AdmissionsOrg ao,AdmissionInfo ai,EduExperience edu  where edu.uid=us.id and us.id in (select s.uid from Score s where s.orgCode=:orgId )and us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId and us.name not like '测试%' group by us.account")
    public List<UserVo> findUserVoByOrgCodeHaveScore(@Param("orgId")Integer orgId);



    @Query("select  us from User us, AdmissionsOrg ao,AdmissionInfo ai  where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId")
    public List<User> findUserByOrgCode(@Param("orgId")Integer orgId);

    @Query("select  us from User us, AdmissionsOrg ao,AdmissionInfo ai,Score s  where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ao.id=:orgId and s.uid=us.id group by us.account")
    public List<User> findUserByOrgCodeWithScore(@Param("orgId")Integer orgId);


    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) from User us, AdmissionsOrg ao,AdmissionInfo ai where us.id=ai.uid and us.useStatus='2'and ai.orgCode=ao.id")
    public Page<UserVo> findUserByReg(Pageable pageable);

    @Query("select  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName,us.answerCommit) from UserView us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut where us.id=ai.uid and us.useStatus='1'and ai.orgCode=ao.id and us.useStatus=ai.useStatus and ut.tid=:tid and ut.uid=us.id and ao.id=:orgCode and us.answerCommit=0")
    public Page<UserVo> findUserVoByTidAndOrgCodeAndAnswerCommit(@Param("tid")Integer tid,@Param("orgCode")Integer orgCode,Pageable pageable);


    @Query("SELECT u FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s WHERE ao.province=(SELECT aog.province from AdmissionsOrg aog  where aog.id=:orgCode)and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.uid=u.id group by u.account")
    public List<User> findUserByProvinceAndOrgCode(@Param("orgCode")Integer orgCode);



    @Query("SELECT  new models.vo.UserVo(u.id,u.sex,u.account,u.email,u.name,edu.grade,edu.class_)  FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,EduExperience edu WHERE edu.uid=u.id and ao.province=:provience and ao.city=:city and ao.town=:town and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
    public List<UserVo> findUserByProvinceAndCityAndTown(@Param("provience")String provience,@Param("city")String city, @Param("town")String town);

    @Query("SELECT  new models.vo.UserVo(u.id,u.sex,u.account,u.email,u.name,edu.grade,edu.class_)  FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,EduExperience edu WHERE edu.uid=u.id and ao.province=:provience and ao.city=:city  and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
    public List<UserVo> findUserByProvinceAndCity(@Param("provience")String provience,@Param("city")String city);

    @Query("SELECT  new models.vo.UserVo(u.id,u.sex,u.account,u.email,u.name,edu.grade,edu.class_)  FROM User u,AdmissionsOrg ao,AdmissionInfo ai,Score s,EduExperience edu WHERE edu.uid=u.id and ao.province=:provience and u.id=s.uid and u.id = ai.uid and ao.id=ai.orgCode and u.useStatus='1' and u.useStatus=ai.useStatus and s.qtype=0")
    public List<UserVo> findUserByProvince(@Param("provience")String provience);
}