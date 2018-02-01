/***********************************************************************
 * Module:  UserService.java
 * Author:  XIAODA
 * Purpose: Defines the Class UserService
 ***********************************************************************/

package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.dto.OrganizeTestDto;
import models.dto.ResponseDto;
import models.dto.UserDto;
import models.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/** 考生相关业务
 * 
 * @pdOid b66c8081-639d-4e40-8766-9bbe7f445268 */
@Service
public class UserService {

   @Autowired
   private TestDao testDao;
   @Autowired
   private UserDao userDao;
   @Autowired
   private VUserInfoDao vUserInfoDao;
   @Autowired
   private AdmissionInfoDao admissionInfoDao;
   @Autowired
   private UserTestDao userTestDao;
   @Autowired
   private EduExperienceDao eduExperienceDao;


   public List<Test> findRelatedTests(User user) {
      return testDao.findByUserId(user.getId());
   }

   public User findById(Integer uid){
      return userDao.findById(uid);
   }


   public UserVo findUserVoByAccount(String account){
      UserVo userVo = userDao.findUserVoByAccount(account);
      return userVo;
   }

   public List<User> findByOrgCode(Integer orgCode){
      return userDao.findUserByOrgCode(orgCode);
   }

   public List<User> findByTid(Integer tid,Integer orgCode){
         return userDao.findUserByTidAndOrgCode(tid, orgCode);
   }

   public UserVo findUserVoByUid(Integer uid){
      return userDao.findUserVoByUidHaveGradeAndClass(uid);
   }

   public UserVo getUserVoByUid(Integer uid){
      UserVo userVo = userDao.findUserVoByUid(uid);
      Calendar calendar = Calendar.getInstance();
      Calendar calendar1 = Calendar.getInstance();
      Integer nowYear = calendar.get(Calendar.YEAR);
      calendar1.setTime(userVo.getBirthday());
      Integer birthYear = calendar1.get(Calendar.YEAR);
      userVo.setAge(nowYear-birthYear+1);
      return userVo;
   }

   /**
    * 分页查找考生
    * @param request
    * @param cols
    * @return
    */
   public Page<VUserInfo> findAll(Http.Request request, String[] cols){
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<VUserInfo> spec = DynamicSpecifications.fromRequest(request, VUserInfo.class);
      return vUserInfoDao.findAll(spec, pageable);
   }


   /** 获取考生集合
    *
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public void update(ObjectNode objectNode){
      try {
         User user = userDao.findById(objectNode.findPath("uid").asInt());
         user.setName(objectNode.findPath("name").asText());
         user.setAddress(objectNode.findPath("address").asText());
         user.setSex(objectNode.findPath("sex").asText());
         SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
         user.setBirthday(sdf.parse(objectNode.findPath("birth").asText()));
         user.setPhone(objectNode.findPath("phone").asText());
         user.setEmail(objectNode.findPath("email").asText());
         user.setIdCard(objectNode.findPath("idCard").asText());
         userDao.save(user);
         AdmissionInfo admissionInfo =  admissionInfoDao.findOne(SearchFilter.eq("uid", objectNode.findPath("uid").asInt()));
         admissionInfo.setTestNum(objectNode.findPath("number").asText());
         admissionInfo.setTestRoom(objectNode.findPath("room").asText());
         admissionInfoDao.save(admissionInfo);
      }catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** 获取某考试考生集合
    * @param orgCode 招生机构
    * @param page 页数
    * @param tid 考试id
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public Page<UserVo> getTestUserList(Integer tid,Integer orgCode,Integer page){
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      return userDao.findUserVoByTidAndOrgCode(tid, orgCode, pageable);
   }
   /** 获取某考试考生集合
    * @param orgCode 招生机构
    * @param tid 考试id
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public List<UserVo> getTestUserList(Integer tid,Integer orgCode){
      return userDao.findUserVoByTidAndOrgCode(tid, orgCode);
   }

   /** 获取某考试没有提交答案考生集合
    * @param orgCode 招生机构
    * @param tid 考试id
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public Page<UserVo> getTestNoAnswerUserList(Integer tid,Integer orgCode,Integer page){
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      return userDao.findUserVoByTidAndOrgCodeAndAnswerCommit(tid, orgCode, pageable);
   }

   /** 获取某机构的考生集合
    * @param orgId 机构ID
    * @param page 页数
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public Page<UserVo> getOrgUserList(Integer orgId,Integer page){
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      return userDao.findUserVoByOrgCode(orgId, pageable);
   }
   /** 获取某机构的考生集合
    * @param orgId 机构ID
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public List<UserVo> getOrgUserList(Integer orgId){
      return userDao.findUserVoByOrgCode(orgId);
   }



   /** 获取注册考生列表
    * @param page 页数
    *
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   public Page<UserVo> getUserRegList(Integer page){
      Pageable pageable = new PageRequest(page-1,SystemConstant.PAGESIZE);
      return userDao.findUserByReg(pageable);
   }

   /**
    * 更新使用状态
    * @param uid
    */
   @Transactional
   public void updateUseStatus(Integer uid, String useStatus){
      User user = userDao.findOne(uid);
      user.setUseStatus(useStatus);
      userDao.save(user);
      AdmissionInfo admissionInfo = admissionInfoDao.findOne(SearchFilter.eq("uid", uid));
      admissionInfo.setUseStatus(useStatus);
      admissionInfoDao.save(admissionInfo);
   }

   /** 导入考生excel文件
    * @param file Excel文件
    * @param orgCode 招生机构
    * @param tid 考试id
    * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
    * */
   @Transactional
   public void addByExcel(Integer tid,Integer orgCode,File file){
      try {
         List list = ExcelUtils.getObjectList(file, models.vo.UserVo.class);

         List<User> users = userDao.findUserByTidAndOrgCode(tid, orgCode);
         List<UserTest> userTests = userTestDao.findUserTestByTidAndOrgCode(tid, orgCode);
         List<AdmissionInfo> admissionInfos = admissionInfoDao.findAdmissionInfoByTidAndOrgCode(tid,orgCode);
         if(users.size() != 0 ) {
            userDao.delete(users);
            userTestDao.delete(userTests);
            admissionInfoDao.delete(admissionInfos);
         }
         userTests.clear();
         admissionInfos.clear();
         for(int i = 0;i<list.size();i++) {
            UserVo userVo = (UserVo)list.get(i);
            User user = new User();
            user.setName(userVo.getName());
            user.setAddress(userVo.getAddress());
            user.setSex(userVo.getSex());
            user.setBirthday(userVo.getBirthday());
            user.setPhone(userVo.getPhone());
            user.setEmail(userVo.getEmail());
            user.setDateCreated(new Date());
            user.setIdCard(userVo.getIdCard());
            user.setUseStatus("1");
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
            user.setLastLogintime(sdf.parse("1970-1-1"));
            user.setAccount("");
            user.setPassword("");
            user = userDao.save(user);
            AdmissionInfo admissionInfo = new AdmissionInfo();
            admissionInfo.setOrgCode(orgCode);
            admissionInfo.setTestNum(userVo.getTestNum());
            admissionInfo.setTestRoom(userVo.getTestRoom());
            admissionInfo.setUseStatus("1");
            admissionInfo.setUid(user.getId());
            admissionInfo.setDateCreated(new Date());
            admissionInfos.add(admissionInfo);
            UserTest userTest = new UserTest();
            userTest.setTid(tid);
            userTest.setUid(user.getId());
            userTests.add(userTest);
            user.setPassword(this.randomPassword(userVo.getIdCard()));
            user.setAccount(this.getAccount(user.getId()));
            userDao.save(user);
         }
         admissionInfoDao.save(admissionInfos);
         userTestDao.save(userTests);
      }catch (Exception e){
         e.printStackTrace();
      }
   }

   /**
    * 重新导入用户
    **/
   @Transactional
   public List<UserVo> uploadUserWithTest(ResponseDto responseDto, OrganizeTestDto organizeTestDto) {
      Integer tid = organizeTestDto.getTestId();

      Test test = testDao.findOne(tid);
      List<UserDto> userDtos = organizeTestDto.getUserDtos();
      List<UserVo> userVos = new ArrayList<UserVo>();
      for (UserDto userDto : userDtos) {
         int turn = userDto.getTurn();
         if (test.getTurn() == turn) {
            User user = userDto.getUser();
            String idCard = user.getIdCard();
            User oldUser = userDao.findOne(SearchFilter.eq("idCard", idCard));
            if(oldUser != null){
               oldUser.setName(user.getName());
               oldUser.setSex(user.getSex());
               oldUser.setBirthday(user.getBirthday());
               oldUser.setAddress(user.getAddress());
               oldUser.setPhone(user.getPhone());
               oldUser.setEmail(user.getEmail());
               oldUser.setUseStatus("1");
               user = userDao.save(oldUser);
            } else {
               user.setDateCreated(new Date());
               user.setUseStatus("1");
               user.setLastLogintime(DateUtils.parseDate("1700-01-01"));
               user.setAccount("-000000000");
               user.setPassword(this.randomPassword(user.getIdCard()));
               user = userDao.save(user);
               user.setAccount(this.getAccount(user.getId()));
               user = userDao.save(user);
            }

            Integer userId = user.getId();
            if(!admissionInfoDao.exists(SearchFilter.eq("uid", userId), SearchFilter.eq("useStatus", "1"))){
               AdmissionInfo admissionInfo = userDto.getAdmissionInfo();
               admissionInfo.setUid(userId);
               admissionInfo.setUseStatus("1");
               admissionInfo.setDateCreated(new Date());
               admissionInfoDao.save(admissionInfo);
            }

            if(!userTestDao.exists(SearchFilter.eq("tid", tid), SearchFilter.eq("uid", userId))){
               UserTest userTest = new UserTest();
               userTest.setTid(tid);
               userTest.setUid(userId);
               userTest.setIsover("0");
               userTestDao.save(userTest);
            }

            EduExperience eduExperience = userDto.getEduExperience();
            if(StringUtils.isNotBlank(eduExperience.getSchoolName()) || StringUtils.isNotBlank(eduExperience.getGrade()) || StringUtils.isNotBlank(eduExperience.getClass_()) || StringUtils.isNotBlank(eduExperience.getStudentNum())){
               eduExperience.setUid(userId);
               eduExperience.setUseStatus("1");
               eduExperience.setDateCreated(new Date());

               EduExperience oldEduExperience = eduExperienceDao.findOne(SearchFilter.eq("uid", userId), SearchFilter.eq("useStatus", "1"));
               if (oldEduExperience != null) {
                  oldEduExperience.setUseStatus("0");
               }
               eduExperienceDao.save(eduExperience);
            }

            userVos.add(new UserVo(turn, user, eduExperience, userDto.getAdmissionInfo()));
         }
      }
      responseDto.setSuccess(true);
      return userVos;
   }

   /**
    * 添加考生
    * @param objectNode 学生信息json对象
    **/
   @Transactional
   public void save(ObjectNode objectNode){
      try {
         User user = new User();
         user.setName(objectNode.findPath("name").asText());
         user.setAddress(objectNode.findPath("address").asText());
         user.setSex(objectNode.findPath("sex").asText());
         SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
         user.setBirthday(sdf.parse(objectNode.findPath("birth").asText()));
         user.setPhone(objectNode.findPath("phone").asText());
         user.setEmail(objectNode.findPath("email").asText());
         user.setDateCreated(new Date());
         user.setIdCard(objectNode.findPath("idCard").asText());
         user.setUseStatus("1");
         user.setLastLogintime(sdf.parse("1970-1-1"));
         user.setAccount("");
         user.setPassword("");
         user = userDao.save(user);
         AdmissionInfo admissionInfo = new AdmissionInfo();
         admissionInfo.setOrgCode(objectNode.findPath("orgCode").asInt());
         admissionInfo.setTestNum(objectNode.findPath("number").asText());
         admissionInfo.setTestRoom(objectNode.findPath("room").asText());
         admissionInfo.setUseStatus("1");
         admissionInfo.setUid(user.getId());
         admissionInfo.setDateCreated(new Date());
         admissionInfoDao.save(admissionInfo);
         UserTest userTest = new UserTest();
         userTest.setTid(objectNode.findPath("tid").asInt());
         userTest.setUid(user.getId());
         userTest.setIsover(SystemConstant.USER_TEST_ISOVER_NO);
         userTestDao.save(userTest);
         user.setPassword(this.randomPassword(objectNode.findPath("idCard").asText()));
         user.setAccount(this.getAccount(user.getId()));
         userDao.save(user);
      }catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * 生成密码
    * @param idCard
    * @return
    */
   private String randomPassword(String idCard){
      String result= idCard.substring(idCard.length()-6,idCard.length());
      return MD5Utils.getMD5Code(result);
   }

   /**
    * 生成帐号
    * @param id
    * @return
    */
   private String getAccount(Integer id){
      return Integer.toString(1000000 + id);
   }

   /**
    * 分页查询
    * @param pageNum
    * @param ids
    * @return
    */
   public Page<User> findPageByIds(int pageNum, List<Integer> ids) {
      Pageable pageable = new PageRequest(pageNum - 1, 5);
      return userDao.findAll(pageable, SearchFilter.in("id", ids));
   }

   /**
    * 导出用户到excel
    * */
   public HSSFWorkbook export(List<UserVo> userVos) {
      HSSFWorkbook userExcel = ExcelUtils.createExcel();
      HSSFSheet sheet = ExcelUtils.createSheet(userExcel, "用户列表");
      HSSFRow head = sheet.createRow(0);
      String[] titles = {"轮次", "姓名", "帐号", "密码", "性别", "出生年月", "身份证号", "生源地", "学校", "年级", "班级", "学号", "联系方式", "邮箱", "考场", "准考证号", "绑定信息"};
      for (int i = 0; i < titles.length; i++) {
         head.createCell(i).setCellValue(titles[i]);
      }

      if (userVos != null) {
         for (int i = 0; i < userVos.size(); i++) {
            UserVo userVo = userVos.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(userVo.getTurn());
            row.createCell(1).setCellValue(userVo.getName());
            row.createCell(2).setCellValue(userVo.getAccount());
            row.createCell(3).setCellValue(userVo.getPassword());
            row.createCell(4).setCellValue(userVo.getSex());
            row.createCell(5).setCellValue(userVo.getExcelBirth());
            row.createCell(6).setCellValue(userVo.getIdCard());
            row.createCell(7).setCellValue(userVo.getAddress());
            row.createCell(8).setCellValue(userVo.getSchoolName());
            row.createCell(9).setCellValue(userVo.getGrade());
            row.createCell(10).setCellValue(userVo.getClass_());
            row.createCell(11).setCellValue(userVo.getStudentNum());
            row.createCell(12).setCellValue(userVo.getPhone());
            row.createCell(13).setCellValue(userVo.getEmail());
            row.createCell(14).setCellValue(userVo.getTestRoom());
            row.createCell(15).setCellValue(userVo.getTestNum());
            row.createCell(16).setCellValue(userVo.getIp());
         }
      }

      return userExcel;
   }

   /**
    * 校验上传的excel
    **/
   public void validateExcel(List<UserVo> userVos, List<UserVo> errorUsers, List<UserVo> rightUsers, String ip){
      if (userVos != null && userVos.size() > 0) {
         for (UserVo userVo : userVos) {
            StringBuilder userErrorStr = new StringBuilder();
            //轮次验证
//            String turnReg = "^(第[一二三四五六七八九]百轮)" +
//                    "|(第[一二三四五六七八九]百[一二三四五六七八九]十[一二三四五六七八九]?轮)" +
//                    "|(第[一二三四五六七八九]百零[一二三四五六七八九]轮)" +
//                    "|(第[二三四五六七八九]?十[一二三四五六七八九]?轮)" +
//                    "|(第[一二三四五六七八九]轮)$";
            String turnReg = "^第[1-9][0-9]*轮$";
            if (StringUtils.isBlank(userVo.getTurn())) {
               userErrorStr.append("轮次不能为空 ");
            } else if (!userVo.getTurn().replaceAll(" ", "").matches(turnReg)) {
               userErrorStr.append("轮次格式错误 ");
            }
            //姓名验证
            if (StringUtils.isBlank(userVo.getName())) {
               userErrorStr.append("姓名不能为空 ");
            } else if (userVo.getName().length() >= 6) {
               userErrorStr.append("姓名过长 ");
            } else if (!userVo.getName().trim().matches("^[\u0391-\uFFE5]+$")) {
               userErrorStr.append("姓名输入汉字 ");
            }
            //身份证号验证
            if (StringUtils.isBlank(userVo.getIdCard())) {
               userErrorStr.append("身份证不能为空 ");
            } else if (!userVo.getIdCard().trim().matches("([0-9]{17}([0-9]|X))|([0-9]{15})")) {
               userErrorStr.append("身份证格式错误 ");
            }
            //性别验证
            if (StringUtils.isBlank(userVo.getSex())) {
               userErrorStr.append("性别不能为空 ");
            } else if (!(userVo.getSex().trim().equals("男") || userVo.getSex().trim().equals("女"))) {
               userErrorStr.append("性别输入错误 ");
            }
            //年级验证
            if (StringUtils.isBlank(userVo.getGrade())) {
               userErrorStr.append("年级不能为空 ");
            }
            //班级验证
            if (StringUtils.isBlank(userVo.getClass_())) {
               userErrorStr.append("班级不能为空 ");
            }
            //联系方式验证
            if (StringUtils.isBlank(userVo.getPhone())) {
//               userErrorStr.append("联系方式不能为空 ");
            } else if (!userVo.getPhone().trim().matches("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")) {
               userErrorStr.append("联系方式格式错误 ");
            }
            //邮箱验证
            if (StringUtils.isBlank(userVo.getEmail())) {
               userErrorStr.append("邮箱不能为空 ");
            } else if (!userVo.getEmail().trim().matches("^[0-9a-z][a-z0-9\\._-]{1,}@[a-z0-9-]{1,}[a-z0-9]\\.[a-z\\.]{1,}[a-z]$")) {
               userErrorStr.append("邮箱格式错误 ");
            }
            //绑定验证
            if (StringUtils.isBlank(userVo.getIp())){
               if (ip.equals("true")){
                  userErrorStr.append("绑定信息不能为空");
               }else{
//                  if (admissionInfoDao.findByip(userVo.getIp()) != null){
//                     userErrorStr.append("IP地址已经绑定");
//                  }
               }
            }else{
               if (ip.equals("false")){
                  userErrorStr.append("不需要填写绑定信息");
               }else{
                  if (admissionInfoDao.findOne(SearchFilter.eq("ip", userVo.getIp())) != null){
                     userErrorStr.append("IP地址已经绑定");
                  }
               }

            }
            userVo.setExcelUserError(userErrorStr.toString());
            if (StringUtils.isBlank(userErrorStr.toString())) {
               rightUsers.add(userVo);
            } else {
               errorUsers.add(userVo);
            }
         }
      }
   }
}