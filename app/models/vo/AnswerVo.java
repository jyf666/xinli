package models.vo;

import java.util.Date;

/**
 * Created by wangbin on 2015/8/20.
 */
public class AnswerVo {
    private String qid;
    private String question;
    private String answer;
    private String userAnswer;
    private Integer qType;
    private Integer clickNum;
    private Integer rightNum;
    private Date clickTime;
    private Integer dimension;
    public AnswerVo(){

    }

    public AnswerVo(String qid,String question,String answer,String userAnswer,Integer qType){
        this.qid = qid;
        this.question = question;
        this.answer = answer;
        this.userAnswer = userAnswer;
        this.qType = qType;
    }

    public AnswerVo(String qid,String question,String answer,String userAnswer,Integer qType, Integer dimension, Integer clickNum,Integer rightNum,Date clickTime){
        this.qid = qid;
        this.question = question;
        this.answer = answer;
        this.userAnswer = userAnswer;
        this.qType = qType;
        this.clickNum =clickNum;
        this.rightNum = rightNum;
        this.clickTime = clickTime;
        this.dimension = dimension;
    }

    public Integer getqType() {
        return qType;
    }

    public void setqType(Integer qType) {
        this.qType = qType;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getClickNum() {
        return clickNum;
    }

    public void setClickNum(Integer clickNum) {
        this.clickNum = clickNum;
    }

    public Integer getRightNum() {
        return rightNum;
    }

    public void setRightNum(Integer rightNum) {
        this.rightNum = rightNum;
    }

    public Date getClickTime() {
        return clickTime;
    }

    public void setClickTime(Date clickTime) {
        this.clickTime = clickTime;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }
}

