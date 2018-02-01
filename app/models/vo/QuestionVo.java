package models.vo;

import java.util.Date;

/**
 * Created by tina on 2015/8/11.
 */
public class QuestionVo {
    private String id;
    private String question;
    private String choices;
    private String choicesType;
    private String answer;
    private Integer qType;
    private String qTypeName;
    private String subType;
    private String difficulty;
    private String version;
    private Date dateCreated;
    private String scoringFormula;//计分方式
    private String jsonQuestionError; //导入题目错误记录
    private String A;
    private String B;
    private String C;
    private String D;

    public QuestionVo(){

    }
    public QuestionVo(String id,String question,String choices,String choicesType,String answer,Integer qType,String qTypeName,String subType,String difficulty,String version,Date dateCreated,String scoringFormula){
        this.id = id;
        this.question = question;
        this.choices = choices;
        this.choicesType = choicesType;
        this.answer = answer;
        this.qType = qType;
        this.qTypeName = qTypeName;
        this.subType = subType;
        this.difficulty = difficulty;
        this.version = version;
        this.dateCreated = dateCreated;
        this.scoringFormula = scoringFormula;
    }

    public QuestionVo(String id,String question,String choices,String choicesType,String answer,Integer qType,String qTypeName,String subType,String difficulty,String version,Date dateCreated){
        this.id = id;
        this.question = question;
        this.choices = choices;
        this.choicesType = choicesType;
        this.answer = answer;
        this.qType = qType;
        this.qTypeName = qTypeName;
        this.subType = subType;
        this.difficulty = difficulty;
        this.version = version;
        this.dateCreated = dateCreated;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getqTypeName() {
        return qTypeName;
    }

    public void setqTypeName(String qTypeName) {
        this.qTypeName = qTypeName;
    }

    public Integer getqType() {
        return qType;
    }

    public void setqType(Integer qType) {
        this.qType = qType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getChoicesType() {
        return choicesType;
    }

    public void setChoicesType(String choicesType) {
        this.choicesType = choicesType;
    }

    public String getScoringFormula() {
        return scoringFormula;
    }

    public void setScoringFormula(String scoringFormula) {
        this.scoringFormula = scoringFormula;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getJsonQuestionError() {
        return this.jsonQuestionError;
    }

    public void setJsonQuestionError(String jsonQuestionError) {
        this.jsonQuestionError = jsonQuestionError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionVo that = (QuestionVo) o;

        return id == that.id;

    }
}
