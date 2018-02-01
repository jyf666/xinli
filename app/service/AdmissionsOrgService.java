/***********************************************************************
 * Module:  AdmissionsOrgService.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdmissionsOrgService
 ***********************************************************************/

package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.dto.OrgRegistDto;
import models.vo.AdmissionsOrgVo;
import models.vo.UserVo;
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
import play.mvc.Result;
import utils.MailUtils;
import utils.PageUtils;
import utils.SystemConstant;

import java.util.*;

/** 招生机构业务类
 * 
 * @pdOid 97ed9624-904b-475f-b4d7-1fd13c0fa2a3 */
@Service
public class AdmissionsOrgService {

   @Autowired
   private AdmissionsOrgDao admissionsOrgDao;
   @Autowired
   private UserDao userDao;
   @Autowired
   private AdminDao adminDao;
   @Autowired
   private OrdersDao ordersDao;
   @Autowired
   private TestpaperDao testpaperDao;
   @Autowired
   private TestpaperQuestiontypeDao testpaperQuestiontypeDao;
   @Autowired
   private TestpaperQuestionDao testpaperQuestionDao;
   @Autowired
   private VAdminOrgDao vAdminOrgDao;

   /** 保存招生机构
    * 
    * @param objectNode
    * @pdOid f1e01830-9157-4f92-b5e3-d3798aafa513 */
   @Transactional
   public boolean save(ObjectNode objectNode) {
      if(!adminDao.exists(SearchFilter.eq("loginName", objectNode.findPath("adminAccount").asText()))){
         return false;
      }
      AdmissionsOrg admissionsOrg = new AdmissionsOrg();
      admissionsOrg.setOrgName(objectNode.findPath("orgName").asText());
      admissionsOrg.setAddress(objectNode.findPath("address").asText());
      admissionsOrg.setProvince(objectNode.findPath("province").asText());
      admissionsOrg.setProperty(objectNode.findPath("property").asText());
      admissionsOrg.setCity(objectNode.findPath("city").asText());
      admissionsOrg.setTown(objectNode.findPath("town").asText());
      admissionsOrg.setDescription(objectNode.findPath("description").asText());
      admissionsOrg.setDateCreated(new Date());
      admissionsOrg.setUseStatus("1");
      admissionsOrg = admissionsOrgDao.save(admissionsOrg);
      Admin admin = new Admin();
      admin.setLoginName(objectNode.findPath("adminAccount").asText());
      admin.setDateCreate(new Date());
      admin.setEmail(objectNode.findPath("email").asText());
      admin.setName(objectNode.findPath("adminName").asText());
      admin.setOrgCode(admissionsOrg.getId());
      admin.setPassword(objectNode.findPath("adminPwd").asText());
      admin.setPhone(objectNode.findPath("phone").asText());
      admin.setDuty(objectNode.findPath("duties").asText());
      admin.setUseStatus("1");
      adminDao.save(admin);
      return true;
   }


   /** 删除招生机构
    *
    * @param orgCode
    * @pdOid f1e01830-9157-4f92-b5e3-d3798aafa513 */
   @Transactional
   public void delete(Integer orgCode) {
      AdmissionsOrg admissionsOrg = admissionsOrgDao.findOne(orgCode);
      Admin admin = adminDao.findByOrgCode(orgCode);
      List<User> users = userDao.findUserByOrgCode(orgCode);
      admissionsOrg.setUseStatus("0");
      admin.setUseStatus("0");
      admissionsOrgDao.save(admissionsOrg);
      adminDao.save(admin);
      for (int i = 0; i < users.size(); i++) {
         User user = users.get(i);
         user.setUseStatus("0");
      }
      userDao.save(users);
   }
   /** 修改招生机构
    * 
    * @param objectNode
    * @pdOid f5e7e377-5590-4792-9ade-48b6667c5b00 */
   @Transactional
   public void update(ObjectNode objectNode) {
      AdmissionsOrg admissionsOrg = admissionsOrgDao.findOne(objectNode.findPath("orgCode").asInt());
      admissionsOrg.setOrgName(objectNode.findPath("orgName").asText());
      admissionsOrg.setProperty(objectNode.findPath("property").asText());
      admissionsOrg.setProvince(objectNode.findPath("province").asText());
      admissionsOrg.setCity(objectNode.findPath("city").asText());
      admissionsOrg.setTown(objectNode.findPath("town").asText());
      admissionsOrg.setAddress(objectNode.findPath("address").asText());
      admissionsOrg.setDescription(objectNode.findPath("description").asText());
      admissionsOrgDao.save(admissionsOrg);
   }

   /**
    * 查询所有的招生机构并返回
    * @return
    */
   public List<AdmissionsOrg> findAll() {
      return admissionsOrgDao.findAll(SearchFilter.eq("useStatus", 1));
   }

   /** 查询所有的招生机构并返回
    * @param page 页数
    * @pdOid 6db3689c-bfa0-4b0c-a1e1-4cd75460b1e1 */
   public Page<AdmissionsOrgVo> findAll(Integer page) {
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      return admissionsOrgDao.findAllAdmissionsOrg(pageable);
   }

   /** 根据招生机构ID查找招生机构
    *
    * @param id
    * @pdOid 00a1548a-c962-4e91-9a89-f46bd983c3cb */
   public AdmissionsOrgVo findVoById(Integer id) {
      return admissionsOrgDao.findVoById(id);
   }

   /** 根据招生机构ID查找招生机构
    *
    * @param id
    * @pdOid 00a1548a-c962-4e91-9a89-f46bd983c3cb */
   public AdmissionsOrg findById(Integer id) {
      return admissionsOrgDao.findOne(id);
   }

   /** 根据招生机构ID查找招生机构
    * @param orgCode 招生机构
    * @param name 姓名
    * @param account 帐号
    * @param email 邮箱
    * @param page 页数
    * @pdOid 00a1548a-c962-4e91-9a89-f46bd983c3cb */
   public Page searchUser(Integer orgCode,String name,String account,String email,Integer page) {
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      StringBuilder sql = new StringBuilder("SELECT  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName) FROM User us, AdmissionsOrg ao,AdmissionInfo ai  WHERE us.id=ai.uid AND us.useStatus='1'AND ai.orgCode=ao.id AND us.useStatus=ai.useStatus");
      if(orgCode!=0){
         sql.append(" AND ao.id=" + orgCode);
      }
      if(!name.equals("0")){
         sql.append(" AND us.name='" + name+"'");
      }
      if(!account.equals("0")){
         sql.append(" AND us.account='" + account+"'");
      }
      if(!email.equals("0")){
         sql.append(" AND us.email='" + email+"'");
      }
      return userDao.findAll(UserVo.class, pageable, sql.toString());
   }

   /**
    * 验证招生机构名称是否存在
    * @param orgName
    * @return
    */
   public String validateOrgName(String orgName){
      if(admissionsOrgDao.exists(SearchFilter.eq("orgName", orgName))){
         return "此机构已经注册";
      }
      return "";
   }

   /**
    * 招生机构注册
    * @return
    */
   @Transactional
   public void regist(OrgRegistDto orgRegistDto){

      AdmissionsOrg admissionsOrg = orgRegistDto.getAdmissionsOrg();
      admissionsOrg.setDateCreated(new Date());
      admissionsOrg.setUseStatus("2");
      admissionsOrg = admissionsOrgDao.save(admissionsOrg);
      Admin admin = orgRegistDto.getAdmin();
      admin.setDateCreate(new Date());
      admin.setOrgCode(admissionsOrg.getId());
      admin.setOrgName(admissionsOrg.getOrgName());
      admin.setUseStatus("2");
      adminDao.save(admin);
      Orders order = orgRegistDto.getOrder();
      order.setOrgCode(admissionsOrg.getId());
      order.setDateCreated(new Date());
      order.setStatus("2");
      if(order.getTpid() == 0){
         Testpaper testpaperReference = testpaperDao.findOne(SearchFilter.eq("useStatus", "1"), SearchFilter.eq("isReference", "1"), SearchFilter.eq("orgCode", "0"));
         List<Integer> testpaperQuestionTypeList = orgRegistDto.getQuestiontypeList();
         List<TestpaperQuestiontype> testpaperQuestiontypes = testpaperQuestiontypeDao.findAll(SearchFilter.eq("tpid", testpaperReference.getId()), SearchFilter.in("qtid", testpaperQuestionTypeList));
         StringBuilder testpaperQuestionStrBuilder =  new StringBuilder("SELECT a FROM TestpaperQuestion a,Question b WHERE a.tpid="+ testpaperReference.getId() + " AND a.qid=b.id AND (");
         for (int i = 0; i < testpaperQuestiontypes.size(); i++) {
            TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypes.get(i);
            if(i!=testpaperQuestiontypes.size()-1)
               testpaperQuestionStrBuilder.append("b.QType=" + testpaperQuestiontype.getQtid() + " OR ");
            else
               testpaperQuestionStrBuilder.append("b.QType=" + testpaperQuestiontype.getQtid()+ ")");
         }
         List<TestpaperQuestion> testpaperQuestions = testpaperQuestionDao.findAll(testpaperQuestionStrBuilder.toString());
         Testpaper testpaper = new Testpaper();
         testpaper.setDateCreated(new Date());
         testpaper.setExpectTime(testpaperReference.getExpectTime());
         testpaper.setName(orgRegistDto.getTestpaperName());
         testpaper.setUseStatus("1");
         testpaper.setOrgCode(admissionsOrg.getId());
         testpaper.setIsReference("0");
         testpaper = testpaperDao.save(testpaper);
         List<TestpaperQuestiontype> newTestpaperQuestiontypes = new ArrayList<>();
         List<TestpaperQuestion> newTestpaperQuestions = new ArrayList<>();
         for (int i = 0; i < testpaperQuestiontypes.size(); i++) {
            TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypes.get(i);
            TestpaperQuestiontype newTestpaperQuestiontype = new TestpaperQuestiontype();
            newTestpaperQuestiontype.setQtid(testpaperQuestiontype.getQtid());
            newTestpaperQuestiontype.setTpid(testpaper.getId());
            newTestpaperQuestiontype.setSeq(i+1);
            newTestpaperQuestiontypes.add(newTestpaperQuestiontype);
         }
         for (int i = 0; i < testpaperQuestions.size(); i++) {
            TestpaperQuestion testpaperQuestion = testpaperQuestions.get(i);
            TestpaperQuestion newTestpaperQuestion = new TestpaperQuestion();
            newTestpaperQuestion.setQid(testpaperQuestion.getQid());
            newTestpaperQuestion.setTpid(testpaper.getId());
            newTestpaperQuestions.add(newTestpaperQuestion);
         }
         testpaperQuestionDao.save(newTestpaperQuestions);
         testpaperQuestiontypeDao.save(newTestpaperQuestiontypes);
         order.setTpid(testpaper.getId());
         order.setTestpaperName(orgRegistDto.getTestpaperName());
      } else {
         Testpaper testpaper = testpaperDao.findOne(order.getTpid());
         order.setTestpaperName(testpaper.getName());
      }
      ordersDao.save(order);
   }

   /**
    * 分页查找招生机构负责人申请列表(审核学校)
    * @param request
    * @param cols 分页列表的列名对应的字段
    * @return
    */
   public Page<VAdminOrg> findAllAudit(Http.Request request, String[] cols) {
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<VAdminOrg> spec = DynamicSpecifications.fromRequest(request, VAdminOrg.class);
      return vAdminOrgDao.findAll(spec, pageable);
   }

   /**
    * 审批招生机构页面
    * @return
    */
   @Transactional
   public AdmissionsOrg updateUseStatus(Integer orgCode, String useStatus) {
      AdmissionsOrg admissionsOrg = admissionsOrgDao.findOne(orgCode);
      admissionsOrg.setUseStatus(useStatus);
      return admissionsOrgDao.save(admissionsOrg);
   }

   /**
    * 发送邮件通知审核通过
    * @param admin
    * @param admissionsOrg
    */
   public void approvedEmail(Admin admin, AdmissionsOrg admissionsOrg){
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("尊敬的");
      stringBuilder.append(admin.getName()+"(先生/女士)：<br />");
      stringBuilder.append("欢迎您通过中国心理与认知能力测评管理平台网站进行招生机构信息注册，您的基本信息如下：<br /><br /><br />");
      stringBuilder.append("用户名："+admin.getLoginName()+"<br />");
      stringBuilder.append("密码："+admin.getPassword()+"<br />");
      stringBuilder.append("机构名称："+admissionsOrg.getOrgName()+"<br />");
      stringBuilder.append("负责人姓名："+admin.getName()+"<br />");
      stringBuilder.append("负责人电话："+admin.getPhone()+"<br /><br /><br />");
      stringBuilder.append("请您登录http://dev.fcbrains.com:8787/admin/login网址确认，谢谢！<br />");
      stringBuilder.append("注意，如果以上链接无法点击，请将上面的地址复制到浏览器(如火狐浏览器)的地址栏进入。<br />如果验证不成功，您可以致信wangbin@fcbrains.com，标题为“招生机构注册验证”，内容为空即可，我们会协助验证。<br />");
      stringBuilder.append("<br /><br /><br />中国心理与认知能力测评管理平台欢迎您！<br />");
      stringBuilder.append("（本邮件由机器自动发送，请不要回复。）<br /><br />");
      stringBuilder.append("---------------------------<br /><br /><br /><br />");
      stringBuilder.append("中国心理与认知能力测评管理平台<br />");
      stringBuilder.append("Chinese psychological and cognitive ability test management platform<br />");

      MailUtils.send(SystemConstant.EMAIL_SMTP_ADDRESS, SystemConstant.EMAIL_SEND_FROM_USER, admin.getEmail(), "审核通过", stringBuilder.toString(), SystemConstant.EMAIL_LOGIN_ACCOUNT, SystemConstant.EMAIL_LOGIN_PWD);
   }

   /**
    * 发送邮件通知审核被拒绝
    * @param admin
    */
   public void refusedEmail(Admin admin){
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("尊敬的");
      stringBuilder.append(admin.getName()+"(先生/女士)：<br /><br />");
      stringBuilder.append("非常抱歉，您的申请因缺少资料没有通过，请发邮件至“ceping@fcbrains.com”与工作人员联系。<br /><br />");
      stringBuilder.append("---------------------------<br />");
      stringBuilder.append("中国心理与认知能力测评管理平台<br />");
      stringBuilder.append("Chinese psychological and cognitive ability test management platform<br />");

      MailUtils.send(SystemConstant.EMAIL_SMTP_ADDRESS, SystemConstant.EMAIL_SEND_FROM_USER, admin.getEmail(), "审核未通过", stringBuilder.toString(), SystemConstant.EMAIL_LOGIN_ACCOUNT, SystemConstant.EMAIL_LOGIN_PWD);
   }

   /**
    * 分页查找招生机构
    * @param request
    * @param cols 分页列表的列名对应的字段
    * @return
    */
   public Page<VAdminOrg> findAll(Http.Request request, String[] cols) {
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<VAdminOrg> spec = DynamicSpecifications.fromRequest(request, VAdminOrg.class);
      return vAdminOrgDao.findAll(spec, pageable);
   }
}