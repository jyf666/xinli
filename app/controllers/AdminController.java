/***********************************************************************
 * Module:  AdminController.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdminController
 ***********************************************************************/

package  controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.mvc.Controller;
import play.mvc.Result;
import service.AdminService;
import service.AdmissionsOrgService;
import utils.PageUtils;
import views.html.admin.systemManage.userManage.adminEdit;
import views.html.admin.systemManage.userManage.adminList;

import java.util.List;
import java.util.Map;

/** 管理员控制
 * 
 * @pdOid 7c2a516c-06fc-4a77-823e-6228540c3600 */
@org.springframework.stereotype.Controller
public class AdminController extends Controller {

   @Autowired
   private AdminService adminService;
   @Autowired
   private AdmissionsOrgService admissionsOrgService;

   /** 添加管理员页面
    * 
    * @pdOid e4be0b7a-8e78-4454-89c3-19e2a4e96571 */
   public Result addView() {
      // TODO: implement
      return null;
   }
   
   /**
    * 修改管理员页面
    * @param id
    * @return
    */
   public Result editView(Integer id) {
      Admin admin = adminService.findById(id);
      return ok(adminEdit.render(admin));
   }

   /**
    * 用户管理页面
    * @return
    */
   public Result listView() {
      return ok(adminList.apply());
   }

   /**
    * 获取管理员分页数据
    * @return
    */
   public Result page() {
      String draw = request().getQueryString("draw"); //获取请求次数
      String[] cols = {"id", "id", "name", "orgCode", "loginName", "password", "email"};// 定义列名

      Page<Admin> page = adminService.findAll(request(), cols);
      return ok(PageUtils.convertToTableData(page, draw));
   }
   /** 添加管理员
    * 
    * @pdOid 46e80db9-c6f5-4400-bec3-7f801b466820 */
   public Result addUser() {
      // TODO: implement
      return null;
   }

   /**
    * 修改管理员
    * @return
    */
   public Result edit() {
      ObjectNode objectNode = (ObjectNode)request().body().asJson();
      adminService.update(objectNode);
      return ok();
   }

   /**
    * 验证登录用户名是否存在
    * @param loginName
    * @return
    */
   public Result validateLoginName(String loginName){
      return ok(adminService.validateLoginName(loginName));
   }

}