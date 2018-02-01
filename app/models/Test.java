package models;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * Test entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "test")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Test implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer pid;
	private Integer orgCode;
	private String orgName;
	private String name;
	private Date startTime;
	private Date endTime;
	private Date dateCreated;
	private String normData;
	private String useStatus;
	private String status;
	private int population;
	private Integer turn;
	private String report;
	private int importedPopulation;// 已导入考试的考生人数

	// Constructors

	/** default constructor */
	public Test() {
	}

	/** minimal constructor */
	public Test(Integer pid, Integer orgCode, String orgName, Date startTime,
			Date dateCreated, String useStatus) {
		this.pid = pid;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.startTime = startTime;
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public Test(Integer pid, Integer orgCode, String orgName, String name, Date startTime,
			Date dateCreated, String useStatus) {
		this.pid = pid;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.name = name;
		this.startTime = startTime;
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

	@Column(name = "pid", nullable = false)
	public Integer getPid() {
		return this.pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
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

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time", nullable = false, length = 19)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", nullable = false, length = 19)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	@Column(name = "norm_data", nullable = false, length = 1000)
	public String getNormData() {
		return normData;
	}

	public void setNormData(String normData) {
		this.normData = normData;
	}

	@Column(name = "status", nullable = false, length = 1)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "population", nullable = false)
	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	@Column(name = "turn", nullable = false)
	public Integer getTurn() {
		return turn;
	}

	public void setTurn(Integer turn) {
		this.turn = turn;
	}

	@Column(name = "report", nullable = false, length = 1)
	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	@Column(name = "imported_population", nullable = false)
	public int getImportedPopulation() {
		return importedPopulation;
	}

	public void setImportedPopulation(int importedPopulation) {
		this.importedPopulation = importedPopulation;
	}
}