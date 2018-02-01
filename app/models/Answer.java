package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * Answer entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "answer")
public class Answer implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private Integer tid;
	private String qid;
	private String answer;
	private Integer qtype;
	private String type;
	private Integer clickNum;
	private Integer rightNum;
	private Date clickTime;
	private Date dateCreated;

	// Constructors

	/** default constructor */
	public Answer() {
	}

	/** minimal constructor */
	public Answer(Integer uid, Integer tid, String qid, String answer,
			Timestamp dateCreated) {
		this.uid = uid;
		this.tid = tid;
		this.qid = qid;
		this.answer = answer;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public Answer(Integer uid, Integer tid, String qid, String answer,
			Integer qtype, String type, Integer clickNum, Integer rightNum,
			Date clickTime, Date dateCreated) {
		this.uid = uid;
		this.tid = tid;
		this.qid = qid;
		this.answer = answer;
		this.qtype = qtype;
		this.type = type;
		this.clickNum = clickNum;
		this.rightNum = rightNum;
		this.clickTime = clickTime;
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

	@Column(name = "answer", nullable = false, length = 65535)
	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Column(name = "q_type")
	public Integer getQtype() {
		return this.qtype;
	}

	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}

	@Column(name = "type", length = 1)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "click_num")
	public Integer getClickNum() {
		return this.clickNum;
	}

	public void setClickNum(Integer clickNum) {
		this.clickNum = clickNum;
	}

	@Column(name = "right_num")
	public Integer getRightNum() {
		return this.rightNum;
	}

	public void setRightNum(Integer rightNum) {
		this.rightNum = rightNum;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "click_time", length = 19)
	public Date getClickTime() {
		return this.clickTime;
	}

	public void setClickTime(Date clickTime) {
		this.clickTime = clickTime;
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