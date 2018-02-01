/***********************************************************************
 * Module:  QuestionController.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestionController
 ***********************************************************************/

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.dto.QuestionDto;
import models.vo.QuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.SystemConstant;
import utils.enums.EmotionRecognitionDimensionEnum;
import utils.enums.EmotionUnderstandingDimensionEnum;
import views.html.admin.questionManage.questionList;
import views.html.admin.questionManage.questionListByType;
import views.html.admin.testPapaerManage.questionTypeAdd;
import views.html.admin.testPapaerManage.questionTypeEdit;
import views.html.admin.questionManage.questionGenerate;
import views.html.admin.questionManage.paragraphReasoningAdd;
import views.html.admin.questionManage.paragraphReasoningEdit;
import views.html.admin.questionManage.personalityEdit;
import views.html.admin.questionManage.familyQuestionnaireEdit;
import views.html.admin.questionManage.emotionRecognitionEdit;
import views.html.admin.questionManage.emotionUnderstandingEdit;
import views.html.admin.questionManage.remoteAssociationEdit;
import views.html.admin.questionManage.remoteAssociationAdd;

import java.util.*;
import java.util.List;
import utils.PageUtils;


/**
 * 题库
 */
@org.springframework.stereotype.Controller
public class QuestionController extends Controller {

   @Autowired
   private QuestionTypeService questionTypeService;
   @Autowired
   private QuestionService questionService;
   @Autowired
   private SymbolicOperationService symbolicOperationService;
   @Autowired
   private SpatialMemoryService spatialMemoryService;
   @Autowired
   private GrapfSearchService grapfSearchService;
   @Autowired
   private MaterialMemoryService materialMemoryService;
   @Autowired
   private ShapeLinkingService shapeLinkingService;

   public Result listQuestionByType(Integer qType) {
      return ok(questionListByType.render(qType));
   }

   /** 添加考试题（只针对某些类型）
    * 
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result addView(Integer qType) {
      Questiontype questiontype = questionTypeService.findById(qType);
      if(qType == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING)
         return ok(paragraphReasoningAdd.render(questiontype));
      if(qType == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION)
         return ok(remoteAssociationAdd.render(questiontype));

      return ok();
   }

   /** 添加考试题（只针对某些类型）
    *
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result add() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      questionService.save(objectNode);
      return ok();
   }
   
   /** 修改考试题（只针对某些类型）
    * 
    * @pdOid 13018095-0120-433c-930c-7f162e478b08 */
   public Result editView(String qid) {
      Question question = questionService.findById(qid);
      QuestionVo  questionVo = new QuestionVo();

      questionVo.setId(question.getId());
      questionVo.setqType(question.getQtype());
      questionVo.setAnswer(question.getAnswer());
      questionVo.setQuestion(question.getQuestion());
      questionVo.setSubType(question.getSubType());
      questionVo.setDifficulty(question.getDifficulty());
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING){
         ObjectNode objectNode = (ObjectNode)Json.parse(question.getChoices());
         questionVo.setA(objectNode.findPath("A").asText());
         questionVo.setB(objectNode.findPath("B").asText());
         questionVo.setC(objectNode.findPath("C").asText());
         questionVo.setD(objectNode.findPath("D").asText());
         return ok(paragraphReasoningEdit.render(questionVo));
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_PERSONALITY){
         ObjectNode objectNode = (ObjectNode)Json.parse(question.getChoices());
         questionVo.setA(objectNode.findPath("A").asText());
         questionVo.setB(objectNode.findPath("B").asText());
         return ok(personalityEdit.render(questionVo));
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE){
         ObjectNode objectNode = (ObjectNode)Json.parse(question.getChoices());
         questionVo.setA(objectNode.findPath("A").asText());
         questionVo.setB(objectNode.findPath("B").asText());
         questionVo.setC(objectNode.findPath("C").asText());
         questionVo.setD(objectNode.findPath("D").asText());
         return ok(familyQuestionnaireEdit.render(questionVo));
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION){
         ObjectNode objectNode = (ObjectNode)Json.parse(question.getAnswer());
         Map<String,Map<String,String>> choicesMap =  new HashMap<>();
         Iterator iterator = objectNode.fieldNames();
         while (iterator.hasNext()){
            String choice = (String)iterator.next();
            Map<String,String> valueMap = new HashMap<>();
            valueMap.put(EmotionRecognitionDimensionEnum.getName(choice),objectNode.findPath(choice).asText());
            choicesMap.put(choice,valueMap);
         }
         return ok(emotionRecognitionEdit.render(questionVo,choicesMap));
      }

      if(question.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING){
         ObjectNode objectNode = (ObjectNode)Json.parse(question.getAnswer());
         Map<String,Map<String,String>> choicesMap =  new HashMap<>();
         Iterator iterator = objectNode.fieldNames();
         while (iterator.hasNext()){
            String choice = (String)iterator.next();
            Map<String,String> valueMap = new HashMap<>();
            valueMap.put(EmotionUnderstandingDimensionEnum.getName(choice),objectNode.findPath(choice).asText());
            choicesMap.put(choice,valueMap);
         }
         return ok(emotionUnderstandingEdit.render(questionVo,choicesMap));
      }

      if(question.getQtype() == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION){
         List<String> questionList = Arrays.asList(question.getQuestion().split(","));
         List<String> answertList = Arrays.asList(question.getAnswer().split(","));
         return ok(remoteAssociationEdit.render(questionVo,questionList,answertList));
      }

      return ok("该题型暂时不支持修改");
   }

   /**
    * 修改考试题（只针对某些类型）
    * @return
    */
   public Result edit() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      questionService.update(objectNode);
      return ok();
   }

   /**
    * 题库列表页面
    * @return
    */
   public Result listView() {
      return ok(questionList.apply());
   }

   /**
    * 获取题库分页数据
    * @return
    */
   public Result page() {
      String draw = request().getQueryString("draw"); //获取请求次数
      String[] cols = {"id", "id", "question", "choices", "choicesType", "answer", "qtype", "subType", "difficulty", "dateCreated", "ispractice", "prompt", "material"};// 定义列名
      Page<Question> page = questionService.findAll(request(), cols);
      return ok(PageUtils.convertToTableData(page, draw));
   }

   /**
    * 生成试题页面
    * @return
    */
   public Result generateView() {
//      List<Testpaper> testpapers = testPaperService.getTestpaperByQtype(qType);
      return ok(questionGenerate.apply());
   }

   /**
    * 生成试题
    * @return
    */
   public Result generate() {
      Map<String,String[]> params = request().body().asFormUrlEncoded();
      Integer questionNumber = 0;
      if(params.get("questionNumber")!=null) {
          questionNumber = Integer.parseInt(params.get("questionNumber")[0]); //题目数量
      }
      Integer qType = Integer.parseInt(params.get("qType")[0]); //题型
      String ispractice = params.get("ispractice")[0]; //题目类型
      List<Question> questions = new ArrayList<Question>();
      if(qType == SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT){
         questions = materialMemoryService.makeQuestion();
      }else if(qType == SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION){
         questions = symbolicOperationService.makeQuestion(ispractice,questionNumber);
      }else if(qType == SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY){
         questions = spatialMemoryService.makeQuestion(ispractice, questionNumber);
      }else if(qType == SystemConstant.QUESTION_TYPE_GRAPF_SEARCH){
         questions = grapfSearchService.addGrapfSearch(ispractice);
      }else if(qType == SystemConstant.QUESTION_TYPE_SHAPE_LINKING){
         questions = shapeLinkingService.generateShapeLinking(ispractice, questionNumber, 3);
      }
      JsonNode jsonNode = Json.toJson(questions);
      return ok(jsonNode);
   }

   /**
    * 将试题上传到题库
    * @return
    */
   public Result upload(){
      Form<QuestionDto> form = Form.form(QuestionDto.class);
      QuestionDto questionDto = form.bindFromRequest().get();
      questionService.save(questionDto.getQuestionList());
      return ok();
   }

   public Result addToTestpaper(Integer tpid){
      Form<QuestionDto> form = Form.form(QuestionDto.class);
      QuestionDto questionDto = form.bindFromRequest().get();
      questionService.save(questionDto.getQuestionList(), tpid);
      return ok();
   }

   /** 添加考试题型页面
    *
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result addQuestionTypeView() {
      return ok(questionTypeAdd.render());
   }

   /** 修改考试题型页面
    *
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result editQuestionTypeView(Integer qtId) {
      Questiontype questiontype = questionTypeService.findById(qtId);
      return ok(questionTypeEdit.render(questiontype));
   }

   /** 添加考试题型
    *
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result addQuestionType() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      questionTypeService.save(objectNode);
      return ok();
   }

   /** 修改考试题型
    *
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result editQuestionType() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      questionTypeService.update(objectNode);
      return ok();
   }

   /** 停用题型
    * @param qtype 题型id
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result deleteQuestionType(Integer qtype) {
      questionTypeService.delete(qtype);
      return ok();
   }

   /** 停用题目
    * @param qid 题号id
    * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
   public Result deleteQuestion(String qid) {
      questionService.delete(qid);
      return ok();
   }
}