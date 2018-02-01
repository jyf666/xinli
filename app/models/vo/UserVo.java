package models.vo;

import models.AdmissionInfo;
import models.EduExperience;
import models.User;
import utils.DateUtils;
import utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XIAODA on 2015/7/23.
 */
public class UserVo implements java.io.Serializable {

    private Integer uid;
    private String turn;   //页面提交的turn
    private String name;      // 姓名
    private String sex;       // 性别
    private String excelBirth;
    private String idCard; // 省份在号
    private String address;
    private String schoolName;// 学校
    private String grade;       //年级
    private String class_;      //班级
    private String studentNum;// 学号
    private String phone;
    private String email;
    private String testRoom;// 考场号
    private String testNum; // 准考证号
    private String ip; // 准考证号
    private int age;     // 年龄
    private String orgName;
    private String account;
    private String password;
    private Date birthday;
    private Integer outputTurn; //输出到页面上打印时 数据库取的turn
    private String excelUserError; //excel表格上传时异常用户的信息提示
    private Integer answerCommit;

    private String householdRegister;//户籍
    private String singleChild;
    private String socialAndEconomicStatus;



    public UserVo(){

    }

    public UserVo(Integer uid, String sex,String householdRegister, String singleChild, String socialAndEconomicStatus,String grade, String class_) {
        this.uid = uid;
        this.grade = grade;
        this.class_ = class_;
        this.householdRegister = householdRegister;
        this.singleChild = singleChild;
        this.socialAndEconomicStatus = socialAndEconomicStatus;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
    }

    public UserVo(Integer uid,String sex,String name,String idCard,String schoolName, Date birthday,String grade,String class_){
        this.uid = uid;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
        this.name = name;
        this.idCard = idCard;
        this.schoolName = schoolName;
        this.age = new Date().getYear() - birthday.getYear();
        this.grade = grade;
        this.class_ = class_;
    }

    public UserVo(Integer uid,String name,String account,String password,String email,String orgName){
        this.uid = uid;
        this.name = name;
        this.account = account;
        this.password = password;
        this.email = email;
        this.orgName = orgName;
    }
    public UserVo(Integer uid,String name,String account,String password,String email,String orgName,Integer answerCommit){
        this.uid = uid;
        this.name = name;
        this.account = account;
        this.password = password;
        this.email = email;
        this.orgName = orgName;
        this.answerCommit = answerCommit;
    }

    public UserVo(Integer uid, String account, String class_, String name, String sex, Date birthday, String grade) {
        this.uid = uid;
        this.account = account;
        this.class_ = class_;
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.age = DateUtils.getAge(birthday);
        this.grade = grade;
    }

    public UserVo(String name, String sex, Date birthday, String email, String testRoom, String testNum, String idCard) {
        this.name = name;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
        this.birthday = birthday;
        this.email = email;
        this.testRoom = testRoom;
        this.testNum = testNum;
        this.age = DateUtils.getAge(birthday);
        this.idCard = idCard;
    }
    public UserVo(String account,String name,String sex,Date birthday,String address,String phone,String email,String idCard,String testRoom,String testNum,String orgName){
        this.account = account;
        this.name = name;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
        this.birthday = birthday;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.idCard = idCard;
        this.testRoom = testRoom;
        this.testNum = testNum ;
        this.orgName = orgName;
    }

    public UserVo(String name,String grade,String class_,String account,String password,String email,Integer outputTurn,String idCard,Date birthday,String sex,String phone,String testRoom,String testNum,String address){
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM" );
        this.grade =grade;
        this.class_ = class_;
        this.account = account;
        this.password = "000000";
        this.turn = StringUtils.numberToString(outputTurn);
        this.name = name;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
        this.excelBirth = sdf.format(birthday);
        this.phone = phone;
        this.email = email;
        this.idCard = idCard;
        this.testRoom = testRoom;
        this.testNum = testNum ;
        this.address = address;

    }
    public UserVo(String name,String grade,String class_,String account,String password,String email,Integer outputTurn,String idCard,Date birthday,String sex,String phone,String testRoom,String testNum,String ip, String address){
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM" );
        this.grade =grade;
        this.class_ = class_;
        this.account = account;
        this.password = "000000";
        this.turn = StringUtils.numberToString(outputTurn);
        this.name = name;
        this.sex = sex.equals("0") || sex.equals("女")  ? "女" : "男";
        this.excelBirth = sdf.format(birthday);
        this.phone = phone;
        this.email = email;
        this.idCard = idCard;
        this.testRoom = testRoom;
        this.testNum = testNum ;
        this.ip = ip;
        this.address = address;

    }

    public UserVo(int turn, User user, EduExperience eduExperience, AdmissionInfo admissionInfo){
        this.turn = String.valueOf(turn);
        this.account = user.getAccount();
        this.password = user.getPassword();
        this.name = user.getName();
        this.sex = user.getSex();
        this.birthday = user.getBirthday();
        this.excelBirth = DateUtils.formatDate(birthday);
        this.idCard = user.getIdCard();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.email = user.getEmail();

        this.schoolName = eduExperience.getSchoolName();// 学校
        this.grade = eduExperience.getGrade();       //年级
        this.class_ = eduExperience.getClass_();      //班级
        this.studentNum = eduExperience.getStudentNum();// 学号

        this.testRoom = admissionInfo.getTestRoom();// 考场号
        this.testNum = admissionInfo.getTestNum(); // 准考证号
        this.ip = admissionInfo.getIp();//绑定ip
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTestRoom() {
        return testRoom;
    }

    public void setTestRoom(String testRoom) {
        this.testRoom = testRoom;
    }

    public String getTestNum() {
        return testNum;
    }

    public void setTestNum(String testNum) {
        this.testNum = testNum;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExcelUserError() {
        return excelUserError;
    }

    public void setExcelUserError(String excelUserError) {
        this.excelUserError = excelUserError;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getExcelBirth() {
        return excelBirth;
    }

    public void setExcelBirth(String excelBirth) {
        this.excelBirth = excelBirth;
    }

    public Integer getOutputTurn() {
        return outputTurn;
    }

    public void setOutputTurn(Integer outputTurn) {
        this.outputTurn = outputTurn;
    }

    public Integer getAnswerCommit() {
        return answerCommit;
    }

    public void setAnswerCommit(Integer answerCommit) {
        this.answerCommit = answerCommit;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getHouseholdRegister() {
        return householdRegister;
    }

    public void setHouseholdRegister(String householdRegister) {
        this.householdRegister = householdRegister;
    }

    public String getSingleChild() {
        return singleChild;
    }

    public void setSingleChild(String singleChild) {
        this.singleChild = singleChild;
    }

    public String getSocialAndEconomicStatus() {
        return socialAndEconomicStatus;
    }

    public void setSocialAndEconomicStatus(String socialAndEconomicStatus) {
        this.socialAndEconomicStatus = socialAndEconomicStatus;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserVo userVo = (UserVo) o;

        return uid.equals(userVo.uid);

    }

}
