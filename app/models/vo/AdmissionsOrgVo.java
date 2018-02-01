package models.vo;

/**
 * Created by wangbin on 2015/9/21.
 */
public class AdmissionsOrgVo {
    private Integer orgId;
    private Integer adminId;
    private String orgName;
    private String orgProperty;
    private String adminName;
    private String adminDuty;
    private String adminEmail;
    private String adminPhone;
    private String orgAddress;
    private String orgDescription;

    public AdmissionsOrgVo(){}

    public AdmissionsOrgVo(Integer orgId,Integer adminId,String orgName,String orgProperty,String adminName,String adminDuty,String adminEmail,String adminPhone){
        this.orgId = orgId;
        this.adminId = adminId;
        this.orgName = orgName;
        if(orgProperty.equals("1"))
            this.orgProperty = "小学";
        if(orgProperty.equals("2"))
            this.orgProperty ="初中";
        if(orgProperty.equals("3"))
            this.orgProperty = "高中";
        if(orgProperty.equals("4"))
            this.orgProperty = "大学";
        this.adminName = adminName;
        this.adminDuty = adminDuty;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
    }

    public AdmissionsOrgVo(Integer orgId,String orgName,String adminName, String orgAddress,String orgDescription){
        this.orgId = orgId;
        this.orgName = orgName;
        this.adminName = adminName;
        this.orgAddress = orgAddress;
        this.orgDescription = orgDescription;
    }



    public String getAdminDuty() {
        return adminDuty;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public void setAdminDuty(String adminDuty) {
        this.adminDuty = adminDuty;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgProperty() {
        return orgProperty;
    }

    public void setOrgProperty(String orgProperty) {
        this.orgProperty = orgProperty;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public void setOrgDescription(String orgDescription) {
        this.orgDescription = orgDescription;
    }
}
