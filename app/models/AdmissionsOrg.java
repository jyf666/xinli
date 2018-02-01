package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * AdmissionsOrg entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admissions_org")
public class AdmissionsOrg implements java.io.Serializable {

	// Fields

	private Integer id;
	private String orgName;
	private String province;
	private String city;
	private String town;
	private String property;
	private String address;
	private String description;
	private Date dateCreated;
	private String useStatus;

	// Constructors

	/** default constructor */
	public AdmissionsOrg() {
	}

	/** minimal constructor */
	public AdmissionsOrg(Date dateCreated, String useStatus) {
		this.dateCreated = dateCreated;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public AdmissionsOrg(String orgName, String address, String description,
			Date dateCreated, String useStatus) {
		this.orgName = orgName;
		this.address = address;
		this.description = description;
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

	@Column(name = "org_name", length = 30)
	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Column(name = "address", length = 100)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "description", length = 1000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Column(name = "province", nullable = false, length = 10)
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	@Column(name = "city", nullable = false, length = 10)
	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "town", nullable = false, length = 10)
	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	@Column(name = "property", nullable = false, length = 1)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}