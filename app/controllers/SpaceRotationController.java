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
import views.html.manifest;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import views.html.spaceRotation.instructions;
import views.html.spaceRotation.practice;
import views.html.spaceRotation.practiceEnd;
import views.html.spaceRotation.test;
import views.html.spaceRotation.mark;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/9/17.
 */
@org.springframework.stereotype.Controller
public class SpaceRotationController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_SPACE_ROTATION;

    @Autowired
    private SpaceRotationService spaceRotationService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到空间旋转说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到空间旋转练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        List<Map<String, Object>> list = spaceRotationService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到空间旋转练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        Long size = spaceRotationService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, size, timeMsg));
    }

    /**
     * 跳转到空间旋转考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String, Object>> list = spaceRotationService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 查看书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(mark.render(questionTypeMap));
    }

    /**
     * 跳转到空间旋转考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "spaceRotation"));
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
     * 跳转到空间旋转说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = spaceRotationService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到空间旋转练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = spaceRotationService.getPracticeManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间旋转练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = spaceRotationService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间旋转考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = spaceRotationService.getTestManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = spaceRotationService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到空间旋转考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = spaceRotationService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}

