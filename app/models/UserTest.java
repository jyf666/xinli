package models;

import javax.persistence.*;

/**
 * UserTestId entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_test")
public class UserTest implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private Integer tid;
	private String isover;

	/** default constructor */
	public UserTest() {
	}

	/** full constructor */
	public UserTest(Integer id, Integer uid, Integer tid) {
		this.id = id;
		this.uid = uid;
		this.tid = tid;
	}

	// Property accessors

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "isover", length = 1)
	public String getIsover() {
		return isover;
	}

	public void setIsover(String isover) {
		this.isover = isover;
	}
}