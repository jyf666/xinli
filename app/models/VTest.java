package models;

import javax.persistence.*;
import java.util.Date;

/**
 * VTest entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "v_test")
public class VTest {

	private Integer id;
	private Integer pid;
	private String name;
	private Date startTime;
	private Date endTime;
	private String useStatus;
	private Integer orgCode;
	private String orgName;
	private String status;
	private Integer population;
	private Integer importedPopulation;
	private Short turn;
	private String report;
	private Long submitNum;

	/** default constructor */
	public VTest() {
	}

	/** full constructor */
	public VTest(Integer id, Integer pid, String name, Date startTime,
				   Date endTime, String useStatus, Integer orgCode,
				   String status, Integer population, Integer importedPopulation,
				   Short turn, String report, Long submitNum) {
		this.id = id;
		this.pid = pid;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.useStatus = useStatus;
		this.orgCode = orgCode;
		this.status = status;
		this.population = population;
		this.importedPopulation = importedPopulation;
		this.turn = turn;
		this.report = report;
		this.submitNum = submitNum;
	}

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
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", nullable = false, length = 19)
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "use_status", nullable = false, length = 1)
	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
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

	@Column(name = "status", nullable = false, length = 1)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "population", nullable = false)
	public Integer getPopulation() {
		return this.population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	@Column(name = "imported_population", nullable = false)
	public Integer getImportedPopulation() {
		return this.importedPopulation;
	}

	public void setImportedPopulation(Integer importedPopulation) {
		this.importedPopulation = importedPopulation;
	}

	@Column(name = "turn", nullable = false)
	public Short getTurn() {
		return this.turn;
	}

	public void setTurn(Short turn) {
		this.turn = turn;
	}

	@Column(name = "report", nullable = false, length = 1)
	public String getReport() {
		return this.report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	@Column(name = "submit_num", precision = 23, scale = 0)
	public Long getSubmitNum() {
		return this.submitNum;
	}

	public void setSubmitNum(Long submitNum) {
		this.submitNum = submitNum;
	}

}