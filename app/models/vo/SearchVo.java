package models.vo;

/**
 * Created by tina on 2015/8/17.
 */
public class SearchVo {
    private Integer orgCode;
    private String testTime ;
    private String startTime ;
    private String name;
    private String account;
    private String email;
    public SearchVo(){}

    public SearchVo(Integer orgCode,String name,String account,String email){
        this.orgCode = orgCode;
        this.name = name;
        this.account = account;
        this.email = email;
    }
    public SearchVo(Integer orgCode,String testTime,String startTime){
        this.orgCode = orgCode;
        this.testTime = testTime;
        this.startTime =startTime;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
