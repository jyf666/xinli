/***********************************************************************
 * Module:  QuestionDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestionDao
 ***********************************************************************/

package dao;

import models.Question;
import models.vo.QuestionVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import javax.persistence.QueryHint;
import java.util.*;

/** 题库Dao
 * 
 * @pdOid de08db58-b384-47db-809f-392816bfce8d */
@Repository
public interface QuestionDao extends GenericRepository<Question, String> {
   
   @Query("select new models.vo.QuestionVo(a.id,a.question,a.choices,a.choicesType,a.answer,a.qtype,qt.name,a.subType,a.difficulty,a.version,a.dateCreated) from Question a,Questiontype qt where a.qtype = qt.id")
   public List<QuestionVo> findAllQuestion();

   @Query("select a from Question a,Questiontype qt where a.qtype=:qType and a.qtype = qt.id and qt.useStatus='1'")
   public List<Question> findByQType(@Param("qType") Integer qType);

   @Query("select new models.vo.QuestionVo(a.id,a.question,a.choices,a.choicesType,a.answer,a.qtype,qt.name,a.subType,a.difficulty,a.version,a.dateCreated) from Question a,Questiontype qt,TestpaperQuestion tq,TestpaperQuestiontype tqt where tqt.tpid=:tpid and tqt.qtid= qt.id and a.qtype = qt.id and tq.tpid=:tpid and tq.qid=a.id order by tqt.seq,tq.qid asc ")
   public List<QuestionVo> findByTpid(@Param("tpid") Integer tpid);

   @Query("select a from Question a,Questiontype qt,TestpaperQuestion tq,TestpaperQuestiontype tqt where tqt.tpid=:tpid and tqt.qtid= qt.id and a.qtype = qt.id and tq.tpid=:tpid and tq.qid=a.id order by tqt.seq,tq.qid asc ")
   public List<Question> findQuestionByTpid(@Param("tpid") Integer tpid);

//   @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
//   @Query("select a from Question a, Testpaper b,TestpaperQuestion c where  a.id = c.qid and c.tpid = b.id and a.qtype = :qType and b.id=:tPid and a.subType=:subType")
//   public List<Question> findByTpidAndQTypeAndSubType(@Param("tPid") Integer tPid, @Param("qType") Integer qType,@Param("subType") String subType);

   @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
   @Query("select a from Question a, Testpaper b,TestpaperQuestion c where  a.id = c.qid and c.tpid = b.id and a.qtype = :qType and b.id=:tPid and a.ispractice=:ispractice")
   public List<Question> findByTpidAndQTypeAndIspractice(@Param("tPid") Integer tPid, @Param("qType") Integer qType,@Param("ispractice") String ispractice);

   @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
   @Query("select a from Question a, Testpaper b,TestpaperQuestion c where  a.id = c.qid and c.tpid = b.id and a.qtype = :qType and b.id=:tPid and a.subType=:subType and a.ispractice=:ispractice")
   public List<Question> findAll(@Param("tPid") Integer tPid, @Param("qType") Integer qType,@Param("subType") String subType, @Param("ispractice") String ispractice);

   @Query("select a from Question a, Testpaper b,TestpaperQuestion c where  a.id = c.qid and c.tpid = b.id and a.qtype = :qType and b.id=:tPid and a.ispractice=:ispractice order by  CONVERT(a.difficulty,SIGNED) asc")
   public List<Question> findByTpidAndQTypeAndIspracticeOrderByDifficulty(@Param("tPid") Integer tPid, @Param("qType") Integer qType,@Param("ispractice") String ispractice);
}