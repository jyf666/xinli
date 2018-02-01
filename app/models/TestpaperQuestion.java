package models;

import javax.persistence.*;

/**
 * TestpaperQuestion entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "testpaper_question")
public class TestpaperQuestion implements java.io.Serializable {

	// Fields

	private Integer id;
	private String qid;
	private Integer tpid;

	// Constructors

	/** default constructor */
	public TestpaperQuestion() {
	}

	/** full constructor */
	public TestpaperQuestion(String qid, Integer tpid) {
		this.qid = qid;
		this.tpid = tpid;
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

	@Column(name = "qid", nullable = false, length = 36)
	public String getQid() {
		return this.qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	@Column(name = "tpid", nullable = false)
	public Integer getTpid() {
		return this.tpid;
	}

	public void setTpid(Integer tpid) {
		this.tpid = tpid;
	}

}