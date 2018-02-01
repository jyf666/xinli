package models.vo;

import java.util.Date;

/**
 * Created by wangbin on 2015/10/30.
 */
public class OrdersVo {
    private int id;
    private int orgCode;
    private int tpid;
    private int testNumber;
    private int ageLow;
    private int ageUpp;
    private String orgName;

    public OrdersVo(){}

    public OrdersVo(int id, int orgCode,int tpid,int testNumber,int ageLow,int ageUpp,String orgName){
        this.id = id;
        this.orgCode = orgCode;
        this.tpid = tpid;
        this.testNumber = testNumber;
        this.ageLow = ageLow;
        this.ageUpp = ageUpp;
        this.orgName = orgName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(int orgCode) {
        this.orgCode = orgCode;
    }

    public int getTpid() {
        return tpid;
    }

    public void setTpid(int tpid) {
        this.tpid = tpid;
    }

    public int getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(int testNumber) {
        this.testNumber = testNumber;
    }

    public int getAgeLow() {
        return ageLow;
    }

    public void setAgeLow(int ageLow) {
        this.ageLow = ageLow;
    }

    public int getAgeUpp() {
        return ageUpp;
    }

    public void setAgeUpp(int ageUpp) {
        this.ageUpp = ageUpp;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
