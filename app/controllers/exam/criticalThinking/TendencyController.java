package controllers.exam.criticalThinking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.AnswerService;
import service.QuestionTypeService;
import service.SessionService;
import service.exam.criticalThinking.TendencyService;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.common.test.endTime;
import views.html.common.test.mark;
import views.html.exam.criticalThinking.tendency.*;
import views.html.manifest;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2016/3/25.
 */
@org.springframework.stereotype.Controller
public class TendencyController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY;

    @Autowired
    private TendencyService tendencyService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到批判性思维倾向说明页面
     * @return
     */
    public Result instructions(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(instructions.render(questionTypeMap));
    }

    /**
     * 跳转到批判性思维倾向练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        Long size = tendencyService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        return ok(practiceEnd.render(questionTypeMap, size));
    }

    /**
     * 跳转到批判性思维倾向考试页面
     * @return
     */
    public Result test(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        List<Map<String, Object>> list = tendencyService.findQuestions(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String pageNum = "1/"+list.size();
        return ok(test.render(list, pageNum));
    }

    /**
     * 查看书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(mark.render(questionTypeMap, "criticalThinking/tendency"));
    }

    /**
     * 跳转到批判性思维倾向考试结束进度条页面
     * @return
     */
    public Result endTime(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(endTime.render(questionTypeMap, qtype, "ability"));
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
     * 跳转到批判性思维倾向练习结束说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> list = tendencyService.getInstructionsManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维倾向练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = tendencyService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维倾向考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = tendencyService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = tendencyService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维倾向考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = tendencyService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
