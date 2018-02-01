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
import service.exam.criticalThinking.AbilityService;
import service.QuestionTypeService;
import service.SessionService;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.common.test.endTime;
import views.html.common.test.mark;
import views.html.exam.criticalThinking.ability.instructions;
import views.html.exam.criticalThinking.ability.practiceEnd;
import views.html.manifest;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2016/3/14.
 */
@org.springframework.stereotype.Controller
public class AbilityController extends Controller {

    private final Integer qtype = SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY;
    public final static String SUB_TYPE_REASONING = "01";// 推理
    public final static String SUB_TYPE_ASSUMING = "02";// 假设辨认
    public final static String SUB_TYPE_DEDUCTIVE = "03";// 演绎推理
    public final static String SUB_TYPE_EXPLAIN = "04";// 解释
    public final static String SUB_TYPE_ASSESSMENT = "05";// 评估论证过程

    @Autowired
    private AbilityService abilityService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 跳转到批判性思维能力说明页面
     * @return
     */
    public Result instructions(String subType){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        if("home".equals(subType)){// 说明页首页
            return ok(instructions.render(questionTypeMap));
        } else if("reasoning".equals(subType)){// 推理
            return ok(views.html.exam.criticalThinking.ability.reasoning.instructions.render(questionTypeMap));
        } else if("assuming".equals(subType)){// 假设辨认
            return ok(views.html.exam.criticalThinking.ability.assuming.instructions.render(questionTypeMap));
        } else if("deductive".equals(subType)){// 演绎推理
            return ok(views.html.exam.criticalThinking.ability.deductive.instructions.render(questionTypeMap));
        } else if("explain".equals(subType)){// 解释
            return ok(views.html.exam.criticalThinking.ability.explain.instructions.render(questionTypeMap));
        } else if("assessment".equals(subType)){// 评估论证过程
            return ok(views.html.exam.criticalThinking.ability.assessment.instructions.render(questionTypeMap));
        } else {
            return notFound(views.html.error_404.render(request().method(), request().uri()));
        }
    }

    /**
     * 跳转到批判性思维能力练习结束说明页面
     * @return
     */
    public Result practiceEnd(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        Long size = abilityService.count(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
        String timeMsg = questionTypeService.getTimeMsg(qtype);
        return ok(practiceEnd.render(questionTypeMap, size, timeMsg));
    }

    /**
     * 跳转到批判性思维能力练习页面
     * @return
     */
    public Result practice(String subType){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);

        if("reasoning".equals(subType)){// 推理
            List<Map<String, Object>> list = abilityService.findQuestions(tpid, qtype, SUB_TYPE_REASONING, SystemConstant.QUESTION_ISPRACTICE_YES);
            String pageNum = "1/"+list.size();
            return ok(views.html.exam.criticalThinking.ability.reasoning.practice.render(questionTypeMap, list, pageNum));
        } else if("assuming".equals(subType)){// 假设辨认
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_ASSUMING, SystemConstant.QUESTION_ISPRACTICE_YES);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.assuming.practice.render(questionTypeMap, questionList, pageNum));
        } else if("deductive".equals(subType)){// 演绎推理
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_DEDUCTIVE, SystemConstant.QUESTION_ISPRACTICE_YES);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.deductive.practice.render(questionTypeMap, questionList, pageNum));
        } else if("explain".equals(subType)){// 解释
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_EXPLAIN, SystemConstant.QUESTION_ISPRACTICE_YES);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.explain.practice.render(questionTypeMap, questionList, pageNum));
        } else if("assessment".equals(subType)){// 评估论证过程
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_ASSESSMENT, SystemConstant.QUESTION_ISPRACTICE_YES);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.assessment.practice.render(questionTypeMap, questionList, pageNum));
        }
        return notFound(views.html.error_404.render(request().method(), request().uri()));
    }

    /**
     * 跳转到批判性思维能力考试页面
     * @return
     */
    public Result test(String subType){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        if("reasoning".equals(subType)){// 推理
            List<Map<String, Object>> list = abilityService.findQuestions(tpid, qtype, SUB_TYPE_REASONING, SystemConstant.QUESTION_ISPRACTICE_NO);
            String pageNum = "1/"+list.size();
            return ok(views.html.exam.criticalThinking.ability.reasoning.test.render(list, pageNum));
        } else if("assuming".equals(subType)){// 假设辨认
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_ASSUMING, SystemConstant.QUESTION_ISPRACTICE_NO);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.assuming.test.render(questionList, pageNum));
        } else if("deductive".equals(subType)){// 演绎推理
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_DEDUCTIVE, SystemConstant.QUESTION_ISPRACTICE_NO);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.deductive.test.render(questionList, pageNum));
        } else if("explain".equals(subType)){// 解释
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_EXPLAIN, SystemConstant.QUESTION_ISPRACTICE_NO);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.explain.test.render(questionList, pageNum));
        } else if("assessment".equals(subType)){// 评估论证过程
            List<Map<String, Object>> questions = abilityService.findQuestions(tpid, qtype, SUB_TYPE_ASSESSMENT, SystemConstant.QUESTION_ISPRACTICE_NO);
            List<Map<String, Object>> questionList = abilityService.getQuestionList(questions);
            String pageNum = "1/"+questionList.size();
            return ok(views.html.exam.criticalThinking.ability.assessment.test.render(questionList, pageNum));
        }
        return notFound(views.html.error_404.render(request().method(), request().uri()));
    }

    /**
     * 查看书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        return ok(mark.render(questionTypeMap, "criticalThinking/ability"));
    }

    /**
     * 跳转到批判性思维能力考试结束进度条页面
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
     * 跳转到批判性思维能力练习结束说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){
        List<String> list = abilityService.getInstructionsManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维能力练习页面的Manifest文件
     * @return
     */
    public Result practiceManifest(){
        List<String> list = abilityService.getPracticeManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维能力练习结束说明页面的Manifest文件
     * @return
     */
    public Result practiceEndManifest(){
        List<String> list = abilityService.getPracticeEndManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维能力考试页面的Manifest文件
     * @return
     */
    public Result testManifest(){
        List<String> list = abilityService.getTestManifest();
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = abilityService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到批判性思维能力考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(){
        List<String> list = abilityService.getEndTimeManifest();
        return ok(manifest.render(list));
    }
}
