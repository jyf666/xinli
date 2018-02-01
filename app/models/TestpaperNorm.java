package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * TestpaperNorm entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "testpaper_norm")
public class TestpaperNorm implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer tpid;
	private Integer orgCode;
	private Integer age;
	private String sex;
	private String province;
	private String city;
	private String town;
	private Date dateCreated;

	// Constructors

	/** default constructor */
	public TestpaperNorm() {
	}

	/** minimal constructor */
	public TestpaperNorm(Integer tpid, Integer orgCode, Date dateCreated) {
		this.tpid = tpid;
		this.orgCode = orgCode;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public TestpaperNorm(Integer tpid, Integer orgCode, Integer age,
			String sex, Date dateCreated) {
		this.tpid = tpid;
		this.orgCode = orgCode;
		this.age = age;
		this.sex = sex;
		this.dateCreated = dateCreated;
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

	@Column(name = "tpid", nullable = false)
	public Integer getTpid() {
		return this.tpid;
	}

	public void setTpid(Integer tpid) {
		this.tpid = tpid;
	}

	@Column(name = "org_code", nullable = false)
	public Integer getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	@Column(name = "age")
	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Column(name = "sex", length = 1)
	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "province", length = 10)
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "city", length = 10)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "town", length = 10)
	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}