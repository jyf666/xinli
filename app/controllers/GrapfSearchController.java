package controllers;

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
import utils.SystemConstant;
import views.html.common.test.endTime;
import views.html.grapfSearch.test;
import views.html.grapfSearch.practice;
import views.html.grapfSearch.instructions;
import views.html.grapfSearch.practiceEnd;
import views.html.manifest;

import java.util.*;

/**
 * Created by XIAODA on 2015/7/14.
 */
@org.springframework.stereotype.Controller
public class GrapfSearchController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_GRAPF_SEARCH;

    @Autowired
    private GrapfSearchService grapfSearchService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到图片搜索说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到图片搜索练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        List<Map<String,List<List<String>>>> list = grapfSearchService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到图片搜索练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, timeMsg));
    }

    /**
     * 跳转到图片搜索考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String,List<List<String>>>> list = grapfSearchService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 跳转到图片搜索考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "grapfSearch"));
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
            answer.setQtype(answerJson.findPath("qType").asInt());
            answer.setClickNum(answerJson.findPath("clickNum").asInt());
            answer.setRightNum(answerJson.findPath("rightNum").asInt());
            answer.setClickTime(DateUtils.parseDate(answerJson.findPath("clickTime").asText()));
            answer.setDateCreated(new Date());
            answerService.save(answer);
        }
//        answerReportService.saveOrUpdate(uid, tid, qtype, null);
        return ok();
    }


    /**
     * 跳转到图片搜索说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = grapfSearchService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到图片搜索练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = grapfSearchService.getPracticeManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到图片搜索练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = grapfSearchService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到图片搜索考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = grapfSearchService.getTestManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到图片搜索考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = grapfSearchService.getEndTimeManifest();
        return ok(manifest.render(list));
    }

}
