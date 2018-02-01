/***********************************************************************
 * Module:  AnswerReportDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class AnswerReportDao
 ***********************************************************************/

package dao;

import models.AnswerReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import java.util.*;

/** 考生答题记录Dao
 * 
 * @pdOid 131d7c44-9927-45e0-933d-56d44ec9d7a6 */
@Repository
public interface AnswerReportDao extends GenericRepository<AnswerReport, Integer> {

   /** 根据考生id，测试题类型，提交时间，查询考生答题信息
    * 
    * @param uid 
    * @param qType 题型
    * @param commitTime
    * @pdOid 83c68682-624f-48a0-a6bd-56240d5c15ca */
   public AnswerReport findByUidAndQtypeAndCommitTime(Integer uid, Integer qType, Date commitTime);

   /**
    * 根据考生id，考试id,测试题类型,查询提交时间为null的考生答题信息，并按照开始时间倒序返回
    * @param uid
    * @param tid
    * @param qType
    * @return
    */
   public List<AnswerReport> findByUidAndTidAndQtypeAndCommitTimeIsNullOrderByStartTimeDesc(Integer uid, Integer tid, Integer qType);

   /**
    * 根据考生id，考试id,查询提交时间不为null的考生答题信息，并按照开始时间倒序返回
    * @param uid
    * @param tid
    * @return
    */
   @Query("select qtype from AnswerReport where uid = :uid and tid = :tid and commitTime is not null order by commitTime desc")
   public List<Integer> findQtypeByUidAndTidAndCommitTimeIsNotNullOrderByCommitTimeDesc(@Param("uid")Integer uid, @Param("tid")Integer tid);

}