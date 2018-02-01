/***********************************************************************
 * Module:  AdmissionsOrgController.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdmissionsOrgController
 ***********************************************************************/

package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.dto.OrgRegistDto;
import models.dto.ResponseDto;
import models.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.FileUtils;
import utils.PageUtils;
import views.html.admin.orgManage.audit.list;
import views.html.admin.orgManage.orgList;
import views.html.admin.orgManage.orgListUser;
import views.html.admin.orgManage.orgAdd;
import views.html.admin.orgManage.orgEdit;
import views.html.admin.orgManage.orgAddTest;
import views.html.admin.orgManage.orgEditTest;
import views.html.admin.orgManage.orgTestIntro;
import views.html.admin.orgManage.orgReg;
import views.html.admin.orgManage.orgReport;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * 招生机构控制
 * @pdOid 34fd036e-eb35-430b-a777-e56f4d8763c2
 **/
@org.springframework.stereotype.Controller
public class AdmissionsOrgController extends Controller {

   @Autowired
   private AdmissionsOrgService admissionsOrgService;
   @Autowired
   private AdminService adminService;
   @Autowired
   private UserService userService;
   @Autowired
   private TestService testService;
   @Autowired
   private TestPaperService testPaperService;
   @Autowired
   private QuestionTypeService questionTypeService;
   @Autowired
   private PersonalReportService personalReportService;
   @Autowired
   private AdmissionsOrgReportService admissionsOrgReportService;

   /**
    * 招生机构列表页面
    * @return
    */
   public Result listView() {
      return ok(orgList.apply());
   }

   /**
    * 获取招生机构分页数据
    * @return
    */
   public Result page() {
      String draw = request().getQueryString("draw"); //获取请求次数
      String[] cols = {"id", "id", "orgName", "name", "address", "description"};// 定义列名
      Page<VAdminOrg> page = admissionsOrgService.findAll(request(), cols);
      return ok(PageUtils.convertToTableData(page, draw));
   }

   /**
    * 获取招生机构列表
    * @return
    */
   public Result list(){
      List<AdmissionsOrg> admissionsOrgs = admissionsOrgService.findAll();
      return ok(Json.toJson(admissionsOrgs));
   }

   /** 添加招生机构页面
    * 
    * @pdOid ce5364a5-5e43-480d-83c0-895015d3ee11 */
   public Result addView() {
      return ok(orgAdd.render());
   }

   /** 添加招生机构
    *
    * @pdOid ce5364a5-5e43-480d-83c0-895015d3ee11 */
   public Result addOrg() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      if(admissionsOrgService.save(objectNode)){
         return ok("true");
      }else {
         return ok("false");
      }
   }


   public Result orgReportPDF(Integer orgCode){
      Map<String,Object> pdfData = null;
      try {
         pdfData = admissionsOrgReportService.getOrgPdfData(orgCode);
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
      return ok(orgReport.render(pdfData));
   }

   /** 考试介绍
    *
    * @pdOid ce5364a5-5e43-480d-83c0-895015d3ee11 */
   @Pattern("/org/testIntro")
   public Result testIntro(){
      return ok(orgTestIntro.render());
   }

   /** 用户导出
    *
    * @pdOid d374a5c3-008a-4686-a1e1-4803bb497e74 */
   public Result outputUserPDF(Integer orgCode) {
      personalReportService.zipPdf(orgCode);
      try {
         response().setContentType("application/x-download");
         response().setHeader("Content-disposition","attachment; filename="+new String(new String("用户PDF.rar").getBytes("UTF-8"),"ISO8859-1"));
         return ok(new File("F://demo.rar"));
      }catch (Exception e){
         e.printStackTrace();
      }
      return null;
   }

   /**
    * 修改招生机构页面
    * @param orgCode
    * @return
    */
//   @Pattern("/admin/org/editView")
   public Result editView(Integer orgCode) {
      AdmissionsOrg admissionsOrg = admissionsOrgService.findById(orgCode);
      return ok(orgEdit.render(admissionsOrg));
   }

   /**
    * 修改招生机构
    * @return
    */
//   @Pattern("/org/edit")
   public Result edit() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      admissionsOrgService.update(objectNode);
      return ok();
   }

   /**
    * 删除招生机构
    * @param orgCode
    * @return
    */
//   @Pattern("/org/delete")
   public Result deleteOrg(Integer orgCode) {
      admissionsOrgService.delete(orgCode);
      return ok();
   }
   
   /** 招生机构学生列表
    * @param orgCode 招生机构
    * @param page 页数
    * @pdOid 62ffcf7a-4a66-4ec7-8a66-7f26a2b8cc65 */
   @Pattern("/org/orgListUser")
   public Result orgListUser(Integer orgCode,Integer page) {
      Page<UserVo> pageContent = userService.getOrgUserList(orgCode,page);
      String orgName = admissionsOrgService.findById(orgCode).getOrgName();

      SearchVo searchVo = new SearchVo(orgCode,"","","");

      List<UserVo> userInfos = pageContent.getContent();
      Long totalNum = pageContent.getTotalElements();
      Integer totalPage = pageContent.getTotalPages();
      Integer currentPage = page;
      return ok(orgListUser.render(userInfos,totalNum,orgName,totalPage,currentPage,searchVo));
   }

   /** 招生机构检索学生
    * @param orgCode 招生机构
    * @param account 帐号
    * @param email 邮箱
    * @param name 姓名
    * @param page 页数
    * @pdOid 62ffcf7a-4a66-4ec7-8a66-7f26a2b8cc65 */
   @Pattern("/org/searchUser")
   public Result searchUser(Integer orgCode,String name,String account,String email,Integer page) {

      SearchVo searchVo = new SearchVo(orgCode,name,account,email);
      Page<UserVo> pageContent = admissionsOrgService.searchUser(orgCode, name, account, email, page);
      String orgName = admissionsOrgService.findById(orgCode).getOrgName();

      List<UserVo> userInfos = pageContent.getContent();
      Long totalNum = pageContent.getTotalElements();
      Integer totalPage = pageContent.getTotalPages();
      Integer currentPage = page;

      return ok(orgListUser.render(userInfos,totalNum,orgName,totalPage,currentPage,searchVo));
   }

   /**
    * 招生机构添加考试页面
    * @param orgCode
    * @return
    */
//   @Pattern("/org/addTestView")
   public Result orgAddTestView(Integer orgCode) {
      List<Testpaper> testpapers = testPaperService.findAll();
      return ok(orgAddTest.render(orgCode,testpapers));
   }

   /** 招生机构修改考试页面
    * @param tid 试卷id
    * @pdOid 62ffcf7a-4a66-4ec7-8a66-7f26a2b8cc65 */
   public Result orgEditTestView(Integer tid) {
      Test test = testService.findById(tid);
      List<Testpaper> testpapers = testPaperService.findAll();

//      JsonNode normDatajsonNode = Json.parse(test.getNormData());
//      Iterator iterator = normDatajsonNode.iterator();
//      List<NormVo> normVos = new ArrayList<NormVo>();
//      while(iterator.hasNext()){
//         ObjectNode normJson = (ObjectNode)iterator.next();
//         NormVo normVo = new NormVo();
//         normVo.setQtypeName(normJson.findPath("qType").asText());
//         normVo.setAverage(normJson.findPath("average").asText());
//         normVo.setStandard(normJson.findPath("standard").asText());
//         normVos.add(normVo);
//      }
      return ok(orgEditTest.render(test,testpapers));
   }

   public Result registView(){
      List<Questiontype> questiontypes = questionTypeService.getByIsReference();
      List<Testpaper> testpapers = testPaperService.findAllByOrgCode(0);
      return ok(orgReg.render(questiontypes, testpapers));
   }

   /**
    * 招生机构负责人申请列表页面
    * @return
    */
   public Result auditListView() {
      return ok(list.apply());
   }

   /**
    * 获取招生机构负责人申请列表分页数据
    * @return
    */
   public Result auditPage() {
      String draw = request().getQueryString("draw"); //获取请求次数
      String[] cols = {"id", "id", "orgName", "property", "name", "duty", "email", "phone"};// 定义列名
      Page<VAdminOrg> page = admissionsOrgService.findAllAudit(request(), cols);
      return ok(PageUtils.convertToTableData(page, draw));
   }

   /**
    * 招生机构注册
    * @return
    */
   public Result regist(){
      play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
      OrgRegistDto orgRegistDto = new OrgRegistDto();

      if(body == null){
         Form<OrgRegistDto> orgRegistDtoForm = Form.form(OrgRegistDto.class);
         orgRegistDto = orgRegistDtoForm.bindFromRequest().get();
      } else {
         play.mvc.Http.MultipartFormData.FilePart picture = body.getFile("agreement_img");

         Form<AdmissionsOrg> admissionsOrgForm = Form.form(AdmissionsOrg.class);
         AdmissionsOrg admissionsOrg = admissionsOrgForm.bindFromRequest().get();
         Form<Admin> adminForm = Form.form(Admin.class);
         Admin admin = adminForm.bindFromRequest().get();
         Form<Orders> orderForm = Form.form(Orders.class);
         Orders order = orderForm.bindFromRequest().get();

         Map<String, String[]> param = body.asFormUrlEncoded();
         String testpaperName = param.get("testpaperName")[0];
         String questiontypeStr = param.get("questiontypeStr")[0];
         List<Integer> questiontypeList = null;
         if(StringUtils.isNotBlank(questiontypeStr)){
            String[] questiontypeArr = questiontypeStr.substring(0, questiontypeStr.length() - 1).split(",");
            for (int i = 0; i < questiontypeArr.length; i++) {
               questiontypeList.add(Integer.valueOf(questiontypeArr[i]));
            }
         }

         String agreement = "";
         if(picture != null){
            try {
               agreement = FileUtils.encodeBase64File(picture.getFile());
               order.setAgreement(agreement);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         orgRegistDto = new OrgRegistDto(admissionsOrg, admin, order, testpaperName, questiontypeList);
      }

      ResponseDto responseDto = new ResponseDto();
      if(!testPaperService.existTestpaper(orgRegistDto.getQuestiontypeList())){
         try {
            admissionsOrgService.regist(orgRegistDto);
            responseDto.setSuccess(true);
         } catch (DataIntegrityViolationException e){
            responseDto.setMessage("上传的文件异常，请注意图片不能超过65k");
            responseDto.setSuccess(false);
            e.printStackTrace();
         } catch (Exception e) {
            responseDto.setMessage("保存失败");
            responseDto.setSuccess(false);
            e.printStackTrace();
         }
      } else {
         responseDto.setMessage("已有试卷与此试卷所含内容一致，请使用已有试卷");
         responseDto.setSuccess(false);
      }
      return ok(Json.toJson(responseDto));
   }

   /**
    * 验证机构名称是否存在
    * @return
    */
   public Result validateOrgName(String orgName){
       return ok(admissionsOrgService.validateOrgName(orgName));
   }

   /**
    * 接受招生机构
    * @return
    */
   @Pattern("/admin/org/approved")
   @Transactional
   public Result approved(Integer orgCode,Integer adminId){

      AdmissionsOrg admissionsOrg = admissionsOrgService.updateUseStatus(orgCode,"1");
      Admin admin = adminService.updateUseStatus(adminId,"1");
      admissionsOrgService.approvedEmail(admin, admissionsOrg);
      return ok();
   }

   /**
    * 拒绝招生机构
    * @return
    */
   @Pattern("/admin/org/refused")
   public Result refused(Integer orgCode,Integer adminId){
      admissionsOrgService.updateUseStatus(orgCode,"3");
      Admin admin = adminService.updateUseStatus(adminId,"3");
      admissionsOrgService.refusedEmail(admin);
      return ok();
   }

}