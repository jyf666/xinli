package models;

import javax.persistence.*;

/**
 * TestpaperQuestiontype entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "testpaper_questiontype")
public class TestpaperQuestiontype implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer qtid;
	private Integer tpid;
	private Integer seq;
	private Integer limitTime;

	// Constructors

	/** default constructor */
	public TestpaperQuestiontype() {
	}

	/** full constructor */
	public TestpaperQuestiontype(Integer qtid, Integer tpid, Integer seq) {
		this.qtid = qtid;
		this.tpid = tpid;
		this.seq = seq;
	}

	public TestpaperQuestiontype(Integer qtid, Integer tpid, Integer seq, Integer limitTime) {
		this.qtid = qtid;
		this.tpid = tpid;
		this.seq = seq;
		this.limitTime = limitTime;
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

	@Column(name = "qtid", nullable = false)
	public Integer getQtid() {
		return this.qtid;
	}

	public void setQtid(Integer qtid) {
		this.qtid = qtid;
	}

	@Column(name = "tpid", nullable = false)
	public Integer getTpid() {
		return this.tpid;
	}

	public void setTpid(Integer tpid) {
		this.tpid = tpid;
	}

	@Column(name = "seq", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "limit_time", nullable = false, columnDefinition = "INT(11) default 120")
	public Integer getLimitTime() {
		return this.limitTime;
	}

	public void setLimitTime(Integer limitTime) {
		this.limitTime = limitTime;
	}
}