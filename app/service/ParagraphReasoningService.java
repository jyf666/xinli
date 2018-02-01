package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/9/9.
 */
@Service
public class ParagraphReasoningService extends QuestionService {
    
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取段落推理说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/wrong.png");
        cacheList.add("/assets/images/common/right.png");
        return cacheList;
    }

    /**
     * 获取段落推理练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取段落推理练习结束说明页面的缓存文件配置信息
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
     * 获取段落推理考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");

        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/marked.png");
        return cacheList;
    }

    /**
     * 获取段落推理书签页面的缓存文件配置信息
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
    @Transactional
    public List<Map<String, Object>> findQuestions(Integer tpid, Integer qType, String ispractice){

        List<Map<String, Object>> questionList = new ArrayList();
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, true);
        for (Question question : questions) {
            String ques = question.getQuestion();
            String answer = question.getAnswer();
            String choices = question.getChoices();
            JsonMapper jsonMapper = new JsonMapper();
            Map options = jsonMapper.fromJson(choices, Map.class);// 选项

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("answer", answer);
            map.put("question", ques);
            map.put("options", options);
            questionList.add(map);
        }
        return questionList;
    }
}
