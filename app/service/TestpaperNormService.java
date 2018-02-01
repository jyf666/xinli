/***********************************************************************
 * Module:  TestpaperNormService.java
 * Author:  XIAODA
 * Purpose: Defines the Class TestpaperNormService
 ***********************************************************************/

package service;

import dao.TestpaperNormDao;
import models.Admin;
import models.Score;
import models.TestpaperNorm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import persistence.DynamicSpecifications;
import play.mvc.Http;
import utils.PageUtils;

import java.util.*;

/** 试卷常模相关业务
 * 
 * @pdOid 8cfd93bc-1dbf-462a-b49a-8aafbc4720d1 */
@Service
public class TestpaperNormService {

   @Autowired
   private TestpaperNormDao testpaperNormDao;

   /** 保存试卷常模
    * 
    * @param testpaperNorm
    * @pdOid c1efe58e-5eb6-4701-b377-3235254f7b1c */
   public TestpaperNorm save(TestpaperNorm testpaperNorm) {
      // TODO: implement
      return null;
   }
   
   /** 更新试卷常模
    * 
    * @param testpaperNorm
    * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
   public TestpaperNorm update(TestpaperNorm testpaperNorm) {
      // TODO: implement
      return null;
   }
   
   /** 查找所有的试卷常模
    * 
    * @pdOid 52d557af-32fa-4bf5-8ac4-473c2b72cb82 */
   public List<TestpaperNorm> findAll() {
      return testpaperNormDao.findAll();
   }

   /**
    * 分页查找管理员
    * @param request
    * @param cols 分页列表的列名对应的字段
    * @return
    */
   public Page<TestpaperNorm> findAll(Http.Request request, String[] cols) {
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<TestpaperNorm> spec = DynamicSpecifications.fromRequest(request, TestpaperNorm.class);
      return testpaperNormDao.findAll(spec, pageable);
   }

   public TestpaperNorm findById(Integer tpnid){
      return testpaperNormDao.findOne(tpnid);
   }
   
   /** 根据招生机构编码查询试卷常模
    * 
    * @param orgCode
    * @pdOid 3168173d-ab76-4e4d-b567-93cf76760b32 */
   public List<TestpaperNorm> findByOrgCode(String orgCode) {
      // TODO: implement
      return null;
   }

}