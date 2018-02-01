/***********************************************************************
 * Module:  AnswerInfoDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class AnswerInfoDao
 ***********************************************************************/

package dao;

import models.Answer;
import models.vo.AnswerVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import java.util.List;


/** 答题信息Dao
 * 
 * @pdOid 017b3437-85ef-4abb-beb5-685cb389d1a3 */
@Repository
public interface AnswerDao extends GenericRepository<Answer, Integer> {

   /** 根据测试题类型查询考生答题信息
    * 
    * @param qType 题型
    * @pdOid ad004191-dc11-4d14-b470-fe94a8618e05 */
   public Answer findByQtype(String qType);

   @Query("select new models.vo.AnswerVo(q.id,q.question,q.answer,a.answer,q.qtype) from Answer a ,Question q where a.qid = q.id and a.tid=:tid and a.uid =:uid")
   public List<AnswerVo> findByTidAndUid(@Param("tid") Integer tid,@Param("uid") Integer uid);

   @Query("select new models.vo.AnswerVo(q.id,q.question,q.answer,a.answer,q.qtype,q.dimension,a.clickNum,a.rightNum,a.clickTime) from Answer a ,Question q where a.qid = q.id and a.tid=:tid and a.uid =:uid and q.qtype=:qtype order by q.id asc")
   public List<AnswerVo> findByUidAndTidAndQtype(@Param("uid") Integer uid,@Param("tid") Integer tid,@Param("qtype")Integer qtype);

}