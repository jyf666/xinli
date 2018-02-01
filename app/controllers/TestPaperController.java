/***********************************************************************
 * Module:  TestPaperController.java
 * Author:  XIAODA
 * Purpose: Defines the Class TestPaperController
 ***********************************************************************/

package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.vo.QuestionVo;
import models.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.*;
import utils.PageUtils;
import utils.ExcelUtils;
import views.html.admin.testPapaerManage.testPaperList;
import views.html.admin.testPapaerManage.testPaperQuestionList;
import views.html.admin.testPapaerManage.testPaperEdit;
import views.html.admin.testPapaerManage.testPaperMake;
import views.html.admin.testPapaerManage.testPaperAddQuestion;
import views.html.admin.testPapaerManage.testPaperQuestionTypeList;
import views.html.admin.testPapaerManage.testPaperAddQuestionType;
import views.html.admin.testPapaerManage.testPaperAdd;
import views.html.admin.testPapaerManage.testPaperImport;

import java.io.*;
import java.util.*;
import play.libs.Json;
import utils.json.JsonMapper;

import javax.mail.Session;

/** 试卷控制
 * 
 * @pdOid 13e917c5-e997-4e0a-8be0-2caf2a9cec9d */

@org.springframework.stereotype.Controller
public class TestPaperController extends Controller {
   @Autowired
   private TestPaperService testPaperService;
   @Autowired
   private TestpaperQuestiontypeService testpaperQuestiontypeService;
   @Autowired
   private QuestionService questionService;
   @Autowired
   private QuestionTypeService questionTypeService;

   public Result updateLimitTime(Integer tpid) {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      Integer qtid = objectNode.get("qtid").asInt();
      Integer limitTime = objectNode.get("limitTime").asInt();
      testpaperQuestiontypeService.updateLimitTime(tpid, qtid, limitTime);
      return ok("修改成功!");
   }

   public Result updateOrder(Integer tpid) {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      JsonNode seq = objectNode.get("seq");
      List<Integer> seqList = new ArrayList();
      if (seq.isArray()) {
         for (JsonNode objNode : seq) {
            seqList.add(Integer.parseInt(objNode.textValue()));
         }
      }
      testpaperQuestiontypeService.updateOrder(tpid, seqList);
      return ok("修改成功!");
   }

   /**
    * 导出试卷
    * @param  tpid
    * @return
     */
   public Result export(Integer tpid) {
      List<Question> questions = testPaperService.export(tpid);
      List<Questiontype> questiontypes = questionTypeService.findAllByTpid(tpid);
      List q = new ArrayList();
      for(int i = 0;i<questiontypes.size();i++) {
         Map m = new HashMap();
         Questiontype questiontype = questiontypes.get(i);
         m.put("questionType",questiontype.getId());
         List qt = new ArrayList();
         for(int j = 0;j<questions.size();j++) {
            Question question = questions.get(j);
            if(question.getQtype()==questiontype.getId()) {
               qt.add(question);
            }
            if(j==questions.size()-1){
               m.put("questions",qt);
               m.put("totalNum",qt.size());
            }
         }
         q.add(m);
      }
      FileWriter fw = null;
      FileOutputStream fileOutputStream = null;
      File file = null;
      try {
         file = utils.FileUtils.getPublicFile(null, tpid.toString() + ".json");
         fw = new FileWriter(file);
         JsonMapper jsonMapper = new JsonMapper();
         String json = jsonMapper.toJson(q);
         fw.write(json);
         fw.flush();
         fw.close();
      }catch (Exception e){
         e.printStackTrace();
      }
      return ok(file);
   }

   /**
    * 导入试卷
    * @param
    * @return
    */
   public Result importPaper() {
      //处理传参
      List<String> questionTypeList = new ArrayList();
      List<String> questionTypeLimitTimeList = new ArrayList();
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      Integer orgCode = objectNode.findPath("orgCode").asInt();
      if (orgCode == null){
         orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
      }
      Integer exceptTime = objectNode.findPath("exceptTime").asInt();
      String isReference = objectNode.findPath("isReference").asText();
      String name = objectNode.findPath("name").asText();
      String jsonQuestionVos = objectNode.findPath("questionVos").asText();
      //保存题目,若题目存在,则忽略
      JsonMapper jsonMapper = new JsonMapper();
      ObjectMapper mapper = new ObjectMapper();
      List<LinkedHashMap> questions = new ArrayList();
      List<LinkedHashMap> questionVos = jsonMapper.fromJson(jsonQuestionVos, List.class);
      for (LinkedHashMap questionVo: questionVos){
         List<LinkedHashMap> questions1 = (List)questionVo.get("questions");
         String qtype = questionVo.get("questionType").toString();
         questions.addAll(questions1);
         questionTypeList.add(qtype);
         questionTypeLimitTimeList.add("120");
      }
      questionService.saveQuestions(questions);
      //保存试卷
      Map testpaper = new HashMap();
      testpaper.put("testpaperName", name);
      testpaper.put("orgCode", orgCode);
      testpaper.put("exceptTime", exceptTime);
      testpaper.put("isReference", isReference);
      testpaper.put("useStatus", "1");
      Integer tpid = testPaperService.save(mapper.valueToTree(testpaper));
      //添加题型到试卷
      testPaperService.addQuestiontype(tpid, String.join(",", questionTypeList), String.join(",", questionTypeLimitTimeList));
      //添加题目到试卷
      testPaperService.associateQuestionToTestpaper(tpid, questions);
      return ok();
   }

   /**
    * 校验上传的excel
    **/
   public Result importPaperValidator() {
      Http.MultipartFormData.FilePart file = request().body().asMultipartFormData().getFile("Filedata");
      JsonMapper jsonMapper = new JsonMapper();
      String json = "";
      String error = "error";
      try {
         FileReader fileReader = new FileReader(file.getFile());
         BufferedReader bufferedReader =
         new BufferedReader(fileReader);
         String line = null;
         while((line = bufferedReader.readLine()) != null) {
            json += line;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return ok(Json.toJson(error));
      }
      List<QuestionVo> questionVos = (List<QuestionVo>) jsonMapper.fromJson(json, List.class);
      if (questionVos == null){
         return ok(Json.toJson(error));
      }
      List<QuestionVo> errorQuestions = new ArrayList<QuestionVo>();
      List<QuestionVo> rightQuestions = new ArrayList<QuestionVo>();
      questionService.validateQuestion(questionVos, errorQuestions, rightQuestions);

      if (errorQuestions.size() > 0) {
         return ok(Json.toJson(errorQuestions));
      }
      for (int i = 0; i < rightQuestions.size(); i++) {
         QuestionVo questionVo = rightQuestions.get(i);
         questionVo.setJsonQuestionError("正常");
      }
      return ok(Json.toJson(questionVos));
   }


   /** 修改试卷页面
    * @param tpid 试卷id
    * @pdOid 8a664071-7af3-44f8-a133-8d573efc5f90 */
   public Result editView(Integer tpid) {
      Testpaper testpaper = testPaperService.findOne(tpid);
      return ok(testPaperAdd.render(testpaper));
   }


   /** 修改试卷为模版试卷
    * @param tpid 试卷id
    * @pdOid 8a664071-7af3-44f8-a133-8d573efc5f90 */
   public Result set(Integer tpid) {
      testPaperService.setToReference(tpid);
      return ok();
   }

   /** 修改试卷
    *
    * @pdOid 2dcac20e-b10c-4c26-bb80-5155b85ee1fb */
   public Result editTestpaper() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      testPaperService.update(objectNode);
      return ok();
   }

   /**
    * 试卷列表页面
    * @return
    */
   public Result listView() {
      return ok(testPaperList.apply());
   }

   /** 获取试卷分页数据
    *@return
    *
    */
   public Result page() {
      String draw = request().getQueryString("draw"); //获取请求次数
      String[] cols = {"id", "id", "name", "orgCode", "expectTime", "dateCreated", "isReference","useStatus","lastTestTime"};// 定义列名
      Page<VTestPaper> page = testPaperService.findAll(request(), cols);
      return ok(PageUtils.convertToTableData(page, draw));
   }
   /** 试卷试题页面
    * @param tpid 试卷id
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result testpaperQuestionView(Integer tpid) {
      List<QuestionVo> questions = questionService.getQuestionByTpid(tpid);
      List<Questiontype> questiontypes = questionTypeService.findAllByTpid(tpid);
      List q = new ArrayList();
      for(int i = 0;i<questiontypes.size();i++) {
         List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
         Map m = new HashMap();
         Questiontype questiontype = questiontypes.get(i);
         m.put("questionType",questiontype.getId());
         List qt = new ArrayList();
         for(int j = 0;j<questions.size();j++) {
            QuestionVo question = questions.get(j);
            if(question.getqType()==questiontype.getId()) {
               qt.add(question);
            }
            if(j==questions.size()-1){
               m.put("questions",qt);
               m.put("totalNum",qt.size());
               list.add(m);
            }
         }
         q.add(list);
      }
      return ok(testPaperQuestionList.render(questiontypes, q, tpid));
   }


   /** 试卷试题题型页面
    * @param tpid 试卷id
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result testpaperQuestiontype(Integer tpid) {
      List<Questiontype> questiontypes = questionTypeService.findAllByTpid(tpid);
      List<VQuestiontype> vQuestiontypes = questionTypeService.findAllVQuestionTypeByTpid(tpid);
      return ok(testPaperQuestionTypeList.render(vQuestiontypes,tpid));
   }

   /** 删除试卷题型
    * @param tpid 试卷id
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result deleteQuestiontype(Integer tpid,Integer qType) {
      testPaperService.deleteQuestiontype(tpid,qType);
      return ok();
   }



   /** 添加试题到试卷页面
    * @param tpid 试卷id
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result addQuestionToTestpaperView(Integer tpid) {
      List<QuestionVo> testpaperQuestions = questionService.getQuestionByTpid(tpid);
      List<QuestionVo> questions = questionService.getAllQuestion();

      for(int i = 0;i<testpaperQuestions.size();i++){
         if(questions.contains(testpaperQuestions.get(i))) {
            questions.remove(testpaperQuestions.get(i));
         }
      }
      List<Questiontype> questiontypes = questionTypeService.findAllByTpid(tpid);
      List q = new ArrayList();
      for(int i = 0;i<questiontypes.size();i++) {
         List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
         Map m = new HashMap();
         Questiontype questiontype = questiontypes.get(i);
         m.put("questionType",questiontype.getId());
         List qt = new ArrayList();
         for(int j = 0;j<questions.size();j++) {
            QuestionVo question = questions.get(j);
            if(question.getqType()==questiontype.getId()) {
               qt.add(question);
            }
            if(j==questions.size()-1){
               m.put("questions",qt);
               m.put("totalNum",qt.size());
               list.add(m);
            }
         }
         q.add(list);
      }
      return ok(testPaperAddQuestion.render(questiontypes,q,tpid));
   }

   /** 添加试题到试卷
    *
    * @pdOid 1fc3c7b2-54ca-4bfc-8a0f-a6265de2a080 */
   public Result addQuestionToTestpaper(Integer tpid) {
      JsonNode questionListJson = request().body().asJson();
      testPaperService.addQuestionToTestpaper(questionListJson,tpid);
      return ok(Integer.toString(tpid));
   }

   /** 添加试题题型页面
    * @param tpid 试卷id
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result addQuestiontypeView(Integer tpid) {
      List<Questiontype> questiontypes = questionTypeService.getAllQuestionType();
      List<Questiontype> testpaperQuestiontypes = questionTypeService.findAllByTpid(tpid);

      for (int i = 0; i < testpaperQuestiontypes.size(); i++) {
         if(questiontypes.contains(testpaperQuestiontypes.get(i))){
            questiontypes.remove(testpaperQuestiontypes.get(i));
         }
      }

      return ok(testPaperAddQuestionType.render(questiontypes, tpid));
   }

   /** 添加试题到试卷
    *
    * @pdOid 1fc3c7b2-54ca-4bfc-8a0f-a6265de2a080 */
   public Result addQuestiontype() {
      Map<String,String[]> params = request().body().asFormUrlEncoded();
      String questiontypeStr = params.get("questiontypeStr")[0];
      String timeStr = params.get("timeStr")[0];
      Integer tpid = Integer.parseInt(params.get("tpid")[0]);
      testPaperService.addQuestiontype(tpid, questiontypeStr, timeStr);
      return ok();
   }

   /** 删除试卷的试题
    * @param tpid 试卷id
    * @param qid 题目id
    * @pdOid 1fc3c7b2-54ca-4bfc-8a0f-a6265de2a080 */
   public Result deleteQuestion(Integer tpid,String qid) {
      testPaperService.deleteQuestion(tpid, qid);
      return ok();
   }

   /** 试卷选题页面
    * @param qTypeStr 题型串 如1，2，3，4
    * @pdOid 2822d079-b8ab-4c7c-bce6-e4a2f4477cfd */
   public Result makeView(String qTypeStr) {
      List<QuestionVo> questions = questionService.getAllQuestion();
      List<Questiontype> questiontypes = new ArrayList<Questiontype>();
      String[] qType = qTypeStr.split(",");
      for(int i =0;i<qType.length;i++){
         questiontypes.add(questionTypeService.findById(Integer.parseInt(qType[i])));
      }
      List q = new ArrayList();
      for(int i = 0;i<questiontypes.size();i++) {
         List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
         Map m = new HashMap();
         Questiontype questiontype = questiontypes.get(i);
         m.put("questionType",questiontype.getId());
         List qt = new ArrayList();
         for(int j = 0;j<questions.size();j++) {
            QuestionVo question = questions.get(j);
            if(question.getqType()==questiontype.getId()) {
               qt.add(question);
            }
            if(j==questions.size()-1){
               m.put("questions",qt);
               m.put("totalNum",qt.size());
               list.add(m);
            }
         }
         q.add(list);
      }
      return ok(testPaperMake.render(questiontypes,q));
   }

   /**
    * 获取题目类型列表
    * @return
    */
   public Result quetionTypeList(){
      List<Questiontype> questiontypes = questionTypeService.getAllQuestionType();
      return ok(Json.toJson(questiontypes));
   }

   /** 组卷
    *
    * @pdOid 1fc3c7b2-54ca-4bfc-8a0f-a6265de2a080 */
   public Result makeTestpaper() {
      JsonNode questionListJson = request().body().asJson();
      String tpid = testPaperService.makeTestpaper(questionListJson);
      return ok(tpid);
   }

   /** 删除试卷
    * @param tpid 试卷id
    * @pdOid 1fc3c7b2-54ca-4bfc-8a0f-a6265de2a080 */
   public Result deleteTestpaper(Integer tpid) {
      testPaperService.delete(tpid);
      return ok();
   }

   /** 添加试卷页面
    * @param
    * @pdOid  */
   public Result addView() {
      Testpaper testpaper = new Testpaper();
      return ok(testPaperAdd.render(testpaper));
   }
   /**
    * 导入试卷页面
    */
   public Result importPaperView() {
      Testpaper testpaper = new Testpaper();
      return ok(testPaperImport.render(testpaper));
   }
}