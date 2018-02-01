package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by XIAODA on 2016/2/26.
 */
@Entity
@Table(name = "v_admin_org")
public class VAdminOrg {

    private Integer id;
    private String loginName;
    private String password;
    private Integer orgCode;
    private String name;
    private String email;
    private String phone;
    private String duty;
    private Date lastLogintime;
    private String useStatus;
    private String orgName;
    private String province;
    private String city;
    private String town;
    private String property;
    private String address;
    private String description;

    /** default constructor */
    public VAdminOrg() {
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
