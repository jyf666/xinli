package models.dto;

import models.Test;

import java.util.List;

/**
 * Created by XIAODA on 2016/1/12.
 */
public class OrganizeTestDto {

    private Integer testId;// 考试id
    private String testName;// 考试名称
    private Integer orgCode;// 机构编码
    private List<Test> tests;
    private List<UserDto> userDtos;

    public int getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<UserDto> getUserDtos() {
        return userDtos;
    }

    public void setUserDtos(List<UserDto> userDtos) {
        this.userDtos = userDtos;
    }
}
