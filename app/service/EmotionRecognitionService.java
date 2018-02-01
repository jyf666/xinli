package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.util.*;

/**
 * Created by XIAODA on 2015/10/28.
 */
@Service
public class EmotionRecognitionService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取情绪识别说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/videos/emotionRecognition/a/139.mp4");
        return cacheList;
    }

    /**
     * 获取情绪识别练习页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getPracticeManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.addAll(getQuestionMp4Manifest(tpid, qtype, ispractice));

        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/javascripts/question/test.js");
        return cacheList;
    }

    /**
     * 获取情绪识别练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange3.png");
        return cacheList;
    }

    /**
     * 获取情绪识别考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.addAll(getQuestionMp4Manifest(tpid, qtype, ispractice));
        cacheList.add("/assets/javascripts/question/test.js");
        return cacheList;
    }

    /**
     * 获取情绪识别书签页面的缓存文件配置信息
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
     * 获取情绪识别题目对应的mp4缓存文件配置信息
     * @param tpid
     * @param qtype
     * @param ispractice
     * @return
     */
    private List<String> getQuestionMp4Manifest(Integer tpid, Integer qtype, String ispractice){
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qtype, ispractice);
        List<String> cacheList = new ArrayList();
        for (Question question:questions){
            String ques = question.getQuestion();
            cacheList.add("/assets/videos/emotionRecognition/" + ques + ".mp4");
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
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, false);
        for (Question question : questions) {
            String ques = question.getQuestion();
            String choices = question.getChoices();
            List options = getShuffleQuestion(choices);
            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("question", ques);
            map.put("options", options);
            questionList.add(map);
        }
        return questionList;
    }
}
