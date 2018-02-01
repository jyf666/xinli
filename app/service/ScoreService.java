/***********************************************************************
 * Module:  ScoreService.java
 * Author:  XIAODA
 * Purpose: Defines the Class ScoreService
 ***********************************************************************/

package service;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;
import play.libs.Json;
import utils.ExcelUtils;
import utils.SystemConstant;
import utils.enums.CriticalThinkingTendencyDimensionEnum;
import utils.enums.DimensionEnum;
import utils.enums.FamilyDimensionEnum;

/** 考生成绩相关业务
 * 
 * @pdOid 7ab2b2ff-4559-4904-8ec7-aa344268552d */
@Service
public class ScoreService {

   @Autowired
   private ScoreDao scoreDao;
   @Autowired
   private AnswerDao answerDao;
   @Autowired
   private TestDao testDao;
   @Autowired
   private UserDao userDao;
   @Autowired
   private UserTestDao userTestDao;
   @Autowired
   private TestpaperQuestiontypeDao testpaperQuestiontypeDao;
   @Autowired
   private QuestiontypeNormDao questiontypeNormDao;
   @Autowired
   private QuestiontypeDimensionNormDao questiontypeDimensionNormDao;
   @Autowired
   private QuestionDao questionDao;
   @Autowired
   private QuestionTypeDao questionTypeDao;

   private static final Integer ORIGIN_SCORE_INDICATOR = 1;
   private static final Integer STANDARD_SCORE_INDICATOR = 2;

   /** 根据用户id，考试id查询考生成绩
    * 
    * @param uid
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   public List<ScoreVo> findByUidAndTid(Integer uid,Integer tid) {
      return scoreDao.findByUidAndTidOrderByQtypeAsc(uid, tid);
   }



   /** 删除某招生机构考生的成绩
    *
    * @param orgCode 招生机构编码
    * @pdOid 63eacc27-e898-48e1-bb7c-ff1b340f8c98 */
   public void deleteOrgScore(Integer orgCode) {
      List<Score> scores = scoreDao.findByOrgCode(orgCode);
      scoreDao.delete(scores);
   }

   /**
    * 根据招生机构id，获取考生成绩
    * @param orgCode
    * @return
    */
   public List<ScoreVo> getOrgScore(Integer orgCode) {
      return scoreDao.findScoreVoByOrgCode(orgCode);
   }

   /**
    * 根据用户id，考试id计算成绩
    * @param orgCode 招生机构id
    * @param tid     考试id
    */
   public boolean calculateScore(Integer tid, Integer orgCode, List<TestpaperQuestiontype> testpaperQuestiontypes, List<QuestionTypeNormVo> questionTypeNorms, List<QuestionTypeNormVo> questionTypDiemensionNorms) {

      List<UserTest> userTests = userTestDao.findAll(SearchFilter.eq("tid", tid));
      Test test = testDao.findOne(tid);
      for (UserTest userTest : userTests) {
         if (answerDao.findAll(SearchFilter.eq("uid", userTest.getUid())).size() != 0) {
            for (TestpaperQuestiontype testpaperQuestiontype : testpaperQuestiontypes) {
               this.calculateOriginalScoreAndStandardScore(orgCode, userTest.getUid(), testpaperQuestiontype.getQtid(), test, questionTypeNorms, questionTypDiemensionNorms);
            }
         }
      }
      return true;
   }


   /**
    * 计算z分数的平均数和标准差
    * @param orgCode
    * @return
    */
   public Map<String,Double> calculateZScoreAvarageAndStandard(Integer orgCode){
      Map<String,Double> map = new HashMap<>();

      List<User> proUser =  userDao.findUserByProvinceAndOrgCode(orgCode);
      List<Score> scores = scoreDao.findByProvinceAndOrgCode(orgCode);


      List<Double> userTotalZScore =  new ArrayList<>();
      double totalUserZScore = 0;
      for (int i = 0; i < proUser.size(); i++) {
         User user = proUser.get(i);
         double totalZScore = 0;
         for (int j = 0; j < scores.size(); j++) {
            Score score = scores.get(j);
            if(!score.getQtype().equals(SystemConstant.QUESTION_TYPE_PERSONALITY) && !score.getQtype().equals(SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE) && score.getQtype() !=0) {
               if (user.getId().equals(score.getUid())) {
                  totalZScore += Double.parseDouble(score.getzScore());
                  System.out.println(user.getId()+"--:"+score.getQtype() + "---:"+score.getzScore());
               }
            }
         }
         totalUserZScore += totalZScore;
         userTotalZScore.add(totalZScore);
      }

      double avageZScore = totalUserZScore/proUser.size();

      double standardZScore = 0;
      for (int i = 0; i < userTotalZScore.size(); i++) {
         double totalZScore = userTotalZScore.get(i);
         standardZScore += (totalZScore-avageZScore) * (totalZScore-avageZScore);
      }

      double SD = Math.sqrt(standardZScore/proUser.size());

      map.put("avarageZScore",Double.valueOf(avageZScore));
      map.put("SDZScore", Double.valueOf(SD));

      return map;

   }

   /** 根据tid，orgCode计算用户总成绩
    * @param orgCode 招生机构id
    * @param tid 考试id
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   public void calculateTotalScore(Integer tid,Integer orgCode,Map<String,Double> zScore) {
      List<TestpaperQuestiontype> testpaperQuestiontypes = testpaperQuestiontypeDao.findByTid(tid);
      double avarageZscore= zScore.get("avarageZScore");
      double SDZscore = zScore.get("SDZScore");
      List<Score> totalScores = new ArrayList<>();
      List<UserVo> userVos = userDao.findUserVoByTidAndOrgCodeAndIsOver(tid, orgCode);

      for (int i = 0; i < userVos.size(); i++) {
         UserVo userVo = userVos.get(i);
         List<Score> scores =  scoreDao.findByUidOrderByStandardAsc(userVo.getUid());
         double useTotalZscore = 0;
         if(scores.size() ==0)
            continue;
         for (int j = 0; j < scores.size(); j++) {
            Score score = scores.get(j);
            if(score.getQtype()!=SystemConstant.QUESTION_TYPE_PERSONALITY &&
                    score.getQtype()!=SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE && score.getQtype() !=0)
               useTotalZscore += Double.parseDouble(score.getzScore());
         }

         Integer totalScore = (int)(100 + 15 *((useTotalZscore-avarageZscore)/SDZscore) + 0.5);
         Score score = new Score();
         score.setOrgCode(orgCode);
         score.setOriginalScore("");
         score.setDateCreated(new Date());
         score.setDateUpdate(new Date());
         score.setDimension("");
         score.setQtype(0);
         score.setQuality("");
         score.setRank("");
         score.setTid(tid);
         score.setUid(userVo.getUid());
         score.setzScore("");
         score.setStandardScore(totalScore.toString());
         totalScores.add(score);
      }
      scoreDao.save(totalScores);
   }

   /** 计算排名
    * @param orgCode 招生机构id
    * @param tid 考试id
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   public void calculateRanking(Integer tid,Integer orgCode) {
      List<TestpaperQuestiontype> testpaperQuestiontypes = testpaperQuestiontypeDao.findByTid(tid);
      List<Score> scoreWithRank = new ArrayList<>();
      for (int j = 0; j < testpaperQuestiontypes.size(); j++) {
         Integer rank = 1;
         TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypes.get(j);
         List<Score> scores = scoreDao.findByOrgCodeAndQtype(orgCode, testpaperQuestiontype.getQtid());
         if(testpaperQuestiontype.getQtid() == SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE || testpaperQuestiontype.getQtid() ==SystemConstant.QUESTION_TYPE_PERSONALITY){
            continue;
         }
         for (int i = 0; i < scores.size(); i++) {
            Score  score = scores.get(i);
            if(i != scores.size()-1){
               Score scoreNext = scores.get(i+1);
               score.setRank(Integer.toString(rank));
               if(!score.getStandardScore() .equals(scoreNext.getStandardScore())){
                  rank++;
               }
            }else {
               score.setRank(Integer.toString(rank));
            }
            scoreWithRank.add(score);
         }

      }
      scoreDao.save(scoreWithRank);
   }

   /** 根据用户id，考试id计算成绩
    * @param uid 用户id
    * @param qtype 题型id
    * @param test 对应考试 和获取常模
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   public Score calculateOriginalScoreAndStandardScore(Integer orgCode,Integer uid,Integer qtype,Test test, List<QuestionTypeNormVo> questionTypeNorms, List<QuestionTypeNormVo> questionTypDiemensionNorms) {
      Score score = new Score();
      for(int i =0;i<questionTypeNorms.size();i++){
         QuestionTypeNormVo questionTypeNormVo = questionTypeNorms.get(i);
         Integer qtid = questionTypeNormVo.getQtype();
         double avrage = Double.parseDouble(questionTypeNormVo.getAvg()); //平均数
         double standard = Double.parseDouble(questionTypeNormVo.getStdev());//标准差
         //材料回忆算分
         if(qtype == SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT && qtid == SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT ) {
            score = this.calculateMaterialExtractScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //顺序记忆算分
         if(qtype == SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY && qtid == SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY  ){
            score =  this.calculateSpatialMemoryScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //图形运算算分
         if(qtype == SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION && qtid == SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION ){
            score = this.calculateSymbolicOpearcionScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //图形连线算分
         if(qtype == SystemConstant.QUESTION_TYPE_SHAPE_LINKING && qtid == SystemConstant.QUESTION_TYPE_SHAPE_LINKING ) {
            score = this.calculateShapeLinkingScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //图案搜索算分
         if(qtype == SystemConstant.QUESTION_TYPE_GRAPF_SEARCH && qtid == SystemConstant.QUESTION_TYPE_GRAPF_SEARCH ){
            score =  this.calculateGrapfSearchScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //段落推理
         if(qtype == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING && qtid == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING){
            score =  this.calculateParagraphReasoningScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //类比推理
         if(qtype == SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING && qtid == SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING){
            score =  this.calculateAnalogicReasoningScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //矩阵推理
         if(qtype == SystemConstant.QUESTION_TYPE_MATRIX_REASONING && qtid == SystemConstant.QUESTION_TYPE_MATRIX_REASONING){
            score =  this.calculateMatrixReasoningScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //空间旋转
         if(qtype == SystemConstant.QUESTION_TYPE_SPACE_ROTATION  && qtid == SystemConstant.QUESTION_TYPE_SPACE_ROTATION){
            score = this.calculateSpaceRotationScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //情绪识别
         if(qtype == SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION  && qtid == SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION){
            score = this.calculateEmotionRecognitionScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //情绪理解
         if(qtype == SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING  && qtid == SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING){
            score = this.calculateEmotionUnderstandingScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //远距离联想测试
         if(qtype == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION && qtid == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION ) {
            score = this.calculateRemoteAssociationScore(orgCode, uid, test.getId(), avrage, standard);
         }
         //批判性思维能力
         if(qtype == SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY && qtid == SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY ) {
            score = this.calculateCrticalThinkingAbilityScore(orgCode, uid, test.getId(), avrage, standard);
         }
      }

      if(questionTypDiemensionNorms.size()!=0){
         //人格问卷
         if(qtype == SystemConstant.QUESTION_TYPE_PERSONALITY )
            score = this.calculatePersonalityScore(orgCode, uid, test.getId(),questionTypDiemensionNorms);
         //家庭环境
         if(qtype == SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE)
            score = this.calculateFamilyQuestionaireScore(orgCode, uid, test.getId(), questionTypDiemensionNorms);
         //批判性思维倾向
         if(qtype == SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY) {
            score = this.calculateCrticalThinkingTendencyScore(orgCode, uid, test.getId(), questionTypDiemensionNorms);
         }
      }

      return  score;
   }
   /** 根据用户id，考试id计算批判性思维倾向
    * @param tid 考试id
    * @param uid 用户id
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateCrticalThinkingTendencyScore(Integer orgCode,Integer uid,Integer tid, List<QuestionTypeNormVo> questionTypDiemensionNorms){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
      if (answers.size() != questionDao.findByQType(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY).size())
         return null;
      Map<Integer, Integer> dimensionScore = new HashMap<>();
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         Integer dimension = answer.getDimension();
         if(dimensionScore.containsKey(dimension))
            dimensionScore.put(dimension, dimensionScore.get(dimension) + Integer.parseInt(answer.getUserAnswer()));
         else
            dimensionScore.put(dimension, Integer.parseInt(answer.getUserAnswer()));
      }
      for (Map.Entry<Integer, Integer> entry: dimensionScore.entrySet()){
         Score score = new Score();
         score.setDateCreated(new Date());
         score.setOriginalScore(entry.getValue().toString());
         for (QuestionTypeNormVo questionTypeNormVo: questionTypDiemensionNorms){
            if (questionTypeNormVo.getDimension().equals(entry.getKey().toString())) {
               double zscore = this.caculateZScore(entry.getValue(), Double.parseDouble(questionTypeNormVo.getAvg()), Double.parseDouble(questionTypeNormVo.getStdev()));
               score.setzScore(String.valueOf(zscore));
               score.setStandardScore(String.valueOf((int)(100 + 15 *zscore + 0.5)));
            }
         }
         score.setQtype(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
         score.setQuality("");
         score.setTid(tid);
         score.setUid(uid);
         score.setDimension(entry.getKey().toString());
         score.setOrgCode(orgCode);
         score.setDateUpdate(new Date());
         scoreDao.save(score);
      }

      return null;
   }

   /** 根据用户id，考试id远距离联想成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateRemoteAssociationScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         String[] answerArray = answer.getAnswer().split(",");
         for (int j = 0; j < answerArray.length; j++) {
            if(answerArray[j].equals(answer.getUserAnswer())){
               originalScore++;
            }
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 *zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString((int)(originalScore+0.5)));
      score.setQtype(SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }
   /** 根据用户id，考试id计算情绪识别成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateEmotionRecognitionScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
      double originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         double[] userAnswer = this.dealUserAnswer(answer.getAnswer(), answer.getUserAnswer());
         double distance = this.caculateDistance(this.getPointsBySort(answer.getAnswer()), userAnswer);
         double maxDistance = this.caculateEmotionRecognitionMaxDistance(0, 5, this.getPointsBySort(answer.getAnswer()));
         originalScore += 1-distance/maxDistance;
      }
      standardScore = (int)(100 + 15 *(float)(originalScore-avarage)/standard + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(String.format("%.2f", originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf((float) (originalScore - avarage) / standard));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算情绪理解成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateEmotionUnderstandingScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
      double originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         double[] userAnswer = this.dealUserAnswer(answer.getAnswer(), answer.getUserAnswer());
         double distance = this.caculateDistance(this.getPointsBySort(answer.getAnswer()), userAnswer);
         double maxDistance = this.caculateEmotionUderstandingMaxDistance(this.getPointsBySort(answer.getAnswer()));
         originalScore += 1-distance/maxDistance;
      }
      standardScore = (int)(100 + 15 *(float)(originalScore-avarage)/standard + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(String.format("%.2f", originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf((float)(originalScore-avarage)/standard));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }
   /** 根据用户id，考试id计算批判性思维能力
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateCrticalThinkingAbilityScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算记忆提取成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateMaterialExtractScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算段落推理成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateParagraphReasoningScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算类比推理成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateAnalogicReasoningScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算矩阵推理成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateMatrixReasoningScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算顺序记忆成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateSpatialMemoryScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      Score score = new Score();

      for(int i = answers.size()-1 ;i >=0; i--){
         AnswerVo answerVo = answers.get(i);
         originalScore = 0;
         if((answerVo.getQuestion()+";").equals(answerVo.getUserAnswer())){
            String[] questions = answerVo.getQuestion().split(";");
            String[] userAnswers = answerVo.getUserAnswer().substring(0, answerVo.getUserAnswer().length() - 1).split(";");
            for (int j = 0; j< userAnswers.length; j++) {
               if (questions[j].equals(userAnswers[j])) {
                  originalScore++;
               }
            }
            break;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }
   /** 根据用户id，考试id计算符号运算成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateSymbolicOpearcionScore(Integer orgCode, Integer uid, Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();
      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);
      return score;
   }
   /** 根据用户id，考试id计算图案搜索成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateGrapfSearchScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid,tid,SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         ObjectNode objectNode = (ObjectNode)(Json.parse(answer.getUserAnswer()));
         if(answer.getAnswer().equals(objectNode.findPath("imgNum").asText())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }
   /** 根据用户id，考试id计算形状连线成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateShapeLinkingScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid,tid,SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      Integer lastRightNum = 0;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         try {
            lastRightNum += answer.getRightNum();
         } catch (Exception e){
            e.printStackTrace();
            continue;
         }
      }
      originalScore = 15 * originalScore + lastRightNum;
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 *zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }


   /** 根据用户id，考试id计算人格成绩
    * @param tid 考试id
    * @param uid 用户id
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculatePersonalityScore(Integer orgCode,Integer uid,Integer tid,List<QuestionTypeNormVo> questionTypeDimensionNorms){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid,tid,SystemConstant.QUESTION_TYPE_PERSONALITY);
//      if (questionDao.findAll(SearchFilter.eq("qType", SystemConstant.QUESTION_TYPE_PERSONALITY), SearchFilter.eq("isPractice","0")).size() != answers.size())
//         return null;
      for(int j=0;j<questionTypeDimensionNorms.size();j++){
         QuestionTypeNormVo questionTypeNormVo = questionTypeDimensionNorms.get(j);
         if(questionTypeNormVo.getQtype() == SystemConstant.QUESTION_TYPE_PERSONALITY) {
            double avarage = Double.parseDouble(questionTypeNormVo.getAvg());
            double standard = Double.parseDouble(questionTypeNormVo.getStdev());
            Integer originalScore = 0;
            Integer standardScore = 0;
            for (int i = 0; i < answers.size(); i++) {
               AnswerVo answerVo = answers.get(i);
               if (questionTypeNormVo.getDimension().equals(answerVo.getQuestion()) && answerVo.getAnswer().equals(answerVo.getUserAnswer())) {
                  originalScore++;
               }
            }
            double zScore = this.caculateZScore(originalScore, avarage, standard);
            standardScore = (int) (100 + 15 * zScore + 0.5);

            Score score = new Score();
            score.setDateCreated(new Date());
            score.setOriginalScore(Integer.toString(originalScore));
            score.setQtype(SystemConstant.QUESTION_TYPE_PERSONALITY);
            score.setQuality("");
            score.setStandardScore(Integer.toString(standardScore));
            score.setTid(tid);
            score.setUid(uid);
            score.setzScore(String.valueOf(""));
            score.setDimension(questionTypeNormVo.getDimension());
            score.setOrgCode(orgCode);
            score.setDateUpdate(new Date());
            scoreDao.save(score);
         }
      }
      return null;
   }

   /** 根据用户id，考试id计算空间旋转成绩
    * @param tid 考试id
    * @param uid 用户id
    * @param avarage 平均数
    * @param standard 标准差
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateSpaceRotationScore(Integer orgCode,Integer uid,Integer tid,double avarage,double standard){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid,tid,SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
      Integer originalScore = 0 ;
      Integer standardScore = 0 ;
      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answer = answers.get(i);
         if(answer.getAnswer().equals(answer.getUserAnswer())){
            originalScore++;
         }
      }
      double zScore = this.caculateZScore(originalScore,avarage,standard);
      standardScore = (int)(100 + 15 * zScore + 0.5);
      Score score = new Score();

      score.setDateCreated(new Date());
      score.setOriginalScore(Integer.toString(originalScore));
      score.setQtype(SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
      score.setQuality("");
      score.setStandardScore(Integer.toString(standardScore));
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(zScore));
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      scoreDao.save(score);

      return score;
   }

   /** 根据用户id，考试id计算人格成绩
    * @param tid 考试id
    * @param uid 用户id
    * @pdOid cc36b8f2-fa8e-46e7-968f-8dcb50036f61 */
   @Transactional
   private Score calculateFamilyQuestionaireScore(Integer orgCode,Integer uid,Integer tid,List<QuestionTypeNormVo> questionTypeDimensionNorms){
      List<AnswerVo> answers = answerDao.findByUidAndTidAndQtype(uid, tid, SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE);
      Integer fatherCareOriginalScore = 0 ;
      Integer motherCareOriginalScore = 0 ;
      Integer fatherOverProtectionOriginalScore = 0 ;
      Integer motherOverProtectionOriginalScore = 0 ;
      boolean fatherIndicator = false;
      boolean motherIndicator = false;
      String fatherEducationLevel = "";
      String motherEducationLevel = "";
      String familyLevel = "";
      Integer[] care = {1,5,6,11,12,17,2,4,14,16,18,24};//前6个为正向加分 即A 算0分 B 1分 C 2分 D 3分 后6个为反向 即A 算3分 B 2分 C 1分 D 0分
      Integer[] overProtection = {8,9,10,13,19,20,23,3,7,15,21,22,25}; //前7个为正向加分 即A 算0分 B 1分 C 2分 D 3分 后6个为反向 即A 算3分 B 2分 C 1分 D 0分


      for (int i = 0; i < answers.size(); i++) {
         AnswerVo answerVo = answers.get(i);
         for (int j = 0; j < care.length; j++) {
            if(Integer.parseInt(answerVo.getAnswer()) == care[j]){
               ObjectNode userAnswerJson =  (ObjectNode)Json.parse(answerVo.getUserAnswer());
               if(userAnswerJson.has("father")){
                  fatherIndicator = true;
                  if(j<6) {
                     if (userAnswerJson.findPath("father").asText().equals("A")) {
                        fatherCareOriginalScore += 0;
                     } else if (userAnswerJson.findPath("father").asText().equals("B")) {
                        fatherCareOriginalScore += 1;
                     } else if (userAnswerJson.findPath("father").asText().equals("C")) {
                        fatherCareOriginalScore += 2;
                     } else if (userAnswerJson.findPath("father").asText().equals("D")) {
                        fatherCareOriginalScore += 3;
                     }
                  }else{
                     if (userAnswerJson.findPath("father").asText().equals("A")) {
                        fatherCareOriginalScore += 3;
                     } else if (userAnswerJson.findPath("father").asText().equals("B")) {
                        fatherCareOriginalScore += 2;
                     } else if (userAnswerJson.findPath("father").asText().equals("C")) {
                        fatherCareOriginalScore += 1;
                     } else if (userAnswerJson.findPath("father").asText().equals("D")) {
                        fatherCareOriginalScore += 0;
                     }
                  }
               }
               if(userAnswerJson.has("mother")){
                  motherIndicator = true;
                  if(j<6) {
                     if (userAnswerJson.findPath("mother").asText().equals("A")) {
                        motherCareOriginalScore += 0;
                     } else if (userAnswerJson.findPath("mother").asText().equals("B")) {
                        motherCareOriginalScore += 1;
                     } else if (userAnswerJson.findPath("mother").asText().equals("C")) {
                        motherCareOriginalScore += 2;
                     } else if (userAnswerJson.findPath("mother").asText().equals("D")) {
                        motherCareOriginalScore += 3;
                     }
                  }else{
                     if (userAnswerJson.findPath("mother").asText().equals("A")) {
                        motherCareOriginalScore += 3;
                     } else if (userAnswerJson.findPath("mother").asText().equals("B")) {
                        motherCareOriginalScore += 2;
                     } else if (userAnswerJson.findPath("mother").asText().equals("C")) {
                        motherCareOriginalScore += 1;
                     } else if (userAnswerJson.findPath("mother").asText().equals("D")) {
                        motherCareOriginalScore += 0;
                     }
                  }
               }
            }
         }
         for (int t = 0; t < overProtection.length; t++) {
            if (Integer.parseInt(answerVo.getAnswer()) == overProtection[t]) {
               ObjectNode userAnswerJson = (ObjectNode) Json.parse(answerVo.getUserAnswer());
               if (userAnswerJson.has("father")) {
                  fatherIndicator = true;
                  if(t<7) {
                     if (userAnswerJson.findPath("father").asText().equals("A")) {
                        fatherOverProtectionOriginalScore += 0;
                     } else if (userAnswerJson.findPath("father").asText().equals("B")) {
                        fatherOverProtectionOriginalScore += 1;
                     } else if (userAnswerJson.findPath("father").asText().equals("C")) {
                        fatherOverProtectionOriginalScore += 2;
                     } else if (userAnswerJson.findPath("father").asText().equals("D")) {
                        fatherOverProtectionOriginalScore += 3;
                     }
                  }else{
                     if (userAnswerJson.findPath("father").asText().equals("A")) {
                        fatherOverProtectionOriginalScore += 3;
                     } else if (userAnswerJson.findPath("father").asText().equals("B")) {
                        fatherOverProtectionOriginalScore += 2;
                     } else if (userAnswerJson.findPath("father").asText().equals("C")) {
                        fatherOverProtectionOriginalScore += 1;
                     } else if (userAnswerJson.findPath("father").asText().equals("D")) {
                        fatherOverProtectionOriginalScore += 0;
                     }
                  }
               }
               if (userAnswerJson.has("mother")) {
                  motherIndicator = true;
                  if(t<7) {
                     if (userAnswerJson.findPath("mother").asText().equals("A")) {
                        motherOverProtectionOriginalScore += 0;
                     } else if (userAnswerJson.findPath("mother").asText().equals("B")) {
                        motherOverProtectionOriginalScore += 1;
                     } else if (userAnswerJson.findPath("mother").asText().equals("C")) {
                        motherOverProtectionOriginalScore += 2;
                     } else if (userAnswerJson.findPath("mother").asText().equals("D")) {
                        motherOverProtectionOriginalScore += 3;
                     }
                  }else{
                     if (userAnswerJson.findPath("mother").asText().equals("A")) {
                        motherOverProtectionOriginalScore += 3;
                     } else if (userAnswerJson.findPath("mother").asText().equals("B")) {
                        motherOverProtectionOriginalScore += 2;
                     } else if (userAnswerJson.findPath("mother").asText().equals("C")) {
                        motherOverProtectionOriginalScore += 1;
                     } else if (userAnswerJson.findPath("mother").asText().equals("D")) {
                        motherOverProtectionOriginalScore += 0;
                     }
                  }
               }
            }
         }
         //父亲教育水平
         if(Integer.parseInt(answerVo.getAnswer())== 26) {
            fatherEducationLevel =((ObjectNode) Json.parse(answerVo.getUserAnswer())).findPath("father").asText();
         }
         //母亲教育水平
         if(Integer.parseInt(answerVo.getAnswer())== 27) {
            motherEducationLevel =((ObjectNode) Json.parse(answerVo.getUserAnswer())).findPath("mother").asText();
         }
         //家庭水平
         if(Integer.parseInt(answerVo.getAnswer())== 28) {
            familyLevel = answerVo.getUserAnswer().substring(1,answerVo.getUserAnswer().length()-1);
         }

      }
      List<Score> scores = new ArrayList<>();
      if (fatherIndicator) {
         scores.add(this.createScore(orgCode, tid, uid, Integer.toString(fatherCareOriginalScore),
                 Integer.toString(fatherCareOriginalScore), SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "1"));
         scores.add(this.createScore(orgCode, tid, uid, Integer.toString(fatherOverProtectionOriginalScore),
                 Integer.toString(fatherOverProtectionOriginalScore), SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "3"));
         scores.add(this.createScore(orgCode, tid, uid, "",
                 fatherEducationLevel, SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "5"));
      }
      if (motherIndicator) {
         scores.add(this.createScore(orgCode, tid, uid, Integer.toString(motherCareOriginalScore),
                 Integer.toString(motherCareOriginalScore), SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "2"));
         scores.add(this.createScore(orgCode, tid, uid, Integer.toString(motherOverProtectionOriginalScore),
                 Integer.toString(motherOverProtectionOriginalScore), SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "4"));
         scores.add(this.createScore(orgCode, tid, uid, "",
                 motherEducationLevel, SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "6"));
      }
      scores.add(this.createScore(orgCode, tid, uid, "",
              familyLevel, SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE, "7"));

      scoreDao.save(scores);
      return null;
   }

   /**
    * 计算Z分数
    * @param originalScore
    * @param avarage
    * @param standard
    * @return
    */
   private double caculateZScore(Integer originalScore,double avarage,double standard){

      return (originalScore-avarage) * 1.0 / standard;
   }

   private Score createScore(Integer orgCode,Integer tid,Integer uid,String originalScore,String standardScore,Integer qtype,String dimension){
      Score score = new Score();
      score.setDateCreated(new Date());
      score.setOriginalScore(originalScore);
      score.setQtype(qtype);
      score.setQuality("");
      score.setStandardScore(standardScore);
      score.setTid(tid);
      score.setUid(uid);
      score.setzScore(String.valueOf(""));
      score.setDimension(dimension);
      score.setOrgCode(orgCode);
      score.setDateUpdate(new Date());
      return score;
   }


   /** 导出用户到excel
    *
    * */
   public HSSFWorkbook outputScore(Integer orgCode){
      return outputScore(orgCode, STANDARD_SCORE_INDICATOR, "分数列表");
   }

   /** 导出原始分数到用户
    *
    * */
   public HSSFWorkbook outputNormalScore(Integer orgCode){
      return outputScore(orgCode, ORIGIN_SCORE_INDICATOR, "常模分数列表");
   }

   public HSSFWorkbook outputScore(Integer orgCode, Integer indicator, String sheetName){
      HSSFWorkbook scoreExcel = ExcelUtils.createExcel();
      HSSFSheet sheet = ExcelUtils.createSheet(scoreExcel, sheetName);
      Integer rowNum = 0;
      HSSFRow head = sheet.createRow(rowNum);
      rowNum++;
      Integer colNum = 0;
      head.createCell(colNum).setCellValue("序号");
      colNum++;
      head.createCell(colNum).setCellValue("账号");
      colNum++;
      head.createCell(colNum).setCellValue("姓名");
      colNum++;
      head.createCell(colNum).setCellValue("性别");
      colNum++;
      head.createCell(colNum).setCellValue("年龄");
      colNum++;

      List<Integer> oneDimension = new ArrayList(){{
         add(SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION);
         add(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
         add(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
         add(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
         add(SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
         add(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
         add(SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
         add(SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
         add(SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
         add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
         add(SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
         add(SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
         add(SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
      }};
      List<Integer> multiDimension = new ArrayList(){{
         add(SystemConstant.QUESTION_TYPE_PERSONALITY);
         add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
         add(SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE);
      }};

      List<Questiontype> questiontypes = questionTypeDao.findByOrgCode(orgCode);
      for (int i = 0; i < questiontypes.size(); i++){
         Questiontype questiontype = questiontypes.get(i);
         if (oneDimension.contains(questiontype.getId())){
            head.createCell(colNum).setCellValue(questiontype.getName());
            colNum++;
         }
         else if (multiDimension.contains(questiontype.getId())){
            if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_PERSONALITY)) {
               for (int j = 0; j < DimensionEnum.values().length; j++) {
                  head.createCell(colNum).setCellValue(DimensionEnum.getName(j + 1));
                  colNum++;
               }
            }
            if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE)) {
               for (int j = 0; j < FamilyDimensionEnum.values().length; j++) {
                  head.createCell(colNum).setCellValue(FamilyDimensionEnum.getName(j + 1));
                  colNum++;
               }
            }
            if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY)) {
               for (int j = 0; j < CriticalThinkingTendencyDimensionEnum.values().length; j++) {
                  head.createCell(colNum).setCellValue(CriticalThinkingTendencyDimensionEnum.getName(j + 1));
                  colNum++;
               }
            }
         }
      }

      List<UserVo> userVos = userDao.findUserVoByOrgCodeHaveScore(orgCode);
      for(UserVo userVo: userVos) {
         HSSFRow row = sheet.createRow(rowNum);
         colNum = 0;
         row.createCell(colNum).setCellValue(rowNum);
         colNum++;
         row.createCell(colNum).setCellValue(userVo.getAccount());
         colNum++;
         row.createCell(colNum).setCellValue(userVo.getName());
         colNum++;
         row.createCell(colNum).setCellValue(userVo.getSex());
         colNum++;
         row.createCell(colNum).setCellValue(userVo.getAge());
         colNum++;
         List<ScoreVo> scoreVos = scoreDao.findScoreVoByUidAndOrgCode(userVo.getUid(), orgCode);
         for (int j = 0; j < questiontypes.size(); j++){
            Questiontype questiontype = questiontypes.get(j);
            if (oneDimension.contains(questiontype.getId())){
               for (ScoreVo scoreVo: scoreVos){
                  if (scoreVo.getQtype().equals(questiontype.getId())){
                     row.createCell(colNum).setCellValue(getScoreType(scoreVo,indicator));
                     colNum++;
                  }
               }
            }
            else if (multiDimension.contains(questiontype.getId())){
               if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_PERSONALITY)) {
                  for (ScoreVo scoreVo: scoreVos){
                     if (scoreVo.getQtype().equals(questiontype.getId())){
                        row.createCell(colNum + Integer.valueOf(scoreVo.getDimension()) - 1).setCellValue(getScoreType(scoreVo, indicator));
                     }
                  }
                  colNum += DimensionEnum.values().length;
               }
               if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE)) {
                  for (ScoreVo scoreVo: scoreVos){
                     if (scoreVo.getQtype().equals(questiontype.getId())){
                        row.createCell(colNum + Integer.valueOf(scoreVo.getDimension()) - 1).setCellValue(getScoreType(scoreVo, indicator));
                     }
                  }
                  colNum += FamilyDimensionEnum.values().length;
               }
               if (questiontype.getId().equals(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY)) {
                  for (ScoreVo scoreVo: scoreVos){
                     if (scoreVo.getQtype().equals(questiontype.getId())){
                        row.createCell(colNum + Integer.valueOf(scoreVo.getDimension()) - 1).setCellValue(getScoreType(scoreVo, indicator));
                     }
                  }
                  colNum += CriticalThinkingTendencyDimensionEnum.values().length;
               }
            }
         }
         rowNum++;
      }
      return scoreExcel;
   }

   /**
    *
    * @param scoreVo
    * @param indicator
    * @return
    */
   private static String getScoreType(ScoreVo scoreVo, Integer indicator){
      if (indicator.equals(ORIGIN_SCORE_INDICATOR))
         return scoreVo.getOriginalScore();
      else if (indicator.equals(STANDARD_SCORE_INDICATOR))
         return scoreVo.getStandardScore();
      return null;
   }

   /**
    * 计算两向量之间的距离
    * @param x 点x
    * @param y 点y
    * @return
    */
   private   Double caculateDistance(double[] x,double[] y){
      double total = 0;
      for (int i = 0; i < y.length; i++) {
         total += Math.pow( x[i]- y[i],2);
      }
      return Math.sqrt(total);
   }
   /**
    * 计算情绪识别的最大距离
    * @param start 一组点的起始数
    * @param end 一组点的结束数
    * @param y 参考的
    * @return
    */
   private void ergodic(Integer start, Integer end, int n, double[] y, List target, List<Double> distances){
      if(target.size() == y.length){
         double[] x = new double[y.length];
         for(int j = 0; j < y.length; j++){
            x[j] = (double)(int)target.get(j);
         }
         double distance = this.caculateDistance(x, y);
         distances.add(distance);
         return;
      }
      for (int i = 0; i < n; i++) {
         List firstTarget = new ArrayList(target);
         firstTarget.add(start);
         ergodic(start, end, --n, y, firstTarget, distances);
         List secondTarget = new ArrayList(target);
         secondTarget.add(end);
         ergodic(end, start, n, y, firstTarget, distances);
      }
   }
   private Double caculateEmotionRecognitionMaxDistance(Integer start,Integer end,double[] y){
      List<Double> distances = new ArrayList<>();
      this.ergodic(start, end, y.length, y, new ArrayList<Double>(), distances);
      return Collections.max(distances);
   }
   /**
    * 计算情绪理解最大距离
    * @param y 情绪理解参考点
    * @return
    */
   private Double caculateEmotionUderstandingMaxDistance(double[] y){
      int total = 10;
      int dimension_max = 6;
      final double[] array = new double[y.length];
      int i = 0;
      while(total > dimension_max){
         array[i] = dimension_max;
         total -= dimension_max;
         i++;
      }
      array[i] = total;
      List<Double> distances = new ArrayList<>();

      this.sortAll(new ArrayList(){{
         for(double d: array)
            add(d);
      }}, new ArrayList<Double>(), y, distances);

      return Collections.max(distances);
   }
   private void sortAll(List datas, List target, double[] y, List<Double> distances) {
      if (target.size() == y.length) {
         double[] x = new double[y.length];
         for(int j = 0; j < y.length; j++){
            x[j] = (double)target.get(j);
         }
         double distance = this.caculateDistance(x, y);
         distances.add(distance);
         return;
      }
      for (int i = 0; i < datas.size(); i++) {
         List newDatas = new ArrayList(datas);
         List newTarget = new ArrayList(target);
         newTarget.add(newDatas.get(i));
         newDatas.remove(i);
         sortAll(newDatas, newTarget, y, distances);
      }
   }
   /**
    * 按照标准答案处理用户答案，将0值补齐
    * @param answer
    * @param userAnswer
    * @return
    */
   private double[] dealUserAnswer(String answer, String userAnswer){
      ObjectNode objectNode = (ObjectNode) Json.parse(answer);
      Iterator iterator = objectNode.fieldNames();
      List<String> chars =  new ArrayList<>();
      while (iterator.hasNext()){
         String choice = (String)iterator.next();
         chars.add(choice);
      }
      ObjectNode userAnswerNode = (ObjectNode) Json.parse(userAnswer);
      double[] numberBySort = new double[objectNode.size()];
      Collections.sort(chars);
      for (int i = 0; i < chars.size(); i++) {
         numberBySort[i] = userAnswerNode.has(chars.get(i)) ? userAnswerNode.findPath(chars.get(i)).asDouble(): 0;
      }

      return numberBySort;
   }

   /**
    * 将乱序的json答案按照顺序排列
    * @param pointsStr json字符串
    * @return
    */
   private double[] getPointsBySort(String pointsStr) {
      ObjectNode objectNode = (ObjectNode) Json.parse(pointsStr);
      Iterator iterator = objectNode.fieldNames();
      List<String> chars = new ArrayList<>();
      while (iterator.hasNext()) {
         String choice = (String) iterator.next();
         chars.add(choice);
      }
      double[] numberBySort = new double[objectNode.size()];
      Collections.sort(chars);
      for (int i = 0; i < chars.size(); i++) {
         numberBySort[i] = objectNode.findPath(String.valueOf(chars.get(i))).asDouble();
      }
      return numberBySort;
   }
}