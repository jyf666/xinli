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

import views.html.matrixReasoning.instructions;
import views.html.matrixReasoning.practice;
import views.html.matrixReasoning.practiceEnd;
import views.html.matrixReasoning.test;
import views.html.common.test.mark;
import views.html.common.test.endTime;

/**
 * Created by XIAODA on 2015/8/27.
 */
@org.springframework.stereotype.Controller
public class MatrixReasoningController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_MATRIX_REASONING;

    @Autowired
    private MatrixReasoningService matrixReasoningService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到矩阵推理说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到矩阵推理练习页面
     * @return
     */
    public Result practice(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        List<Map<String, Object>> list = matrixReasoningService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        String pageNum = "1/"+list.size();
        return ok(practice.render(questionTypeMap, list, pageNum));
    }

    /**
     * 跳转到矩阵推理练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        Long size = matrixReasoningService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, size, timeMsg));
    }

    /**
     * 跳转到矩阵推理考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Map<String, Object>> list = matrixReasoningService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 查看书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(mark.render(questionTypeMap, "matrixReasoning"));
    }

    /**
     * 跳转到矩阵推理考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer org_code = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);// 招生机构编码
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "matrixReasoning"));
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
            answer.setQtype(answerJson.findPath("qtype").asInt());
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
     * 跳转到矩阵推理说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> cacheList = matrixReasoningService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到矩阵推理练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        List<String> list = matrixReasoningService.getPracticeManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_YES);
        return ok(manifest.render(list));
    }

    /**
     * 跳转到矩阵推理练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = matrixReasoningService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到矩阵推理考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> list = matrixReasoningService.getTestManifest(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = matrixReasoningService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到矩阵推理考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = matrixReasoningService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
