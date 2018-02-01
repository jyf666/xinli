package models;

import java.util.Date;
import javax.persistence.*;

/**
 * AnswerDetail entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "answer_detail")
public class AnswerDetail implements java.io.Serializable {

    // Fields
    private Integer id;
    private Integer uid;
    private Integer tid;
    private String qid;
    private Integer qtype;
    private Integer fragenummer;
    private String operationType;
    private String answer;
    private long operationTime;
    private Date dateCreated;

    // Constructors

    /** default constructor */
    public AnswerDetail() {
    }

    /** minimal constructor */
    public AnswerDetail(Integer uid, Integer tid, String qid, Integer qtype,
                        String operationType, Date dateCreated) {
        this.uid = uid;
        this.tid = tid;
        this.qid = qid;
        this.qtype = qtype;
        this.operationType = operationType;
        this.dateCreated = dateCreated;
    }

    /** full constructor */
    public AnswerDetail(Integer uid, Integer tid, String qid, Integer qtype,Integer fragenummer,
                        String operationType, String answer, long operationTime,
                        Date dateCreated) {
        this.uid = uid;
        this.tid = tid;
        this.qid = qid;
        this.qtype = qtype;
        this.fragenummer = fragenummer;
        this.operationType = operationType;
        this.answer = answer;
        this.operationTime = operationTime;
        this.dateCreated = dateCreated;
    }

    // Property accessors
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "uid", nullable = false)
    public Integer getUid() {
        return this.uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Column(name = "tid", nullable = false)
    public Integer getTid() {
        return this.tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    @Column(name = "qid", nullable = false, length = 36)
    public String getQid() {
        return this.qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    @Column(name = "q_type", nullable = false)
    public Integer getQtype() {
        return this.qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    @Column(name = "fragenummer")
    public Integer getFragenummer() {
        return fragenummer;
    }

    public void setFragenummer(Integer fragenummer) {
        this.fragenummer = fragenummer;
    }

    @Column(name = "operation_type", nullable = false, length = 1)
    public String getOperationType() {
        return this.operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Column(name = "answer", length = 65535)
    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Column(name = "operation_time")
    public long getOperationTime() {
        return this.operationTime;
    }

    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", nullable = false, length = 19)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}