package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.common.test.endTime;
import views.html.manifest;
import views.html.shapeLinking.instructions;
import views.html.shapeLinking.practice;
import views.html.shapeLinking.practiceEnd;
import views.html.shapeLinking.test;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mare on 15/7/16.
 */
@org.springframework.stereotype.Controller
public class ShapeLinkingController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_SHAPE_LINKING;

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private ShapeLinkingService shapeLinkingService;

    /**
     * 跳转到形状连线说明页面
     * @return
     */
    public Result instructions() {
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到形状连线练习页面
     * @return
     */
    public Result practice() {
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        List<Map<String,List<Map<String, String>>>> questiontList = shapeLinkingService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+questiontList.size();
        return ok(practice.render(questionTypeMap, questiontList, pageNum));
    }

    /**
     * 跳转到形状连线练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, timeMsg));
    }

    /**
     * 跳转到形状连线考试页面
     * @return
     */
    public Result test() {
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String,List<Map<String, String>>>> questiontList = shapeLinkingService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+questiontList.size();
        return ok(test.render(questiontList, pageNum));
    }

    /**
     * 跳转到形状连线考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "shapelinking"));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result submitAnswer() {

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
            answer.setRightNum(answerJson.findPath("rightNum").asInt());
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
        List<String> cacheList = shapeLinkingService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到符号运算练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        List<String> list = shapeLinkingService.getPracticeManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = shapeLinkingService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = shapeLinkingService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到符号运算考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = shapeLinkingService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
