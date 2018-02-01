package models.dto;

import models.Answer;
import models.AnswerDetail;
import models.AnswerReport;

import java.util.List;

/**
 * Created by XIAODA on 2016/5/5.
 */
public class AnswerDto {
    private List<Answer> answers;
    private List<AnswerReport> answerReports;
    private List<AnswerDetail> answerDetails;

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<AnswerReport> getAnswerReports() {
        return answerReports;
    }

    public void setAnswerReports(List<AnswerReport> answerReports) {
        this.answerReports = answerReports;
    }

    public List<AnswerDetail> getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(List<AnswerDetail> answerDetails) {
        this.answerDetails = answerDetails;
    }
}
