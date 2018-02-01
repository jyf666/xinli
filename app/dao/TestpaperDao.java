/***********************************************************************
 * Module:  TestPaperDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class TestPaperDao
 ***********************************************************************/

package dao;

import java.util.*;
import models.Testpaper;
import models.vo.TestpaperVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/** 试卷Dao
 * 
 * @pdOid c4b03062-c1a5-4f87-9f13-579036b92152 */
@Repository
public interface TestpaperDao extends GenericRepository<Testpaper, Integer> {


   @Query("FROM Testpaper WHERE useStatus = 1 AND (orgCode = :orgCode OR isReference = 1)")
   public List<Testpaper> findAllByOrgCodeOrReference(@Param("orgCode") Integer orgCode);

   @Query("FROM Testpaper WHERE useStatus = 1 AND (orgCode in ( :orgCodes ) OR isReference = 1)")
   public List<Testpaper> findAllByOrgCodesOrReference(@Param("orgCodes") List<Integer> orgCodes);

   /**
    * 查询所有的试卷
    * @param pageable
    * @return
    */
   @Query("select a from Testpaper a where a.useStatus='1'")
   public Page<Testpaper> findAllTestpaper(Pageable pageable);

   @Query("select new models.vo.TestpaperVo(q.qtype,count(1)) from Testpaper tp,TestpaperQuestion tq,Question q where tp.id=:tpid and tp.id=tq.tpid and q.id=tq.qid and q.subType='01'GROUP BY q.qtype")
   public List<TestpaperVo> findTestpaperPerQuestiontypeQuestionNum(@Param("tpid")Integer tpid);

}