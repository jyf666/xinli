/***********************************************************************
 * Module:  ScoreController.java
 * Author:  XIAODA
 * Purpose: Defines the Class ScoreController
 ***********************************************************************/

package controllers;



import models.Score;
import models.TestpaperQuestiontype;
import models.vo.QuestionTypeNormVo;
import models.vo.ScoreVo;
import models.vo.TestVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.FileUtils;
import utils.SystemConstant;
import views.html.admin.userManage.userScore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/** 考生成绩控制
 * 
 * @pdOid c6e7711c-738f-4ce5-8ba2-a6e3cadcf2b2 */
@org.springframework.stereotype.Controller
public class ScoreController extends Controller {

   @Autowired
   private ScoreService scoreService;
   @Autowired
   private TestpaperQuestiontypeService testpaperQuestiontypeService;
   @Autowired
   private TestService testService;
   @Autowired
   private QuestiontypeNormService questiontypeNormService;
   @Autowired
   private QuestiontypeDimensionNormService questiontypeDimensionNormService;

   /**
    * 展示用户分数页面
    * @param tid 考试id
    * @param uid 用户id
    */
   public Result showScoreView(Integer tid, Integer uid) {
      List<ScoreVo> scores = scoreService.findByUidAndTid(uid, tid);
      List<ScoreVo> scoresPersonality = new ArrayList<>(); //人格分
      List<ScoreVo> familyQuestionNaire = new ArrayList<>(); //家庭分
      List<ScoreVo> otherScores = new ArrayList<>(); //除了人格分,家庭分
      for (int i = 0; i < scores.size(); i++) {
         ScoreVo scoreVo = scores.get(i);
         if(scoreVo.getQtype() == SystemConstant.QUESTION_TYPE_PERSONALITY) {
            scoresPersonality.add(scoreVo);
         }else if(scoreVo.getQtype() == SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE) {
            familyQuestionNaire.add(scoreVo);
         }else {
            otherScores.add(scoreVo);
         }
      }
      return ok(userScore.render(tid, uid,otherScores,scoresPersonality,familyQuestionNaire));
   }


   /**
    * 展示用户分数页面
    * @param orgCode 招生机构id
    */
   public Result calculateScore(Integer orgCode, Integer tpnid) {
      List<TestVo> tests = testService.getOrgTestList(orgCode);
      List<TestpaperQuestiontype> testpaperQuestiontypes = testpaperQuestiontypeService.findByTid(tests.get(0).getId());
      //查看试卷中的题型是否全部拥有常模，长时记忆的记忆部分除外
      List<Integer> tpQtypes = new ArrayList<>();
      for (TestpaperQuestiontype testpaperQuestiontype : testpaperQuestiontypes){
         if (testpaperQuestiontype.getQtid() != SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY && !tpQtypes.contains(testpaperQuestiontype.getQtid())){
            tpQtypes.add(testpaperQuestiontype.getQtid());
         }
      }
      List<Integer> tpnQtypes = new ArrayList<>();
      List<QuestionTypeNormVo> questionTypeNorms = questiontypeNormService.findQuestionTypeNormVoByTpnid(tpnid);
      for (QuestionTypeNormVo questionTypeNormVo: questionTypeNorms){
         tpnQtypes.add(questionTypeNormVo.getQtype());
      }
      List<QuestionTypeNormVo> questionTypDiemensionNorms = questiontypeDimensionNormService.findQuestionTypeNormVoByTpnid(tpnid);
      for (QuestionTypeNormVo questionTypeNormVo: questionTypDiemensionNorms){
         tpnQtypes.add(questionTypeNormVo.getQtype());
      }
      if (!tpnQtypes.containsAll(tpQtypes)) return ok("fail");;

      scoreService.deleteOrgScore(orgCode);
      for (int i = 0; i < tests.size(); i++) {
         scoreService.calculateScore(tests.get(i).getId(), orgCode, testpaperQuestiontypes, questionTypeNorms, questionTypDiemensionNorms);
      }
      scoreService.calculateRanking(tests.get(0).getId(), orgCode);
      Map<String, Double> map = scoreService.calculateZScoreAvarageAndStandard(orgCode);
      for (int i = 0; i < tests.size(); i++) {
         scoreService.calculateTotalScore(tests.get(i).getId(), orgCode, map);
      }
      return ok("success");
   }

   /** 用户分数导出
    *
    * @pdOid d374a5c3-008a-4686-a1e1-4803bb497e74 */
   public Result outputScore(Integer orgCode) {
      try {
         HSSFWorkbook scoreExcel = scoreService.outputScore(orgCode);
         File file = FileUtils.getPublicFile("/assets/download/", "scoreoutput.xls");
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         scoreExcel.write(fileOutputStream);
         fileOutputStream.flush();
         fileOutputStream.close();
         response().setContentType("application/x-download");
         response().setHeader("Content-disposition", "attachment; filename=" + new String(new String("分数列表.xls").getBytes("UTF-8"), "ISO8859-1"));
         return ok(file);
      }catch (Exception e){
         e.printStackTrace();
      }
      return null;
   }

   /** 常模计算分数导出
    *
    * @pdOid d374a5c3-008a-4686-a1e1-4803bb497e74 */
   public Result outputNormalScore(Integer orgCode) {
      try {
         HSSFWorkbook scoreExcel = scoreService.outputNormalScore(orgCode);
         File file = FileUtils.getPublicFile("/assets/download/", "scoreoutput.xls");
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         scoreExcel.write(fileOutputStream);
         fileOutputStream.flush();
         fileOutputStream.close();
         response().setContentType("application/x-download");
         response().setHeader("Content-disposition", "attachment; filename=" + new String(new String("常模分数列表.xls").getBytes("UTF-8"), "ISO8859-1"));
         return ok(file);
      }catch (Exception e){
         e.printStackTrace();
      }
      return null;
   }
}