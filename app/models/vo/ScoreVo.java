package models.vo;

/**
 * Created by wangbin on 2015/9/11.
 */
public class ScoreVo {
    private Integer qtype;
    private String qtypeName;
    private String introduce;
    private String originalScore;
    private String standardScore;
    private String dimension;
    private String score;
    private String rank;
    private Integer uid;
    private String userName;
    private String standardTenScore;

    public ScoreVo(){}
    public ScoreVo(Integer uid,String userName,Integer qtype,String qtypeName,String standardScore,String score,String dimension, String rank){
        this.uid = uid;
        this.userName = userName;
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.standardScore = standardScore;
        this.score = score;
        this.dimension = dimension;
        this.rank = rank;
        if(score.length() != 0) {
            Integer standardTenScoreInt = (int) (5.5 + 2 * Double.parseDouble(score) + 0.5);
            if (standardTenScoreInt <= 1) standardTenScoreInt = 1;
            if (standardTenScoreInt >= 10) standardTenScoreInt = 10;
            this.standardTenScore = String.valueOf(standardTenScoreInt);
        }
    }
    public ScoreVo(Integer qtype,String qtypeName,String introduce,String originalScore,String standardScore,String score,String dimension, String rank){
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.introduce = introduce;
        this.originalScore = originalScore;
        this.standardScore = standardScore;
        this.score = score;
        this.dimension = dimension;
        this.rank = rank;
    }

    public ScoreVo(Integer qtype,String qtypeName, String introduce,String standardScore, String score, String dimension, String rank){
        this.qtype=qtype;
        this.qtypeName =qtypeName;
        this.standardScore = standardScore;
        this.introduce = introduce;
        this.dimension = dimension;
        this.score = score;
        this.rank = rank;

    }
    public ScoreVo(Integer uid,String userName,Integer qtype,String qtypeName,String standardScore,String dimension, String rank){
        this.uid = uid;
        this.userName = userName;
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.standardScore = standardScore;
        this.dimension = dimension;
        this.rank = rank;
    }

    public ScoreVo(Integer uid,String userName,String qtypeName,String standardScore,String dimension, String rank){
        this.uid = uid;
        this.userName = userName;
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.standardScore = standardScore;
        this.dimension = dimension;
        this.rank = rank;
    }

    public Integer getQtype() {
        return qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    public String getQtypeName() {
        return qtypeName;
    }

    public void setQtypeName(String qtypeName) {
        this.qtypeName = qtypeName;
    }

    public String getOriginalScore() {
        return originalScore;
    }

    public void setOriginalScore(String originalScore) {
        this.originalScore = originalScore;
    }

    public String getStandardScore() {
        return standardScore;
    }

    public void setStandardScore(String standardScore) {
        this.standardScore = standardScore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public String getStandardTenScore(){ return this.standardTenScore;}

    public void setStandardTenScore(String standardTenScore){ this.standardTenScore = standardTenScore;}

}
