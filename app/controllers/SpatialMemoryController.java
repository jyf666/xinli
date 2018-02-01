package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.SystemConstant;
import views.html.manifest;
import views.html.spatialMemory.instructions;
import views.html.spatialMemory.practice;
import views.html.spatialMemory.practiceEnd;
import views.html.spatialMemory.test;
import views.html.common.test.endTime;

import java.util.*;


@org.springframework.stereotype.Controller
public class SpatialMemoryController extends Controller {

    private final Integer qType = SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY;

    @Autowired
    private  SpatialMemoryService spatialMemoryService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到空间记忆说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qType);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到空间记忆练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qType);
        String[] list = spatialMemoryService.getQuestions(tpid, qType, SystemConstant.QUESTION_ISPRACTICE_YES);
        return ok(practice.render(list[0],list[1],questionTypeMap));
    }

    /**
     * 跳转到空间记忆练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qType);
        return ok(practiceEnd.render(questionTypeMap));
    }

    /**
     * 跳转到空间记忆考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qType);
        String[] list = spatialMemoryService.getQuestions(tpid, qType, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(test.render(list[0],list[1],questionTypeMap));
    }

    /**
     * 跳转到空间记忆考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qType);
        return ok(endTime.render(questionTypeMap,qType, "spatialMemory"));
    }

    /**
     * 空间记忆测试保存数据
     */
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
            answer.setQtype(answerJson.findPath("qType").asInt());
            answer.setClickNum(2);
            answer.setRightNum(answerJson.findPath("rightNum").asInt());
            answer.setClickTime(new Date());
            answer.setDateCreated(new Date());
            answerService.save(answer);
        }
        return ok();
    }

    /**
     * 跳转到空间记忆说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = spatialMemoryService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到空间记忆练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        List<String> list = spatialMemoryService.getPracticeManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间记忆练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = spatialMemoryService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间记忆考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = spatialMemoryService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间记忆考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = spatialMemoryService.getEndTimeManifest();
        return ok(manifest.render(list));
    }

}
