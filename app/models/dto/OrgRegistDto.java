package models.dto;

import models.Admin;
import models.AdmissionsOrg;
import models.Orders;
import models.Testpaper;

import java.util.List;

/**
 * Created by XIAODA on 2016/2/25.
 */
public class OrgRegistDto {

    private AdmissionsOrg admissionsOrg;
    private Admin admin;
    private Orders order;
    private String testpaperName;
    private List<Integer> questiontypeList;

    public OrgRegistDto(){}

    public OrgRegistDto(AdmissionsOrg admissionsOrg, Admin admin, Orders order, String testpaperName, List<Integer> questiontypeList) {
        this.admissionsOrg = admissionsOrg;
        this.admin = admin;
        this.order = order;
        this.testpaperName = testpaperName;
        this.questiontypeList = questiontypeList;
    }

    public AdmissionsOrg getAdmissionsOrg() {
        return admissionsOrg;
    }

    public void setAdmissionsOrg(AdmissionsOrg admissionsOrg) {
        this.admissionsOrg = admissionsOrg;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public String getTestpaperName() {
        return testpaperName;
    }

    public void setTestpaperName(String testpaperName) {
        this.testpaperName = testpaperName;
    }

    public List<Integer> getQuestiontypeList() {
        return questiontypeList;
    }

    public void setQuestiontypeList(List<Integer> questiontypeList) {
        this.questiontypeList = questiontypeList;
    }
}
