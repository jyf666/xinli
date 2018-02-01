/***********************************************************************
 * Module:  QuestiontypeNormDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestiontypeNormDao
 ***********************************************************************/

package dao;

import models.QuestionTypeDimensionNorm;
import models.QuestionTypeNorm;
import models.vo.QuestionTypeNormVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

import java.util.List;

/** 题型常模Dao
 * 
 * @pdOid 48d60b5e-05ea-4a88-9c31-7ed71ad24da4 */
@Repository
public interface QuestiontypeDimensionNormDao extends GenericRepository<QuestionTypeNorm, Integer> {

    @Query("select new models.vo.QuestionTypeNormVo(qtn.id,qtn.tpnid,qtn.QType,qt.name,qtn.dimension,qtn.avg,qtn.stdev) from Questiontype qt,QuestionTypeDimensionNorm qtn where qtn.tpnid=:tpnid and qtn.QType=qt.id")
    public List<QuestionTypeNormVo> findQuestionTypeNormVoByTpnid(@Param("tpnid") Integer tpnid);


    @Query("select new models.vo.QuestionTypeNormVo(qtn.id,qtn.tpnid,qtn.QType,qt.name,qtn.dimension,qtn.avg,qtn.stdev) from Questiontype qt,QuestionTypeDimensionNorm qtn,TestpaperNorm tpn where tpn.tpid=:tpid and qtn.tpnid=tpn.id and qtn.QType=qt.id")
    public List<QuestionTypeNormVo> findQuestionTypeNormVoByTpid(@Param("tpid") Integer tpid);

    @Query("select qtn from Questiontype qt,QuestionTypeDimensionNorm qtn where qtn.tpnid=:tpnid and qtn.QType=qt.id")
    public List<QuestionTypeDimensionNorm> findByTpnid(@Param("tpnid")Integer tpnid);
}