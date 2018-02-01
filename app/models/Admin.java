package models;

import be.objectify.deadbolt.core.models.*;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * Admin entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Admin implements java.io.Serializable, Subject {

	// Fields

	private Integer id;
	private String loginName;
	private String password;
	private Integer orgCode;
	private String orgName;
	private String name;
	private String email;
	private String phone;
	private String duty;
	private Date dateCreate;
	private Date lastLogintime;
	private String useStatus;

	private List<models.Role> roles;

	// Constructors

	/** default constructor */
	public Admin() {
	}

	/** minimal constructor */
	public Admin(String loginName, String password, Integer orgCode, String orgName,
				 Date dateCreate, String useStatus) {
		this.loginName = loginName;
		this.password = password;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.dateCreate = dateCreate;
		this.useStatus = useStatus;
	}

	/** full constructor */
	public Admin(String loginName, String password, Integer orgCode, String orgName,
				 String name, String email, String phone, Date dateCreate,
				 Date lastLogintime, String useStatus) {
		this.loginName = loginName;
		this.password = password;
		this.orgCode = orgCode;
		this.orgName = orgName;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dateCreate = dateCreate;
		this.lastLogintime = lastLogintime;
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

	@Column(name = "login_name", nullable = false, length = 15)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "password", nullable = false, length = 35)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	@Column(name = "name", length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "email", length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "phone", length = 20)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_create", nullable = false, length = 19)
	public Date getDateCreate() {
		return this.dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_logintime", length = 19)
	public Date getLastLogintime() {
		return this.lastLogintime;
	}

	public void setLastLogintime(Date lastLogintime) {
		this.lastLogintime = lastLogintime;
	}

	@Column(name = "use_status", nullable = false, length = 1)
	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}
	@Column(name = "duty", nullable = false, length = 10)
	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	// 多对多定义
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	// Fecth策略定义
	@Fetch(FetchMode.SUBSELECT)
	// 集合按id排序
	@OrderBy("id ASC")
	// 缓存策略
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Transient
	@Override
	public List<Permission> getPermissions(){
		if(roles != null && roles.size()>0){
			Set<Permission> permissionSet = Sets.newHashSet();
			for(Role role : roles){
				permissionSet.addAll(role.getPermissions());
			}
			return new ArrayList(permissionSet);
		}
		return null;
	}

	@Transient
	@Override
	public String getIdentifier()
	{
		return String.valueOf(id);
	}
}