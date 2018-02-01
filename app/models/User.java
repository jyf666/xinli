package models;

import javax.persistence.*;
import java.util.Date;

/**
 * User entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user")
public class User implements java.io.Serializable {

	// Fields

	private Integer id;
	private String account;
	private String password;
	private String name;
	private String sex;
	private Date birthday;
	private String address;
	private String phone;
	private String email;
	private String idCard;
	private String useStatus;
	private Date lastLogintime;
	private String photo;
	private Date dateCreated;

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String account, String password, String name, String sex,
			Date birthday, String useStatus, Date lastLogintime,
			Date dateCreated) {
		this.account = account;
		this.password = password;
		this.name = name;
		this.sex = sex;
		this.birthday = birthday;
		this.useStatus = useStatus;
		this.lastLogintime = lastLogintime;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public User(String account, String password, String name, String sex,
			Date birthday, String address, String phone, String email,
			String useStatus, Date lastLogintime, String photo,
			String idCard, Date dateCreated) {
		this.account = account;
		this.password = password;
		this.name = name;
		this.sex = sex;
		this.birthday = birthday;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.useStatus = useStatus;
		this.lastLogintime = lastLogintime;
		this.photo = photo;
		this.idCard = idCard;
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

	@Column(name = "account", nullable = false, length = 100)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "password", nullable = false, length = 35)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "name", nullable = false, length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sex", nullable = false, length = 1)
	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "birthday", nullable = false, length = 10)
	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Column(name = "address", length = 100)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "phone", length = 20)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "email", length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "use_status", nullable = false, length = 1)
	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_logintime", nullable = false, length = 19)
	public Date getLastLogintime() {
		return this.lastLogintime;
	}

	public void setLastLogintime(Date lastLogintime) {
		this.lastLogintime = lastLogintime;
	}

	@Column(name = "photo", length = 200)
	public String getPhoto() {
		return this.photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Column(name = "id_card", length = 20)
	public String getIdCard() {
		return this.idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
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