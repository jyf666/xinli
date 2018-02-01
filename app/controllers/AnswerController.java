/***********************************************************************
 * Module:  AnswerInfoController.java
 * Author:  XIAODA
 * Purpose: Defines the Class AnswerInfoController
 ***********************************************************************/

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Questiontype;
import models.dto.AnswerDto;
import models.vo.AnswerVo;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.AnswerService;
import service.QuestionTypeService;
import service.TestService;
import utils.SystemConstant;
import utils.json.JsonMapper;
import views.html.admin.userManage.answerList;

import java.util.*;

/** 答题信息控制
 * 
 * @pdOid ae8d385a-ca59-4185-821d-b1c14e8de314 */
@org.springframework.stereotype.Controller
public class AnswerController extends Controller {

   @Autowired
   private AnswerService answerService;
   @Autowired
   private QuestionTypeService questionTypeService;
   @Autowired
   private TestService testService;

   /**
    * 添加考生答题信息
    * */
   @BodyParser.Of(value = BodyParser.Json.class,maxLength = 8 * 1024 * 1024)
   public Result upload() {
      JsonNode jsonNode = request().body().asJson();
      JsonMapper jsonMapper = new JsonMapper();
      AnswerDto answerDto = jsonMapper.fromJson(jsonNode.toString(), AnswerDto.class);
      answerService.upload(answerDto);
      return ok();
   }

   /** 考试答案页面
    * @param uid 用户id
    * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
    * */
   public Result answerListView(Integer tid,Integer uid) {
      List<AnswerVo> answers = answerService.getUserAnswer(tid, uid);
      List<Questiontype> questiontypes = questionTypeService.findAllByTpid(testService.findById(tid).getPid());
      for (int i = 0; i < questiontypes.size(); i++) {
         if(questiontypes.get(i).getId() == SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY){
            questiontypes.remove(i);
         }
      }

      List<List<Map<String,Object>>> q = new ArrayList();
      for(int i = 0;i<questiontypes.size();i++) {
         List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
         Map m = new HashMap();
         Questiontype questiontype = questiontypes.get(i);
         m.put("questionType",questiontype.getId());
         List as = new ArrayList();
         for(int j = 0;j<answers.size();j++) {
            AnswerVo answer = answers.get(j);
            if(answer.getqType()==questiontype.getId()) {
               as.add(answer);
            }
            if(j==answers.size()-1){
               m.put("answers",as);
               m.put("totalNum",as.size());
               list.add(m);
            }
         }
         q.add(list);
      }
      return ok(answerList.render(questiontypes,q));
   }

}