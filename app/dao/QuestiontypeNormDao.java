/***********************************************************************
 * Module:  QuestiontypeNormDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestiontypeNormDao
 ***********************************************************************/

package dao;

import models.QuestionTypeNorm;
import models.vo.QuestionTypeNormVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

import java.util.*;

/**
 * 题型常模Dao
 */
@Repository
public interface QuestiontypeNormDao extends GenericRepository<QuestionTypeNorm, Integer> {

    @Query("select new models.vo.QuestionTypeNormVo(qtn.id,qtn.tpnid,qtn.QType,qt.name,qtn.avg,qtn.stdev) from Questiontype qt,QuestionTypeNorm qtn where qtn.tpnid=:tpnid and qtn.QType=qt.id")
    public List<QuestionTypeNormVo> findQuestionTypeNormVoByTpnid(@Param("tpnid") Integer tpnid);
}