package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Testpaper entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "testpaper")
public class Testpaper implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer orgCode;
	private Integer expectTime;
	private String isReference;
	private String name;
	private Date dateCreated;
	private String useStatus;

	// Constructors

	/** default constructor */
	public Testpaper() {
	}

	/** minimal constructor */
	public Testpaper(Integer qid, Integer QType, Integer orgCode,
			Date dateCreated) {
		this.orgCode = orgCode;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public Testpaper(Integer qid, Integer QType, Integer orgCode,
			Integer expectTime, Date dateCreated, String useStatus) {
		this.orgCode = orgCode;
		this.expectTime = expectTime;
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

	@Column(name = "org_code", nullable = false)
	public Integer getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	@Column(name = "expect_time")
	public Integer getExpectTime() {
		return this.expectTime;
	}

	public void setExpectTime(Integer expectTime) {
		this.expectTime = expectTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "use_status", length = 1)
	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	@Column(name = "is_reference", length = 1)
	public String getIsReference() {
		return isReference;
	}

	public void setIsReference(String isReference) {
		this.isReference = isReference;
	}

	@Column(name = "name", length = 30)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}