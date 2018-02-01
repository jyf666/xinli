package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.Question;
import models.Questiontype;
import play.libs.Json;
import play.mvc.*;
import org.springframework.beans.factory.annotation.Autowired;
import service.*;
import utils.DateUtils;
import utils.SystemConstant;
import views.html.manifest;
import views.html.memoryExtract.mark;
import views.html.common.test.endTime;
import java.util.*;


@org.springframework.stereotype.Controller
public class MaterialMemoryController extends Controller {

    @Autowired
    private MaterialMemoryService materialMemoryService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private AnswerService answerService;

    /**
     * 根据qtype跳转到相应的说明页面
     * @return
     */
    public Result instructions(Integer qtype){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        Long size = materialMemoryService.count(tpid, SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT, SystemConstant.QUESTION_ISPRACTICE_NO);
        if (SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY.equals(qtype)) {
            String timeMsg = questionTypeService.getTimeMsg(qtype);
            return ok(views.html.materialMemory.instructions.render(questionTypeMap, size, timeMsg));
        } else if (SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT.equals(qtype)) {
            return ok(views.html.memoryExtract.instructions.render(questionTypeMap, size));
        }
        return ok();
    }

    /**
     * 跳转到材料记忆或记忆提取考试页面
     * @return
     */
    public Result test(Integer qtype){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        if (SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY.equals(qtype)) {
            List<Question> list = materialMemoryService.getMaterialMemoryQuestion(tpid, SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT, SystemConstant.QUESTION_ISPRACTICE_NO);
            String pageNum = "1/" + list.size()/6;
            return ok(views.html.materialMemory.test.render(list, pageNum));
        } else if (SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT.equals(qtype)) {
            List<Map<String, List<Integer>>> list = materialMemoryService.getQuestion(tpid, qtype, SystemConstant.QUESTION_ISPRACTICE_NO);
            String pageNum = "1/" + list.size();
            return ok(views.html.memoryExtract.test.render(list, pageNum));
        }
        return ok();
    }

    /**
     * 跳转到材料记忆或记忆提取结束进度条页面
     * @return
     */
    public Result endTime(Integer qtype){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, qtype);
        if (SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY.equals(qtype)) {
            return ok(endTime.render(questionTypeMap, qtype, "materialMemory"));
        } else if (SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT.equals(qtype)) {
            return ok(endTime.render(questionTypeMap, qtype, "memoryExtract"));
        }
        return ok();
    }

    /**
     * 查看记忆提取书签
     */
    public Result mark(){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id

        Map<String, List<Questiontype>> questionTypeMap = questionTypeService.getQuestionTypeMapTpid(tpid, SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
        return ok(mark.render(questionTypeMap));
    }

    /**
     * 保存记忆提取测试答案
     */
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
     * 跳转到材料记忆或记忆提取说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(Integer qtype){
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<String> cacheList = materialMemoryService.getInstructionsManifest(tpid, qtype);
        return ok(manifest.render(cacheList));
    }

    /**
     * 跳转到材料记忆或记忆提取考试页面的Manifest文件
     * @return
     */
    public Result testManifest(Integer qtype){
        List<String> list = materialMemoryService.getTestManifest(qtype);
        return ok(manifest.render(list));
    }

    /**
     * 查看书签
     */
    public Result markManifest(){
        List<String> list = materialMemoryService.getMarkManifest();
        return ok(manifest.render(list));
    }

    /**
     * 跳转到材料记忆或记忆提取考试结束进度条页面的Manifest文件
     * @return
     */
    public Result endTimeManifest(Integer qtype){
        List<String> list = materialMemoryService.getEndTimeManifest();
        return ok(manifest.render(list));
    }


}
