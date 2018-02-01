package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.AnswerService;
import service.EmotionRecognitionService;
import service.QuestionTypeService;
import service.SessionService;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.manifest;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import views.html.emotionRecognition.instructions;
import views.html.emotionRecognition.practice;
import views.html.emotionRecognition.practiceEnd;
import views.html.emotionRecognition.test;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/10/27.
 */
@org.springframework.stereotype.Controller
public class EmotionRecognitionController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION;

    @Autowired
    private EmotionRecognitionService emotionRecognitionService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到情绪识别说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到情绪识别练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        List<Map<String, Object>> list = emotionRecognitionService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到情绪识别练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, timeMsg));
    }

    /**
     * 跳转到情绪识别考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String, Object>> list = emotionRecognitionService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 跳转到情绪识别考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "emotionRecognition"));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result submitAnswer(){

        JsonNode answerListJson = request().body().asJson();
        Iterator iterator = answerListJson.iterator();
        while(iterator.hasNext()){
            ObjectNode answerJson = (ObjectNode)iterator.next();
            Answer answer = new Answer();
            answer.setUid(answerJson.findPath("uid").asInt());
            answer.setTid(answerJson.findPath("tid").asInt());
            answer.setQid(answerJson.findPath("qid").asText());
            answer.setAnswer(answerJson.findPath("answer").asText());
            answer.setQtype(answerJson.findPath("qType").asInt());
            answer.setType(answerJson.findPath("type").asText());
            answer.setClickNum(answerJson.findPath("clickNum").asInt());
            answer.setRightNum(answerJson.findPath("rightNum").asInt());
            answer.setClickTime(DateUtils.parseDate(answerJson.findPath("clickTime").asText()));
            answer.setDateCreated(new Date());
            answerService.save(answer);
        }
        return ok();
    }

    /**
     * 跳转到情绪识别练习结束说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> list = emotionRecognitionService.getInstructionsManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到情绪识别练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = emotionRecognitionService.getPracticeManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到情绪识别练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = emotionRecognitionService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到情绪识别考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = emotionRecognitionService.getTestManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到情绪识别考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = emotionRecognitionService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}



