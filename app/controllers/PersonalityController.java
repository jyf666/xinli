package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.manifest;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import views.html.personality.instructions;
import views.html.personality.practiceEnd;
import views.html.personality.test;
import views.html.personality.mark;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/9/11.
 */
@org.springframework.stereotype.Controller
public class PersonalityController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_PERSONALITY;

    @Autowired
    private PersonalityService personalityService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到人格测试说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到人格测试说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        Long size = personalityService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(practiceEnd.render(questionTypeMap, size));
    }

    /**
     * 跳转到人格测试考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String, Object>> list = personalityService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
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
     * 跳转到人格测试考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "personality"));
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
     * 跳转到人格测试说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = personalityService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }
    
    /**
     * 跳转到人格测试练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = personalityService.practiceEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到人格测试考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = personalityService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = personalityService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到人格测试考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = personalityService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
