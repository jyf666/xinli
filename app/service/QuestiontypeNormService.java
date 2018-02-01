/***********************************************************************
 * Module:  QuestiontypeNormService.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestiontypeNormService
 ***********************************************************************/

package service;

import java.util.*;

import dao.QuestiontypeNormDao;
import models.QuestionTypeNorm;
import models.vo.QuestionTypeNormVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;

/**
 * 题型常模相关业务
 */
@Service
public class QuestiontypeNormService {

   @Autowired
   private QuestiontypeNormDao questiontypeNormDao;

   /**
    * 保存题型常模
    * @param questionTypeNorms
    * @return
    */
   @Transactional
   public List<QuestionTypeNorm> save(List<QuestionTypeNorm> questionTypeNorms) {
      return questiontypeNormDao.save(questionTypeNorms);
   }

   /**
    * 获取常模的各个题型的具体数据
    * @param tpnid
    * @return
    */
   public List<QuestionTypeNormVo> getNormData(Integer tpnid){
      return questiontypeNormDao.findQuestionTypeNormVoByTpnid(tpnid);
   }

   /**
    * 获取常模的各个题型的具体数据
    * @param tpnid
    * @return
    */
   public List<QuestionTypeNorm> findByTpnid(Integer tpnid){
      return questiontypeNormDao.findAll(SearchFilter.eq("tpnid", tpnid));
   }

   public List<QuestionTypeNormVo>  findQuestionTypeNormVoByTpnid(Integer tpnid){
      return questiontypeNormDao.findQuestionTypeNormVoByTpnid(tpnid);
   }
}