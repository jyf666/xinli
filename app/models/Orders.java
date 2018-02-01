package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangbin on 2015/10/30.
 */
@Entity
@Table(name = "orders")
public class Orders {
    private int id;
    private int adminId;
    private int orgCode;
    private int tpid;
    private int testNumber;
    private int remainNumber;
    private Date startTime;
    private Date endTime;
    private int ageLow;
    private int ageUpp;
    private Date dateCreated;
    private String status;
    private String testpaperName;// 测评方案名称
    private String agreement;

    public Orders(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "admin_id", unique = true, nullable = false)
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Column(name = "org_code", nullable = false)
    public int getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(int orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "tpid", nullable = false)
    public int getTpid() {
        return tpid;
    }

    public void setTpid(int tpid) {
        this.tpid = tpid;
    }

    @Column(name = "test_number", nullable = false)
    public int getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(int testNumber) {
        this.testNumber = testNumber;
    }

    @Column(name = "age_low", nullable = false)
    public int getAgeLow() {
        return ageLow;
    }

    public void setAgeLow(int ageLow) {
        this.ageLow = ageLow;
    }

    @Column(name = "age_upp", nullable = false)
    public int getAgeUpp() {
        return ageUpp;
    }

    public void setAgeUpp(int ageUpp) {
        this.ageUpp = ageUpp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", nullable = false, length = 19)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "status", nullable = false, length = 1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "remain_number", nullable = false)
    public int getRemainNumber() {
        return remainNumber;
    }

    public void setRemainNumber(int remainNumber) {
        this.remainNumber = remainNumber;
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

    @Column(name = "testpaper_name", length = 30)
    public String getTestpaperName() {
        return testpaperName;
    }

    public void setTestpaperName(String testpaperName) {
        this.testpaperName = testpaperName;
    }

    @Column(name = "agreement")
    public String getAgreement() {
        return this.agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

}
