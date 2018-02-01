package models.dto;

import models.AdmissionInfo;
import models.EduExperience;
import models.User;
import models.vo.UserVo;

import java.util.List;

/**
 * Created by XIAODA on 2016/1/6.
 */
public class UserDto {

    private int turn;// 考试轮次
    private List<UserVo> userVos;
    private User user;
    private AdmissionInfo admissionInfo;
    private EduExperience eduExperience;

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<UserVo> getUserVos() {
        return userVos;
    }

    public void setUserVos(List<UserVo> userVos) {
        this.userVos = userVos;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AdmissionInfo getAdmissionInfo() {
        return admissionInfo;
    }

    public void setAdmissionInfo(AdmissionInfo admissionInfo) {
        this.admissionInfo = admissionInfo;
    }

    public EduExperience getEduExperience() {
        return eduExperience;
    }

    public void setEduExperience(EduExperience eduExperience) {
        this.eduExperience = eduExperience;
    }
}
