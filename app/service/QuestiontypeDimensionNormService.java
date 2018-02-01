/***********************************************************************
 * Module:  QuestiontypeNormService.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestiontypeNormService
 ***********************************************************************/

package service;

import dao.QuestiontypeDimensionNormDao;
import dao.QuestiontypeNormDao;
import models.QuestionTypeDimensionNorm;
import models.QuestionTypeNorm;
import models.vo.QuestionTypeNormVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 题型常模相关业务
 * 
 * @pdOid 3e9d8247-3148-42ee-b0b2-c6fc3dfdd492 */
@Service
public class QuestiontypeDimensionNormService {

   @Autowired
   private QuestiontypeDimensionNormDao questiontypeDimensionNormDao;

   /**
    * 获取常模的各个题型的具体数据
    * @param tpnid
    * @return
    */
   public List<QuestionTypeNormVo> getNormData(Integer tpnid){
      return questiontypeDimensionNormDao.findQuestionTypeNormVoByTpnid(tpnid);
   }

   /**
    * 获取常模的各个题型的具体数据
    * @param tpnid
    * @return
    */
   public List<QuestionTypeDimensionNorm> findByTpnid(Integer tpnid){
      return questiontypeDimensionNormDao.findByTpnid(tpnid);
   }


   public  List<QuestionTypeNormVo> findQuestionTypeNormVoByTpnid(Integer tpnid){
      return questiontypeDimensionNormDao.findQuestionTypeNormVoByTpnid(tpnid);
   }
}