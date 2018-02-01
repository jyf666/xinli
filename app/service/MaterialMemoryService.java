package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.api.libs.json.Json;
import scala.util.parsing.json.JSON;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.util.*;

/**
 * Created by XIAODA on 2015/8/10.
 */
@Service
public class MaterialMemoryService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取材料记忆或记忆提取说明页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getInstructionsManifest(Integer tpid, Integer qtype){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        if (SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY.equals(qtype)) {
            cacheList.add("/assets/images/materialMemory/lizi/lizi1.jpg");
            cacheList.add("/assets/images/materialMemory/lizi/1gif.gif");
        } else if (SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT.equals(qtype)) {
            cacheList.add("/assets/images/common/feedback0.png");
            cacheList.add("/assets/images/common/clock0.png");
            cacheList.add("/assets/images/common/exchange1.png");
            cacheList.add("/assets/images/common/tag1.png");

            cacheList.add("/assets/images/common/mark.png");
            cacheList.add("/assets/images/common/seemark.png");
        }

        return cacheList;
    }

    /**
     * 获取材料记忆或记忆提取考试页面的缓存文件配置信息
     * @param qtype
     * @return
     */
    public List<String> getTestManifest(Integer qtype){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");

        if (SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT.equals(qtype)) {
            cacheList.add("/assets/images/common/seemark.png");
            cacheList.add("/assets/images/common/mark.png");
            cacheList.add("/assets/images/common/marked.png");
        }
        for (int i = 1; i <= 100; i++) {
            cacheList.add("/assets/images/materialMemory/question/" + i + ".png");
            cacheList.add("/assets/images/materialMemory/answer/" + i + ".png");
        }
        return cacheList;
    }

    /**
     * 获取记忆提取书签页面的缓存文件配置信息
     * @return
     */
    public List<String> getMarkManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/dingzi.png");
        return cacheList;
    }

    /**
     * 根据机构编码和题型查询题目
     * @param tpid
     * @param qType
     * @param ispractice
     * @return
     */
    public List<Question> getMaterialMemoryQuestion(Integer tpid, Integer qType,String ispractice){
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        Collections.shuffle(questions);
        return questions;
    }

    /**
     * 根据题型查询题目
     *
     * @param tpid
     * @param qType
     * @return
     */
    public List<Map<String, List<Integer>>> getQuestion(Integer tpid, Integer qType,String ispractice){
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, true);
        List<Map<String, List<Integer>>> list=  new ArrayList<Map<String, List<Integer>>>();
        for(Question question:questions) {
            Map map = new HashMap();
            String choices = question.getChoices();
            JsonMapper jsonMapper = new JsonMapper();
            List options = jsonMapper.fromJson(choices, List.class);// 选项\
            Collections.shuffle(options);
            map.put("question", question.getQuestion());
            map.put("qid",question.getId());
            map.put("options", options);
            list.add(map);
        }
      /*Collections.shuffle(list);*/
        return list;
    }


    public List<Question> makeQuestion() {
        List<Integer> list = new ArrayList(100);
        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        List<Integer> quesList = new ArrayList(100);
        quesList.addAll(list.subList(0, 54));
        Collections.shuffle(list);
        List<Integer> answerList = new ArrayList(100);
        answerList.addAll(list.subList(0, 54));

        List<Question> questionList = new ArrayList<Question>();
        for(int i = 0; i < 54; i++){
            int answer = answerList.get(i);
            int ques = quesList.get(i);
            Collections.shuffle(list);
            List<Integer> choiceList = new ArrayList(5);
            choiceList.addAll(list.subList(0, 5));
            if(!choiceList.contains(answer)){
                choiceList.remove(0);
                choiceList.add(answer);
            }
            JsonMapper jsonMapper = new JsonMapper();
            String choices = jsonMapper.toJson(choiceList);


            Question question = new Question();
            question.setQuestion(String.valueOf(ques));// 测试题目
            question.setChoices(choices);// 测试选项
            question.setAnswer(String.valueOf(answer));// 参考答案
            question.setQtype(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);// 题目类型1-11
            question.setIspractice(SystemConstant.QUESTION_ISPRACTICE_NO);
            question.setDateCreated(new Date());// 创建日期
            questionList.add(question);
        }
        return questionList;
    }
}
