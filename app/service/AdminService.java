/***********************************************************************
 * Module:  AdminService.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdminService
 ***********************************************************************/

package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.AdminDao;
import dao.RoleDao;
import models.Admin;
import models.AdmissionsOrg;
import models.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.MailUtils;
import utils.PageUtils;
import utils.SystemConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 管理员相关业务
 */
@Service
public class AdminService {

   @Autowired
   private AdminDao adminDao;
   @Autowired
   private RoleDao roleDao;

   /**
    * 分页查找管理员
    * @param request
    * @param cols 分页列表的列名对应的字段
    * @return
    */
   public Page<Admin> findAll(Http.Request request, String[] cols) {
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<Admin> spec = DynamicSpecifications.fromRequest(request, Admin.class);
      return adminDao.findAll(spec, pageable);
   }

   /** 保存管理员信息(登录名不能重复)
    * 
    * @param admin
    * @pdOid 66f890ab-5892-4702-bf8e-e3c32b17c726 */
   public Admin save(Admin admin) {
      // TODO: implement
      return null;
   }

   /**
    * 更新管理员信息
    * @param objectNode
    * @return
    */
   @Transactional
   public Admin update(ObjectNode objectNode) {
      Admin admin = adminDao.findByOrgCode(objectNode.findPath("orgCode").asInt());
      admin.setName(objectNode.findPath("adminName").asText());
      admin.setPhone(objectNode.findPath("phone").asText());
      admin.setEmail(objectNode.findPath("email").asText());
      admin.setDuty(objectNode.findPath("duties").asText());

      Integer roleId = objectNode.findPath("roleId").asInt();
      Role role = roleDao.findOne(roleId);
      List roles = new ArrayList<>();
      roles.add(role);
      admin.setRoles(roles);

      adminDao.save(admin);
      return admin;
   }

   /**
    * 查找所有管理员信息
    * @param page
    * @return
    */
   public Page<Admin> findAll(int page, int pageSize) {
      Pageable pageable = new PageRequest(page - 1, pageSize);
      return adminDao.findAll(pageable, SearchFilter.eq("useStatus", 1));
   }

   /** 根据所传id查找管理员
    * 
    * @param id
    * @pdOid aa0a5ce6-923a-4dae-b0a8-8c7c79f8b19d */
   public Admin findById(Integer id) {
      return adminDao.findOne(id);
   }

   /** 根据所传id查找管理员
    *
    * @param orgCode
    * @pdOid aa0a5ce6-923a-4dae-b0a8-8c7c79f8b19d */
   public Admin getByOrgCode(Integer  orgCode) {

      return adminDao.findByOrgCode(orgCode);
   }


   /** 根据登录名和密码查找管理员
    * 
    * @param loginName 登录名
    * @param password 登录密码
    * @pdOid 4470075a-5350-437b-91e1-8b43b3c27489 */
   public Admin findByLoginNameAndPassword(String loginName, String password) {
      // TODO: implement
      return null;
   }


   /**
    * 验证登录用户名是否存在
    * @param loginName
    * @return
    */
   public String validateLoginName(String loginName){
      Admin admin = adminDao.findOne(SearchFilter.eq("loginName", loginName));
      if(admin != null){
         String useStatus = admin.getUseStatus();
         if(SystemConstant.USE_STATUS_NO.equals(useStatus)){
            return "由于申请没有成功，此用户名可以被继续使用。";
         } else if(SystemConstant.USE_STATUS_YES.equals(useStatus)){
            return "用户名已经存在";
         } else {
            return "用户名已经存在";
         }
      }
      return "";
   }

   /**
    * 更新使用状态
    * @param adminId
    * @param useStatus
    */
   @Transactional
   public Admin updateUseStatus(Integer adminId,String useStatus){
      Admin admin = adminDao.findOne(adminId);
      admin.setUseStatus(useStatus);
      return adminDao.save(admin);
   }
}