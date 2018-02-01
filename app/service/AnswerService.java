/***********************************************************************
 * Module:  AnswerInfoService.java
 * Author:  XIAODA
 * Purpose: Defines the Class AnswerInfoService
 ***********************************************************************/

package service;

import dao.*;
import models.*;
import models.dto.AnswerDto;
import models.vo.AnswerVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;
import utils.SystemConstant;

import java.util.*;

/** 答题信息相关业务
 * 
 * @pdOid bab6e82c-80d8-4f31-8e7a-262861b77083 */
@Service
public class AnswerService {

   @Autowired
   private AnswerDao answerDao;
   @Autowired
   private AdmissionInfoDao admissionInfoDao;
   @Autowired
   private AnswerReportDao answerReportDao;
   @Autowired
   private AnswerDetailDao answerDetailDao;
   @Autowired
   private UserTestDao userTestDao;
   @Autowired
   private TestpaperUserQuestionDao testpaperUserQuestionDao;


   /**
    * 添加考生答题信息
    * @param answer
    * @return
    */
   @Transactional
   public Answer save(Answer answer) {
      return answerDao.save(answer);
   }

   @Transactional
   public List<Answer> save(List<Answer> answerList) {
      return answerDao.save(answerList);
   }

   /**
    * 根据测试题类型查询考生答题信息
    * @param  uid 用户ID
    * @param  tid 考试ID
    **/
   public List<AnswerVo> getUserAnswer(Integer tid,Integer uid){
      return answerDao.findByTidAndUid(tid,uid);
   }

   @Transactional
   public void upload(AnswerDto answerDto) {
      try {
         List<Answer> answerList = answerDto.getAnswers();
         for (Answer answer : answerList) {
            Integer qtype = answer.getQtype();

            /** 转换为题库中的答案选项 */
//            String choiceAnswer = answer.getAnswer();
//            Integer choiceIndex = SystemConstant.CHOICE_LIST.indexOf(choiceAnswer);
//            TestpaperUserQuestion userQuestion = testpaperUserQuestionDao.findOne(SearchFilter.eq("uid", answer.getUid()), SearchFilter.eq("qid", answer.getQid()));
            /** 当题型为8的时候不进行转化 */
//            if (userQuestion != null && choiceIndex >= 0 && qtype != SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING){
//               String[] choiceList = userQuestion.getChoiceseq().split(",");
//               if(choiceIndex >= choiceList.length){
//                  System.out.println(answer.getQtype());
//               }
//               answer.setAnswer(choiceList[choiceIndex]);
//            }
            answer.setDateCreated(new Date());
            if(StringUtils.isBlank(answer.getAnswer())){
               answer.setAnswer("");
//               System.out.println(answer.getQtype());
            }
         }
         if (answerList != null && answerList.size() > 0) {
            answerDao.save(answerList);
         }

         Set<UserTest> userTestSet = new HashSet();
         List<AnswerReport> answerReportList = answerDto.getAnswerReports();
         for (AnswerReport answerReport : answerReportList) {
            UserTest ut = new UserTest();
            ut.setUid(answerReport.getUid());
            ut.setTid(answerReport.getTid());
            userTestSet.add(ut);
         }
         if (answerReportList != null && answerReportList.size() > 0) {
            answerReportDao.save(answerReportList);
         }

         List<AnswerDetail> answerDetailList = answerDto.getAnswerDetails();
         for (AnswerDetail answerDetail : answerDetailList) {
            answerDetail.setDateCreated(new Date());
         }
         if (answerDetailList != null && answerDetailList.size() > 0) {
            answerDetailDao.save(answerDetailList);
         }

         for (UserTest ut: userTestSet) {
            UserTest userTest = userTestDao.findOne(SearchFilter.eq("tid", ut.getTid()), SearchFilter.eq("uid",ut.getUid()));
            userTest.setIsover("1");
            userTestDao.save(userTest);
         }

         /** 提交答案后,如果有IP绑定信息,则清除 */
         AdmissionInfo admissionInfo = admissionInfoDao.findOne(SearchFilter.eq("uid", SessionService.getSessionItemInteger(SessionService.SessionItemMark.USER_ID)));
         String ip = admissionInfo.getIp();
         if (StringUtils.isNotBlank(ip)) {
            admissionInfo.setIp(null);
            admissionInfoDao.save(admissionInfo);
         }
      } catch (Exception e){
         e.printStackTrace();
         throw new RuntimeException();
      }
   }
}