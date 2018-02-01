package models.vo;

import java.util.Date;

/**
 * Created by tina on 2015/8/10.
 */
public class TestVo {
    private  Integer id;

    private String excelDate;
    private String excelTurn;
    private String excelTime;
    private String excelPopulation;
    private String excelTestError;   //上传的考试数据是否异常    标识

    private Integer population;
    private Integer turn;
    private Integer pid;
    private String name;
    private Date startTime;
    private Date dateCreated;
    private String useStatus;
    private String orgName;
    private Integer orgCode;
    private String status;
    private String report;
    private Integer answerNoCommitNumber;
    private String stringDate;
    public TestVo(){

    }
    public TestVo(Integer tid,Integer orgCode){
        this.id = tid;
        this.orgCode = orgCode;
    }
    public TestVo(Integer id,Integer pid,String name,Date startTime,Date dateCreated,String useStatus,String orgName,Integer orgCode){
        this.id = id;
        this.pid =pid;
        this.name = name;
        this.startTime = startTime;
        this.dateCreated = dateCreated;
        this.useStatus = useStatus;
        this.orgName = orgName;
        this.orgCode = orgCode;
    }

    public TestVo(Integer id,Integer pid,String name,Date startTime,String useStatus,String orgName,Integer orgCode,String status,Integer population,Integer turn,String report){
        this.id = id;
        this.pid =pid;
        this.name = name;
        this.startTime = startTime;
        this.useStatus = useStatus;
        this.orgName = orgName;
        this.orgCode = orgCode;
        this.status = status;
        this.population = population;
        this.turn = turn;
        this.report = report;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getExcelDate() {
        return excelDate;
    }

    public void setExcelDate(String excelDate) {
        this.excelDate = excelDate;
    }

    public String getExcelTurn() {
        return excelTurn;
    }

    public void setExcelTurn(String excelTurn) {
        this.excelTurn = excelTurn;
    }

    public String getExcelTime() {
        return excelTime;
    }

    public void setExcelTime(String excelTime) {
        this.excelTime = excelTime;
    }

    public String getExcelPopulation() {
        return excelPopulation;
    }

    public void setExcelPopulation(String excelPopulation) {
        this.excelPopulation = excelPopulation;
    }

    public String getExcelTestError() {
        return excelTestError;
    }

    public void setExcelTestError(String excelTestError) {
        this.excelTestError = excelTestError;
    }

    public Integer getAnswerNoCommitNumber() {
        return answerNoCommitNumber;
    }

    public void setAnswerNoCommitNumber(Integer answerNoCommitNumber) {
        this.answerNoCommitNumber = answerNoCommitNumber;
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }
}
