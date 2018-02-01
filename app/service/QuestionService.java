/***********************************************************************
 * Module:  QuestionService.java
 * Author:  XIAODA
 * Purpose: Defines the Class QuestionService
 ***********************************************************************/

package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.QuestionDao;
import dao.TestpaperQuestionDao;
import dao.TestpaperUserDao;
import dao.TestpaperUserQuestionDao;
import models.Question;
import models.TestpaperQuestion;
import models.TestpaperUser;
import models.TestpaperUserQuestion;
import models.vo.QuestionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.PageUtils;
import utils.SystemConstant;

import java.util.*;

import utils.json.JsonMapper;

/** 题库相关业务
 * 
 * @pdOid 92d65df1-d424-4de5-8ee4-bb0b23f61ec8 */
@Service
public class QuestionService {

   @Autowired
   private QuestionDao questionDao;
   @Autowired
   private TestpaperQuestionDao testpaperQuestionDao;
   @Autowired
   private TestpaperUserQuestionDao testpaperUserQuestionDao;

   /**
    * 添加题目到题库
    * @param questions
    */
   public List<Question> save(List<Question> questions) {
      for(Question question:questions){
         question.setUuid("");
         question.setDateCreated(new Date());
      }
      return questionDao.save(questions);
   }

   /** 添加题目到题库
    *
    * @param objectNode questionJson 数据
    * @pdOid 76af6b7c-1457-41b7-be6c-45ddcddb420a */
   public void save(ObjectNode objectNode) {
      Question question = new Question();
      if(objectNode.findPath("qType").asInt() == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING) {
         question.setQuestion(objectNode.findPath("question").asText());
         question.setChoices(objectNode.findPath("choice").asText());
         question.setAnswer(objectNode.findPath("answer").asText());
         question.setSubType(objectNode.findPath("subType").asText());
         question.setQtype(objectNode.findPath("qType").asInt());
         question.setDateCreated(new Date());
         question.setDifficulty("");
         question.setChoicesType("");
         question.setUseStatus("1");
         question.setVersion("");
      }
      if(objectNode.findPath("qType").asInt()  == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION){
         question.setQuestion(objectNode.findPath("question").asText());
         question.setChoices("");
         question.setAnswer(objectNode.findPath("answer").asText());
         question.setSubType("01");
         question.setQtype(objectNode.findPath("qType").asInt());
         question.setDateCreated(new Date());
         question.setDifficulty(objectNode.findPath("difficulty").asText());
         question.setChoicesType("");
         question.setUseStatus("1");
         question.setVersion("");
      }
      questionDao.save(question);
   }

   /**
    * 添加多个题目到题库
    **/
   public void saveQuestions(List<LinkedHashMap> questionVos) {
      List<Question> questions = new ArrayList();
      for (LinkedHashMap questionVo: questionVos) {
         String id = questionVo.get("id").toString();
         if (questionDao.findOne(SearchFilter.eq("id", id))==null){
            Question question = new Question();
            question.setId(id);
            question.setQuestion(questionVo.get("question").toString());
            question.setChoicesType(questionVo.get("choicesType").toString());
            question.setChoices(questionVo.get("choices")!=null ? questionVo.get("choices").toString() : null );
            question.setAnswer(questionVo.get("answer").toString());
            question.setSubType(questionVo.get("subType")!=null ? questionVo.get("subType").toString() : null);
            question.setQtype(Integer.parseInt(questionVo.get("qType").toString()));
            question.setDateCreated(new Date());
            question.setDifficulty(questionVo.get("difficulty")!=null ? questionVo.get("difficulty").toString() : null);
            question.setChoicesType("");
            question.setUseStatus("1");
            question.setIspractice(questionVo.get("ispractice").toString());
            question.setMaterial(questionVo.get("material").toString());
            question.setPrompt(questionVo.get("prompt").toString());
            question.setUuid("");
            question.setVersion("");
            questions.add(question);
         }else{
            continue;
         }
      }
      questionDao.save(questions);
   }

   /** 更新题目信息
    *
    * @param objectNode questionJson 数据
    * @pdOid 7d184cf9-294c-4226-8a96-f5c235d50c78 */
   public void update(ObjectNode objectNode) {
      Question question = questionDao.findOne(objectNode.findPath("qid").asText());
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING) {
         question.setQuestion(objectNode.findPath("question").asText());
         question.setChoices(objectNode.findPath("choice").asText());
         question.setAnswer(objectNode.findPath("answer").asText());
         question.setSubType(objectNode.findPath("subType").asText());
         question.setQtype(objectNode.findPath("qType").asInt());
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_PERSONALITY){
         question.setChoices(objectNode.findPath("choice").asText());
         question.setAnswer(objectNode.findPath("answer").asText());
         question.setQtype(objectNode.findPath("qType").asInt());
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE){
         question.setQuestion(objectNode.findPath("question").asText());
         question.setChoices(objectNode.findPath("choice").asText());
         question.setQtype(objectNode.findPath("qType").asInt());
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION){
         question.setAnswer("{" + objectNode.findPath("answer").asText() + "}");
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING){
         question.setAnswer("{" + objectNode.findPath("answer").asText() + "}");
      }
      if(question.getQtype() == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION){
         question.setQuestion(objectNode.findPath("question").asText());
         question.setAnswer(objectNode.findPath("answer").asText());
         question.setDifficulty(objectNode.findPath("difficulty").asText());
      }
      questionDao.save(question);
   }

   /**
    * 添加题目到试卷
    * @param questions
    * @param tpid
    */
   public void save(List<Question> questions, Integer tpid) {
      questions = save(questions);
      List<TestpaperQuestion> testpaperQuestions = new ArrayList<TestpaperQuestion>();
      for(Question question:questions){
         TestpaperQuestion testpaperQuestion = new TestpaperQuestion();
         testpaperQuestion.setQid(question.getId());
         testpaperQuestion.setTpid(tpid);
         testpaperQuestions.add(testpaperQuestion);
      }
      testpaperQuestionDao.save(testpaperQuestions);
   }

   /**
    * 分页查找考试题
    * @param request
    * @param cols 分页列表的列名对应的字段
    * @return
    */
   public Page<Question> findAll(Http.Request request, String[] cols) {
      Pageable pageable = PageUtils.getPageRequest(request, cols);
      Specification<Question> spec = DynamicSpecifications.fromRequest(request, Question.class);
      return questionDao.findAll(spec, pageable);
   }

   /**
    * 根据id查找试题
    * @param qid
    * @return
    */
   public Question findById(String qid) {
      return questionDao.findOne(qid);
   }

   /**
    * 查询所有的考试题
    * @return
    */
   public List<QuestionVo> getAllQuestion() {
      return questionDao.findAllQuestion();
   }

   /**
    * 查询对应考试的考试题
    * @param tpid
    * @return
    */
   public List<QuestionVo> getQuestionByTpid(Integer tpid) {
      return questionDao.findByTpid(tpid);
   }

   public Long count(Integer tpid, Integer qType, String ispractice) {
      List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
      return Long.valueOf(questions.size());
   }

   /** 停用题目
    * @param qid 题目id
    * @pdOid cbd41eb4-86cc-4e69-ae3a-d4caa5d882a5 */
   public void delete(String qid) {
      Question question = questionDao.findOne(qid);
      question.setUseStatus("0");
      questionDao.save(question);
   }


   /**
    * 获取考试结束进度条页面的缓存文件配置信息
    * @return
    */
   public List<String> getEndTimeManifest(){
      List<String> cacheList = new ArrayList<String>();
      cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
      cacheList.add("/assets/stylesheets/amazeUI/amazeui.min.css");
      cacheList.add("/assets/javascripts/amazeUI/amazeui.min.js");
      cacheList.add("/assets/javascripts/exam/routes.js");
      cacheList.add("/assets/images/common/logo_xinli.png");
      return cacheList;
   }


   /**
    * 将根据用户生成的试卷存入数据库
    * @param questionList List 数据
    * @param tpid
    */
   public List<Question>  getShuffledQuestionList(List<Question> questionList, Integer tpid, Integer qtype ,boolean choiceShuffle){
      Integer uid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.USER_ID);

      /* 随机题序 */
      //Collections.shuffle(questionList);
      List<Question> shuffleQuestionList = getShuffledQuestionListByQtype(questionList, qtype);
      List<String> shuffledQid = new ArrayList();

      TestpaperUser testpaperUser = new TestpaperUser();
      testpaperUser.setUid(uid);
      testpaperUser.setTpid(tpid);
      testpaperUser.setQtype(qtype);
      for (Question question : shuffleQuestionList){
         String qid = question.getId();
         String uuid = question.getUuid();
         shuffledQid.add(qid);
         if (choiceShuffle){
            String choicesShuffled = "";
            String choices = question.getChoices();
            int qtype_int = qtype.intValue();
            Integer choiceType = 0;
            switch (qtype_int){
               case 2:
                  // choice format in json
                  //{"A":"143","B":"141","C":"151","D":"153"}
                  choiceType = SystemConstant.CHOICE_TYPE_JSON;
                  break;
               case 6:
                  // choice format in json
                  // {"A":"该省既潮湿又寒冷","B":" 该省的大部分地区炎热","C":" 该省的大部分地区潮湿","D":"该省的某些地区既不寒冷也不潮湿"}
                  choiceType = SystemConstant.CHOICE_TYPE_JSON;
                  break;
               case 7:
                  // choice format in string
                  // A:22,B:40,C:20,D:41,E:32
                  choiceType = SystemConstant.CHOICE_TYPE_JSONLIKE;
                  break;
               case 8:
                  // choice format in string
                  // A,B,C,D,E,F,G,H
                  choiceType = SystemConstant.CHOICE_TYPE_STRING;
                  break;
               case 9:
                  // choice format in json
                  // {"A":"1","B":"2","C":"3","D":"4","E":"5","F":"6","G":"7","H":"8"}
                  choiceType = SystemConstant.CHOICE_TYPE_JSON;
                  break;
               case 12:
                  // choice format in json
                  // {"A":"1","B":"2","C":"3","D":"4"}
                  choiceType = SystemConstant.CHOICE_TYPE_JSON;
                  break;
               case 13:
                  // choice format in string
                  // a,b,f,d
                  choiceType = SystemConstant.CHOICE_TYPE_STRING;
                  break;
               case 14:
                  // choice format in string
                  // jk,st,a,qr
                  choiceType = SystemConstant.CHOICE_TYPE_STRING;
                  break;
               default:
                  break;
            }
            if(choiceType > 0){
               choicesShuffled = getShuffleStringChoices(choices, uuid, uid, tpid, qid, qtype, choiceType);
            }
            question.setChoices(choicesShuffled);
         }
      }
      testpaperUser.setQuestionseq(String.join(",",shuffledQid));
//      testpaperUserDao.save(testpaperUser);
      return questionList;
   }

   /**
    * 将根据用户生成的选项顺序存入数据库
    * @param choices String 数据
    * @param uuid
    * @param uid
    * @param tpid
    * @param qid
    * @param choiceType
    */
   public String getShuffleStringChoices(String choices, String uuid, Integer uid, Integer tpid, String qid, Integer qtype, Integer choiceType){

      if (SystemConstant.CHOICE_TYPE_JSON.equals(choiceType)){
         // {"A":"该省既潮湿又寒冷","B":" 该省的大部分地区炎热","C":" 该省的大部分地区潮湿","D":"该省的某些地区既不寒冷也不潮湿"}
         JsonMapper jsonMapper = new JsonMapper();
         Map optionMap = jsonMapper.fromJson(choices, Map.class);

         Set optionSet = optionMap.keySet();
         List<String> choiceOptionList = new ArrayList();
         choiceOptionList.addAll(optionSet);

         //将选项随机
         Collections.shuffle(choiceOptionList);

         List<String> choiceAnswerList = new ArrayList() {{
            for (String option : choiceOptionList){
               add(optionMap.get(option));
            }
         }};

         Map newOptions = zipList(SystemConstant.CHOICE_LIST, choiceAnswerList);
         TestpaperUserQuestion testpaperUserQuestion = testpaperUserQuestionDao.findOne(SearchFilter.eq("qid", qid), SearchFilter.eq("uid", uid));
         if (testpaperUserQuestion==null){
            testpaperUserQuestion = new TestpaperUserQuestion();
            testpaperUserQuestion.setUuid(uuid);
            testpaperUserQuestion.setUid(uid);
            testpaperUserQuestion.setTpid(tpid);
            testpaperUserQuestion.setQid(qid);
            testpaperUserQuestion.setQtype(qtype);
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceOptionList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);
         }else{
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceOptionList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);
         }

         return jsonMapper.toJson(newOptions);
      }else if (SystemConstant.CHOICE_TYPE_STRING.equals(choiceType)){
         // A,B,C,D,E,F,G,H
         List choiceList = Arrays.asList(choices.split(","));
         Collections.shuffle(choiceList);
         TestpaperUserQuestion testpaperUserQuestion = testpaperUserQuestionDao.findOne(SearchFilter.eq("qid", qid), SearchFilter.eq("uid", uid));
         if (testpaperUserQuestion==null){
            testpaperUserQuestion = new TestpaperUserQuestion();
            testpaperUserQuestion.setUuid(uuid);
            testpaperUserQuestion.setUid(uid);
            testpaperUserQuestion.setTpid(tpid);
            testpaperUserQuestion.setQid(qid);
            testpaperUserQuestion.setQtype(qtype);
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);
         }else{
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);

         }
         return String.join(",", choiceList);
      }else if (SystemConstant.CHOICE_TYPE_JSONLIKE.equals(choiceType)){
         // A:22,B:40,C:20,D:41,E:32
         TestpaperUserQuestion testpaperUserQuestion = testpaperUserQuestionDao.findOne(SearchFilter.eq("qid", qid), SearchFilter.eq("uid", uid));
         List<String> choiceListTemp = Arrays.asList(choices.split(","));
         Collections.shuffle(choiceListTemp);
         List<String> choiceAnswerList = new ArrayList();
         List<String> choiceOptionList = new ArrayList();
         for (String choice : choiceListTemp){
            choiceAnswerList.add(choice.split(":")[1]);
            choiceOptionList.add(choice.split(":")[0]);
         }
         if (testpaperUserQuestion==null){
            testpaperUserQuestion = new TestpaperUserQuestion();
            testpaperUserQuestion.setUuid(uuid);
            testpaperUserQuestion.setUid(uid);
            testpaperUserQuestion.setTpid(tpid);
            testpaperUserQuestion.setQid(qid);
            testpaperUserQuestion.setQtype(qtype);
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceOptionList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);
         }else{
            testpaperUserQuestion.setChoiceseq(String.join(",", choiceOptionList));
            testpaperUserQuestionDao.save(testpaperUserQuestion);
         }

         return String.join(",", choiceAnswerList);
      }
      return "";
  }

   /**
    * 将所传的题号随机排序
    * @param ques
    * @return
    */
   protected List getShuffleQuestion(String ques){
      if(StringUtils.isNotBlank(ques) && ques.contains(",")){
         String[] arr = ques.split(",");
         List list = Arrays.asList(arr);
         Collections.shuffle(list);
         return list;
      }
      return null;
   }

   /**
    * 根据题型随机单元长度进行题目随机
    * @param questionList
    * @param qtype
    * @return
    */
   protected List<Question> getShuffledQuestionListByQtype(List<Question> questionList, Integer qtype){
      Integer step = 1;
      int int_qtype = qtype.intValue();
      switch (int_qtype){
         case 1: //材料记忆,3
            step = 3;
            break;
         case 3: //顺序记忆,3
            step = 3;
            break;
         case 4: //图案搜索,5
            step = 5;
            break;
         case 5: //图形连线,3
            step = 3;
            break;
         case 6: //言语推理,5
            step = 5;
            break;
//         case 7: //配对记忆,3
//            step = 3;
//            break;
         case 8: //类比推理,5
            step = 5;
            break;
         case 9: //矩阵推理,3
            step = 3;
            break;
         case 12://空间能力,5
            step = 5;
            break;
         case 13: //情绪识别,5
            step = 5;
            break;
         case 14: //情绪理解,5
            step = 5;
            break;
         case 15: //远距离联想,10
            step = 10;
            break;
         default:
            break;
      }
      List<Question> newQuestionList = new ArrayList();
      if (step == 1 || questionList.size() <= step){
         Collections.shuffle(questionList);
         return questionList;
      }else{
         int i = 0;
         while (i + step < questionList.size()){
            List<Question> tmpList = questionList.subList(i, i+step);
            Collections.shuffle(tmpList);
            newQuestionList.addAll(tmpList);
            i+=step;
         }
         List<Question> lastList = questionList.subList(i, questionList.size());
         Collections.shuffle(lastList);
         newQuestionList.addAll(lastList);
         return newQuestionList;
      }
   }

   /**
    * 将两个List压缩成Map
    * @param listA
    * @param listB
    * @return map Map
    * */
   protected Map zipList(List listA, List listB){
      Map map = new HashMap();
      if (listA.size() <= listB.size()){
         for (int i=0; i < listA.size();i++){
            map.put(listA.get(i), listB.get(i));
         }
      }else{
         for (int i=0; i < listB.size();i++){
            map.put(listA.get(i), listB.get(i));
         }
      }
      return map;
   }
   /**
    * 校验导入的题目
    * @param questionVos List
    * @param errorQuestionVos List
    * @param rightQuestionVos List
    * @return
    * */
   public void validateQuestion(List<QuestionVo> questionVos, List<QuestionVo> errorQuestionVos, List<QuestionVo> rightQuestionVos){

   }
}

