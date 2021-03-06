package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SystemConstant;

import java.util.*;

/**
 * Created by XIAODA on 2015/8/25.
 */
@Service
public class AnalogicReasoningService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取类比推理说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/analogicReasoning/example.png");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取类比推理练习页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getPracticeManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");

        List<String> questionCacheList = getQuestionImgManifest(tpid, qtype, ispractice);
        cacheList.addAll(questionCacheList);
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取类比推理练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange1.png");
        cacheList.add("/assets/images/common/tag1.png");

        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/seemark.png");
        return cacheList;
    }

    /**
     * 获取类比推理考试页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getTestManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");
        List<String> questionCacheList = getQuestionImgManifest(tpid, qtype, ispractice);
        cacheList.addAll(questionCacheList);

        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/marked.png");
        return cacheList;
    }

    /**
     * 获取类比推理书签页面的缓存文件配置信息
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
     * 获取类比推理题目对应的图片缓存文件配置信息
     * @param tpid
     * @param qtype
     * @param ispractice
     * @return
     */
    private List<String> getQuestionImgManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qtype, ispractice);
        for (Question question:questions){
            String ques = question.getQuestion();
            String choices = question.getChoices();
            String[] choiceArr = choices.split(",");
            cacheList.add("/assets/images/analogicReasoning/question/question" + ques + ".png");
            for (int i = 0; i < choiceArr.length ; i++) {
                cacheList.add("/assets/images/analogicReasoning/answer/answer" + ques + "_" + choiceArr[i] + ".png");
            }
        }
        return  cacheList;
    }

    /**
     * 根据机构编码和题型查询题目
     * @param tpid
     * @param qType
     * @param ispractice
     * @return
     */
    @Transactional
    public List<Map<String, Object>> findQuestions(Integer tpid, Integer qType, String ispractice){
        List<Map<String, Object>> questionList = new ArrayList();

        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, true);
        for (Question question : questions) {
            String ques = question.getQuestion();
            String choices = question.getChoices();
            String answer = question.getAnswer();
            String[] choiceArr = choices.split(",");

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("answer", answer);
            map.put("question", ques);
            map.put("choiceArr", choiceArr);
            questionList.add(map);
        }
        return questionList;
    }
}
