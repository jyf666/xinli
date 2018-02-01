package models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by XIAODA on 2015/12/28.
 */
@Entity
@Table(name = "v_user_info")
public class VUserInfo {

    private Integer id;
    private String account;
    private String password;
    private String name;
    private String sex;
    private Date birthday;
    private String address;
    private String phone;
    private String email;
    private Timestamp lastLogintime;
    private String photo;
    private String idCard;
    private Timestamp dateCreated;
    private Integer answerCommit;
    private Integer orgCode;
    private String testRoom;
    private String testNum;
    private String orgName;

    // Constructors

    /** default constructor */
    public VUserInfo() {
    }

    /** minimal constructor */
    public VUserInfo(Integer id, String account, String password,
                       String name, String sex, Date birthday, Timestamp lastLogintime,
                       Timestamp dateCreated, Integer answerCommit) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.lastLogintime = lastLogintime;
        this.dateCreated = dateCreated;
        this.answerCommit = answerCommit;
    }

    /** full constructor */
    public VUserInfo(Integer id, String account, String password,
                       String name, String sex, Date birthday, String address,
                       String phone, String email, Timestamp lastLogintime, String photo,
                       String idCard, Timestamp dateCreated, Integer answerCommit,
                       Integer orgCode, String testRoom, String testNum, String orgName) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.lastLogintime = lastLogintime;
        this.photo = photo;
        this.idCard = idCard;
        this.dateCreated = dateCreated;
        this.answerCommit = answerCommit;
        this.orgCode = orgCode;
        this.testRoom = testRoom;
        this.testNum = testNum;
        this.orgName = orgName;
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

    @Column(name = "last_logintime", nullable = false, length = 19)
    public Timestamp getLastLogintime() {
        return this.lastLogintime;
    }

    public void setLastLogintime(Timestamp lastLogintime) {
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

    @Column(name = "date_created", nullable = false, length = 19)
    public Timestamp getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "answer_commit", nullable = false)
    public Integer getAnswerCommit() {
        return this.answerCommit;
    }

    public void setAnswerCommit(Integer answerCommit) {
        this.answerCommit = answerCommit;
    }

    @Column(name = "org_code")
    public Integer getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
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

    @Column(name = "org_name", length = 30)
    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
