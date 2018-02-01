package models;

import java.util.Date;
import javax.persistence.*;

/**
 * AdmissionInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admission_info")
public class AdmissionInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private Integer orgCode;
	private String orgName;
	private String testRoom;
	private String testNum;
	private String ip;
	private Date dateCreated;
	private String useStatus;

	// Constructors

	/** default constructor */
	public AdmissionInfo() {
	}

	/** minimal constructor */
	public AdmissionInfo(Integer uid, Integer orgCode, String orgName, Date dateCreated,
			String useStatus) {
		this.uid = uid;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public AdmissionInfo(Integer uid, Integer orgCode, String orgName, String testRoom,
			String testNum, Date dateCreated, String useStatus) {
		this.uid = uid;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.testRoom = testRoom;
		this.testNum = testNum;
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public AdmissionInfo(Integer uid, Integer orgCode, String orgName, String testRoom,
						 String testNum, String ip, Date dateCreated, String useStatus) {
		this.uid = uid;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.testRoom = testRoom;
		this.testNum = testNum;
		this.ip = ip;
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

	@Column(name = "org_code", nullable = false)
	public Integer getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	@Column(name = "org_name", length = 30)
	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Column(name = "test_room", length = 10)
	public String getTestRoom() {
		return this.testRoom;
	}

	public void setTestRoom(String testRoom) {
		this.testRoom = testRoom;
	}

	@Column(name = "test_num", length = 30)
	public String getTestNum() {
		return this.testNum;
	}

	public void setTestNum(String testNum) {
		this.testNum = testNum;
	}

	@Column(name = "ip", length = 15)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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