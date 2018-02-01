package models.vo;

/**
 * Created by wangbin on 2015/11/2.
 */
public class QuestiontypeVo {
    private Integer id;
    private String name;
    private String introduce;
    private Integer limitTime;
    private String scoringFormula;
    private String userStatus;
    private Integer questionNumber;
    private String type;

    public QuestiontypeVo(){}

    public QuestiontypeVo(Integer id, String name,String introduce,Integer limitTime,String scoringFormula,String userStatus,Integer questionNumber){
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.limitTime = limitTime;
        this.scoringFormula = scoringFormula;
        this.questionNumber = questionNumber;
        this.userStatus = userStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Integer limitTime) {
        this.limitTime = limitTime;
    }

    public String getScoringFormula() {
        return scoringFormula;
    }

    public void setScoringFormula(String scoringFormula) {
        this.scoringFormula = scoringFormula;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
