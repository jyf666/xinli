package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * EduExperience entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "edu_experience")
public class EduExperience implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private String schoolCode;
	private String schoolName;
	private String grade;
	private String class_;
	private String studentNum;
	private Date dateCreated;
	private String useStatus;

	// Constructors

	/** default constructor */
	public EduExperience() {
	}

	/** minimal constructor */
	public EduExperience(Integer uid, Date dateCreated, String useStatus) {
		this.uid = uid;
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public EduExperience(Integer uid, String schoolCode, String schoolName,
			String class_, String studentNum, Date dateCreated,
			String useStatus) {
		this.uid = uid;
		this.schoolCode = schoolCode;
		this.schoolName = schoolName;
		this.class_ = class_;
		this.studentNum = studentNum;
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
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

	@Column(name = "school_code", length = 20)
	public String getSchoolCode() {
		return this.schoolCode;
	}

	public void setSchoolCode(String schoolCode) {
		this.schoolCode = schoolCode;
	}

	@Column(name = "school_name", length = 30)
	public String getSchoolName() {
		return this.schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	@Column(name = "grade", length = 10)
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Column(name = "class", length = 10)
	public String getClass_() {
		return this.class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	@Column(name = "student_num", length = 10)
	public String getStudentNum() {
		return this.studentNum;
	}

	public void setStudentNum(String studentNum) {
		this.studentNum = studentNum;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "use_status", nullable = false, length = 1)
	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}


}