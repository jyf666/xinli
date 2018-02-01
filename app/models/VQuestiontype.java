package models;

import javax.persistence.*;

/**
 * Created by lintongyu on 2016/1/7.
 */
@Entity
@Table(name = "v_questiontype")
public class VQuestiontype {
    private Integer id;
    private String name;
    private String introduce;
    private Integer limitTime;
    private String scoringFormula;
    private String userStatus;
    private Integer questionNumber;
    private String type;

    // Constructors

    /** default constructor */
    public VQuestiontype() {
    }

    public VQuestiontype(Integer id, String name,
                         Integer limitTime) {
        this.id = id;
        this.name = name;
        this.limitTime = limitTime;
    }

    /** minimal constructor */
    public VQuestiontype(Integer id, String name, String introduce,
                         Integer limitTime, String scoringFormula, Integer questionNumber,
                         String type) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.limitTime = limitTime;
        this.scoringFormula = scoringFormula;
        this.questionNumber = questionNumber;
        this.type = type;
    }

    /** full constructor */


    // Property accessors

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() { return this.id; }

    public void setId(Integer id) { this.id = id; }

    @Column(name = "name", nullable = false, length = 25)
    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    @Column(name = "introduce", nullable = false, length = 100)
    public String getIntroduce() { return this.introduce; }

    public void setIntroduce(String introduce) { this.introduce = introduce; }

    @Column(name = "limitTime", nullable = false, length = 10)
    public Integer getLimitTime() { return this.limitTime; }

    public void setLimitTime(Integer limitTime) { this.limitTime = limitTime; }

    @Column(name = "scoringFormula", length = 200)
    public String getScoringFormula() { return this.scoringFormula; }

    public void setScoringFormula(String scoringFormula) { this.scoringFormula = scoringFormula; }

    @Column(name = "userStatus", length = 1)
    public String getUserStatus() { return this.userStatus; }

    public void setUserStatus(String scoringFormula) { this.userStatus = userStatus; }

    @Column(name = "questionNumber", nullable = false)
    public Integer getQuestionNumber(){ return this.questionNumber;}

    public void setQuestionNumber(Integer questionNumber){this.questionNumber = questionNumber;}

    @Column(name = "type",length = 1)
    public String getType(){return this.type;}

    public void setType(String type){this.type = type;}

}
