/***********************************************************************
 * Module:  ScoreDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class ScoreDao
 ***********************************************************************/

package dao;

import models.TestpaperQuestion;
import persistence.GenericRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 考试题目DAO
 * 
 * @pdOid 58e592e8-8870-48d6-a30d-821ac3e31e22 */
@Repository
public interface TestpaperQuestionDao extends GenericRepository<TestpaperQuestion, Integer> {
   /** 保存考试成绩
    * 
    * @param testpaperQuestion
    * @pdOid 7ad5e31e-026e-4b6b-ac79-b768a5fa8c65 */
   public TestpaperQuestion save(TestpaperQuestion testpaperQuestion);

   public TestpaperQuestion findByTpidAndQid(Integer tpid,String qid);

   public List<TestpaperQuestion> findByTpid(Integer tpid);
}