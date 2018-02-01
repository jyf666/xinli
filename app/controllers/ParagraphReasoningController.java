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

import views.html.paragraphReasoning.instructions;
import views.html.paragraphReasoning.practice;
import views.html.paragraphReasoning.practiceEnd;
import views.html.paragraphReasoning.test;
import views.html.common.test.mark;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/9/9.
 */
@org.springframework.stereotype.Controller
public class ParagraphReasoningController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING;

    @Autowired
    private ParagraphReasoningService paragraphReasoningService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到段落推理说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到段落推理练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        List<Map<String, Object>> list = paragraphReasoningService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到段落推理练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        Long size = paragraphReasoningService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, size, timeMsg));
    }

    /**
     * 跳转到段落推理考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String, Object>> list = paragraphReasoningService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 查看书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(mark.render(questionTypeMap, "paragraphReasoning"));
    }

    /**
     * 跳转到段落推理考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "paragraphReasoning"));
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
     * 跳转到段落推理说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = paragraphReasoningService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到段落推理练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        List<String> list = paragraphReasoningService.getPracticeManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到段落推理练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = paragraphReasoningService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到段落推理考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = paragraphReasoningService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = paragraphReasoningService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到段落推理考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = paragraphReasoningService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
