/***********************************************************************
 * Module:  TestPaperService.java
 * Author:  XIAODA
 * Purpose: Defines the Class TestPaperService
 ***********************************************************************/

package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.vo.QuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.PageUtils;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/** 试卷相关业务
 *
 * @pdOid 1520dcb6-4baa-4ba1-9969-33b99c274c64 */
@Service
public class TestPaperService {

   @Autowired
   private TestpaperDao testpaperDao;
   @Autowired
   private QuestionDao questionDao;
   @Autowired
   private QuestionTypeDao questionTypeDao;
   @Autowired
   private TestpaperQuestiontypeDao testpaperQuestiontypeDao;
   @Autowired
   private TestpaperQuestionDao testpaperQuestionDao;
   @Autowired
   private VTestPaperDao vTestPaperDao;
   /**
    * 分页查找题目类型
    * @param request
    * @param cols
    * @return
    */
   public Page<VTestPaper> findAll(Http.Request request, String[] cols){
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<VTestPaper> spec = DynamicSpecifications.fromRequest(request, VTestPaper.class);
      return vTestPaperDao.findAll(spec,pageable);
   }
   /**
    * 添加试卷
    **/
   public Integer save(ObjectNode objectNode) {
      try {
         Testpaper testpaper = new Testpaper();
         if(testpaperDao.findOne(objectNode.findPath("tpid").asInt())!=null){
               testpaper = testpaperDao.findOne(objectNode.findPath("tpid").asInt());
         }
         testpaper.setName(objectNode.findPath("testpaperName").asText());
         testpaper.setOrgCode(objectNode.findPath("orgCode").asInt());
         testpaper.setExpectTime(objectNode.findPath("exceptTime").asInt());
         testpaper.setIsReference(objectNode.findPath("isReference").asText());
         testpaper.setUseStatus(objectNode.findPath("useStatus").asText());
         testpaper.setDateCreated(new Date());
         return testpaperDao.save(testpaper).getId();
      }catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * 导出试卷
    * @param tpid
    */
   public List<Question> export(Integer tpid){
      List<Question> questions = questionDao.findQuestionByTpid(tpid);
      return questions;

   }
   
   /** 修改试卷
    * 
    * @param objectNode
    * @pdOid 7ae91de1-6168-49fe-aa89-956e7fdb6eb5 */
   @Transactional
   public void update(ObjectNode objectNode) {
      
      Testpaper testpaper = new Testpaper();
      if(testpaperDao.findOne(objectNode.findPath("tpid").asInt())!=null){
         testpaper = testpaperDao.findOne(objectNode.findPath("tpid").asInt());
      }else{
         testpaper.setDateCreated(new Date());
         testpaper.setIsReference("0");
      }
      testpaper.setName(objectNode.findPath("name").asText());
      testpaper.setOrgCode(objectNode.findPath("orgCode").asInt());
      testpaper.setExpectTime(objectNode.findPath("exceptTime").asInt());
      testpaper.setUseStatus("1");
      testpaperDao.save(testpaper);
      if(objectNode.findPath("isReference").asText().equals("1")) {
         int tpid = testpaper.getId();
         this.setToReference(tpid);
      }

   }

   /** 删除试卷
    *
    * @param tpid 试卷id
    * @pdOid 7ae91de1-6168-49fe-aa89-956e7fdb6eb5 */
   @Transactional
   public void delete(Integer tpid) {
      Testpaper testpaper = testpaperDao.findOne(tpid);
      testpaper.setUseStatus("0");
      testpaperDao.save(testpaper);
   }

   /** 删除试卷的试题
    *
    * @param tpid 试卷id
    * @param qid 试题id
    * @pdOid 7ae91de1-6168-49fe-aa89-956e7fdb6eb5 */
   @Transactional
   public void deleteQuestion(Integer tpid,String qid) {
      TestpaperQuestion testpaperQuestion = testpaperQuestionDao.findByTpidAndQid(tpid, qid);
      testpaperQuestionDao.delete(testpaperQuestion);
   }

   /** 删除试卷的试题
    *
    * @param tpid 试卷id
    * @param qType 题型id
    * @pdOid 7ae91de1-6168-49fe-aa89-956e7fdb6eb5 */
   @Transactional
   public void deleteQuestiontype(Integer tpid,Integer qType) {
      TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypeDao.findByTpidAndQtid(tpid, qType);
      testpaperQuestiontypeDao.delete(testpaperQuestiontype);
   }

   /** 查询所有的试卷
    *
    * @pdOid 2098f385-b321-4510-8b2c-9cf4e945b532 */
   public List<Testpaper> findAll() {
      return testpaperDao.findAll(SearchFilter.eq("useStatus", "1"));
   }

   /**
    * 根据招生机构查找试卷
    **/
   public List<Testpaper> findAllByOrgCode(Integer orgCode) {
      return testpaperDao.findAllByOrgCodeOrReference(orgCode);
   }

   public List<Testpaper> findAllByOrgCodes(List<Integer> orgCodes) {
      return testpaperDao.findAllByOrgCodesOrReference(orgCodes);
   }

   /** 查找包含题型字符串的试卷
    *
    * @pdOid 2098f385-b321-4510-8b2c-9cf4e945b532 */
   public List<Testpaper> containsQtypeTestpaper(String qTtypeStr) {
      String[] qTypes = qTtypeStr.split(",");
      List<Testpaper> testpapers = new ArrayList<>();
      StringBuilder sb = new StringBuilder();
      sb.append("SELECT t FROM Testpaper t,TestpaperQuestiontype tq WHERE t.id=tq.tpid AND (");
      for (int i = 0; i < qTypes.length; i++) {
         sb.append("tq.qtid="+ Integer.parseInt(qTypes[i])+" OR ");
      }
      String sql = sb.toString().substring(0,sb.toString().length()-3) +") GROUP BY tq.tpid HAVING count(*)="+qTypes.length;
      testpapers = testpaperDao.findAll(sql);
      return testpapers;
   }

   /** 查询所有的试卷
    * @param page 页数
    * @pdOid 2098f385-b321-4510-8b2c-9cf4e945b532 */
   public Page<Testpaper> findAll(Integer page) {
      Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
      return testpaperDao.findAllTestpaper(pageable);
   }

   /** 修改试卷为模版试卷
    * @pdOid 2098f385-b321-4510-8b2c-9cf4e945b532 */
   @Transactional
   public void setToReference(Integer tpid) {
      Testpaper referenceTestpaper = testpaperDao.findOne(SearchFilter.eq("useStatus", "1"), SearchFilter.eq("isReference", "1"));
      if (referenceTestpaper != null){
         referenceTestpaper.setIsReference("0");
      }
      Testpaper testpaper = testpaperDao.findOne(tpid);
      testpaper.setIsReference("1");
   }

   /** 组卷
    *
    * @param questionListJson 题目json对象
    * @pdOid 51aeb4c9-16b4-415e-837e-edad170f1e1a */
   @Transactional
   public String makeTestpaper(JsonNode questionListJson) {
      Testpaper testpaper = new Testpaper();
      testpaper.setUseStatus("1");
      testpaper.setIsReference("0");
      testpaper.setName("");
      testpaper.setDateCreated(new Date());
      testpaper.setExpectTime(2 * 60 * 60);
      testpaper.setOrgCode(1);
      testpaper = testpaperDao.save(testpaper);
      System.out.println(testpaper.getId());
      List<TestpaperQuestiontype> testpaperQuestiontypes = new ArrayList<TestpaperQuestiontype>();
      List<TestpaperQuestion> testpaperQuestions = new ArrayList<TestpaperQuestion>();
      Iterator iterator = questionListJson.iterator();
      while(iterator.hasNext()){
         ObjectNode questionJson = (ObjectNode)iterator.next();
         TestpaperQuestiontype testpaperQuestiontype = new TestpaperQuestiontype();
         testpaperQuestiontype.setQtid(questionJson.findPath("qType").asInt());
         testpaperQuestiontype.setSeq(questionJson.findPath("seq").asInt());
         testpaperQuestiontype.setTpid(testpaper.getId());
         testpaperQuestiontypes.add(testpaperQuestiontype);
         if(questionJson.findPath("questionStr").asText().equals("")){
            continue;
         }
         if(questionJson.findPath("qType").asInt()!=SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY) {
            String[] question = questionJson.findPath("questionStr").asText().split(",");
            for (int i = 0; i < question.length; i++) {
               TestpaperQuestion testpaperQuestion = new TestpaperQuestion();
               testpaperQuestion.setQid(question[i]);
               testpaperQuestion.setTpid(testpaper.getId());
               testpaperQuestions.add(testpaperQuestion);
            }
         }
      }
      testpaperQuestionDao.save(testpaperQuestions);
      testpaperQuestiontypeDao.save(testpaperQuestiontypes);
      return Integer.toString(testpaper.getId());
   }


   /** 添加试题到试卷
    *
    * @param questionListJson 题目json对象
    * @pdOid 51aeb4c9-16b4-415e-837e-edad170f1e1a */
   @Transactional
   public void addQuestionToTestpaper(JsonNode questionListJson,Integer tpid) {
      List<TestpaperQuestiontype> testpaperQuestiontypes = new ArrayList<TestpaperQuestiontype>();
      List<TestpaperQuestion> testpaperQuestions = new ArrayList<TestpaperQuestion>();
      Iterator iterator = questionListJson.iterator();
      while(iterator.hasNext()){
         ObjectNode questionJson = (ObjectNode)iterator.next();
         if(questionJson.findPath("questionStr").asText().equals("")){
            continue;
         }
         if(questionJson.findPath("qType").asInt()!=SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY) {
            String[] question = questionJson.findPath("questionStr").asText().split(",");
            for (int i = 0; i < question.length; i++) {
               TestpaperQuestion testpaperQuestion = new TestpaperQuestion();
               testpaperQuestion.setQid(question[i]);
               testpaperQuestion.setTpid(tpid);
               testpaperQuestions.add(testpaperQuestion);
            }
         }
      }
      testpaperQuestionDao.save(testpaperQuestions);
   }

   /**
    * 关联试卷和导入试题
    * @param questionVos List
    * @param tpid
    */
   public void associateQuestionToTestpaper(Integer tpid, List<LinkedHashMap> questionListJson) {
      List<TestpaperQuestion> testpaperQuestions = new ArrayList<TestpaperQuestion>();
      Iterator iterator = questionListJson.iterator();
      while (iterator.hasNext()){
         LinkedHashMap questionJson = (LinkedHashMap)iterator.next();
         TestpaperQuestion testpaperQuestion = new TestpaperQuestion();
         testpaperQuestion.setQid(questionJson.get("id").toString());
         testpaperQuestion.setTpid(tpid);
         testpaperQuestions.add(testpaperQuestion);
      }
      testpaperQuestionDao.save(testpaperQuestions);
   }

   /**
    * 根据试卷的id查找试卷
    * @param id
    * @return
    */
   public Testpaper findOne(Integer id) {
      return testpaperDao.findOne(id);
   }

   /**添加试卷题型
    *
    * @param tpid 试卷id
    * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
   @Transactional
   public void addQuestiontype(Integer tpid,String questiontypeStr, String timeStr) {
      String[] questiontype = questiontypeStr.split(",");
      List<TestpaperQuestiontype> testpaperQuestiontypes = new ArrayList<TestpaperQuestiontype>();
      String[] limitTime = timeStr.split(",");
      Integer seq = 0;
      if(testpaperQuestiontypeDao.findByTpidOrderBySeqAsc(tpid).size()>0)
         seq = testpaperQuestiontypeDao.findByTpidOrderBySeqAsc(tpid).get(testpaperQuestiontypeDao.findByTpidOrderBySeqAsc(tpid).size()-1).getSeq();
      for (int i = 0; i < questiontype.length; i++) {
         TestpaperQuestiontype testpaperQuestiontype = new TestpaperQuestiontype();
         testpaperQuestiontype.setTpid(tpid);
         testpaperQuestiontype.setQtid(Integer.parseInt(questiontype[i]));
         testpaperQuestiontype.setSeq(seq+i+1);
         testpaperQuestiontype.setLimitTime(Integer.parseInt(limitTime[i]));
         testpaperQuestiontypes.add(testpaperQuestiontype);
      }
      testpaperQuestiontypeDao.save(testpaperQuestiontypes);
   }

   /**
    * 根据订单查询所传机构的可用试卷
    * @param orgCode
    * @return
    */
   public List<Testpaper> findAllByOrdersAndOrgCode(Integer orgCode){
      String sql = "SELECT TP.ID, TP.ORG_CODE, TP.EXPECT_TIME, TP.NAME, TP.IS_REFERENCE, TP.DATE_CREATED, TP.USE_STATUS " +
                    "FROM ORDERS OD, TESTPAPER TP\n" +
                    "WHERE OD.TPID = TP.ID\n" +
                    "AND REMAIN_NUMBER > 0 \n" +
                    "AND OD.STATUS = 1\n" +
                    "AND OD.ORG_CODE = ?1";
      return testpaperDao.findAllBySql(sql, orgCode);
   }

   /**
    * 判断所传的题型数组是否已经存在于莫套模版试卷
    * @param questiontypeList
    * @return
    */
   public boolean existTestpaper(List<Integer> questiontypeList){
      if (questiontypeList == null) return false;
      List<Testpaper> testpapers = this.findAllByOrgCode(0);
      for (Testpaper testpaper : testpapers) {
         List<TestpaperQuestiontype> list = testpaperQuestiontypeDao.findAll(SearchFilter.eq("tpid", testpaper.getId()));
         int num = 0;
         if(list.size() == questiontypeList.size()){
            for (TestpaperQuestiontype testpaperQuestiontype : list) {
               Integer qtid = testpaperQuestiontype.getQtid();
               for (int i = 0; i < questiontypeList.size(); i++) {
                  if(qtid.equals(questiontypeList.get(i))){
                     num++;
                  }
               }
            }
            if(list.size() == num){
               return true;
            }
         }
      }
      return false;
   }
}