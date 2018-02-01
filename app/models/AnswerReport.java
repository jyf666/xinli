package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * AnswerReport entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "answer_report")
public class AnswerReport implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private Integer tid;
	private Integer qtype;
	private Date startTime;
	private Date commitTime;
	private Integer complete;

	// Constructors

	/** default constructor */
	public AnswerReport() {
	}

	/** minimal constructor */
	public AnswerReport(Integer uid, Integer tid, Integer qtype) {
		this.uid = uid;
		this.tid = tid;
		this.qtype = qtype;
	}

	/** full constructor */
	public AnswerReport(Integer uid, Integer tid, Integer qtype,
			Date startTime, Date commitTime, Integer complete) {
		this.uid = uid;
		this.tid = tid;
		this.qtype = qtype;
		this.startTime = startTime;
		this.commitTime = commitTime;
		this.complete = complete;
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

	@Column(name = "q_type", nullable = false)
	public Integer getQtype() {
		return this.qtype;
	}

	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time", length = 19)
	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "commit_time", length = 19)
	public Date getCommitTime() {
		return this.commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	@Column(name = "complete")
	public Integer getComplete() {
		return this.complete;
	}

	public void setComplete(Integer complete) {
		this.complete = complete;
	}

}