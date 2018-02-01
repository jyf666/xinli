package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Answer;
import models.AnswerReport;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import service.AnswerReportService;
import service.SessionService;
import utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by XIAODA on 2015/8/14.
 */
@org.springframework.stereotype.Controller
public class AnswerReportController extends Controller {

    @Autowired
    private AnswerReportService answerReportService;

    public Result add(Integer qtype){
        Integer uid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.USER_ID);// 考生id
        Integer tid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TEST_ID);// 考试表id

        answerReportService.saveOrUpdate(uid, tid, qtype, null);
        return ok();
    }

    /**
     * 上传离线数据
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result upload(){
        JsonNode answerReportListJson = request().body().asJson();
        Iterator iterator = answerReportListJson.iterator();
        List<AnswerReport> list = new ArrayList();
        while (iterator.hasNext()) {
            ObjectNode answerReportJson = (ObjectNode) iterator.next();
            AnswerReport answerReport = new AnswerReport();
            answerReport.setUid(answerReportJson.findPath("uid").asInt());
            answerReport.setTid(answerReportJson.findPath("tid").asInt());
            answerReport.setQtype(answerReportJson.findPath("qtype").asInt());
            answerReport.setStartTime(DateUtils.parseDateTime(answerReportJson.findPath("startTime").asText()));
            answerReport.setCommitTime(DateUtils.parseDateTime(answerReportJson.findPath("endTime").asText()));
            list.add(answerReport);
        }
        if (list.size() > 0) {
            answerReportService.save(list);
        }
        return ok();
    }
}
