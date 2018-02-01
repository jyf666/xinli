package controllers.exam;

import play.cache.Cache;
import service.*;
import utils.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.SystemConstant;
import views.html.manifest;
import views.html.exam.symbolicOperation.instructions;
import views.html.exam.symbolicOperation.practice;
import views.html.exam.symbolicOperation.practiceEnd;
import views.html.exam.symbolicOperation.test;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/7/21.
 */
@org.springframework.stereotype.Controller
public class SymbolicOperationController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION;

    @Autowired
    private SymbolicOperationService symbolicOperationService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到符号运算说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        Long size = symbolicOperationService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(instructions.render(questionTypeMap, size));
    }

    /**
     * 跳转到符号运算练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        List<Map<String,Object>> list = symbolicOperationService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到符号运算练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, timeMsg));
    }

    /**
     * 跳转到符号运算考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String,Object>> list = symbolicOperationService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 跳转到符号运算考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "symbolicOperation"));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result submitAnswer(){
        Integer uid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.USER_ID);// 考生id
        Integer tid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TEST_ID);// 考试表id

        JsonNode answerListJson = request().body().asJson();
        Iterator iterator = answerListJson.iterator();
        while(iterator.hasNext()){
            ObjectNode answerJson = (ObjectNode)iterator.next();
            Answer answer = new Answer();
            answer.setUid(uid);
            answer.setTid(tid);
            answer.setQid(answerJson.findPath("qid").asText());
            answer.setAnswer(answerJson.findPath("answer").asText());
            answer.setQtype(answerJson.findPath("qtype").asInt());
            answer.setClickNum(answerJson.findPath("clickNum").asInt());
//            answer.setRightNum(answerJson.findPath("rightNum").asInt());
            answer.setClickTime(DateUtils.parseDateTime(answerJson.findPath("clickTime").asText()));
            answer.setDateCreated(new Date());
            answerService.save(answer);
        }
        return ok();
    }


    /**
     * 跳转到符号运算说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = symbolicOperationService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到符号运算练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        List<String> list = symbolicOperationService.getPracticeManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = symbolicOperationService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = symbolicOperationService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = symbolicOperationService.getEndTimeManifest();
        return ok(manifest.render(list));
    }

}
