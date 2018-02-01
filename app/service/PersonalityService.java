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
 * Created by XIAODA on 2015/9/11.
 */
@Service
public class PersonalityService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取人格测试说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/spaceRotation/example.gif");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取人格测试练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> practiceEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock0.png");
        cacheList.add("/assets/images/common/exchange1.png");
        cacheList.add("/assets/images/common/tag1.png");

        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/seemark.png");
        return cacheList;
    }

    /**
     * 获取人格测试练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取人格测试考试页面的缓存文件配置信息
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
     * 获取人格测试书签页面的缓存文件配置信息
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
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, false);
        for (Question question : questions) {
            String choices = question.getChoices();
            JsonMapper jsonMapper = new JsonMapper();
            Map options = jsonMapper.fromJson(choices, Map.class);// 选项

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("options", options);
            questionList.add(map);
        }
        return questionList;
    }
}
