package service.exam.criticalThinking;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.QuestionService;
import service.QuestionTypeService;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.util.*;

/**
 * Created by XIAODA on 2016/3/25.
 */
@Service
public class TendencyService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取批判性思维倾向说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取批判性思维倾向练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
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
     * 获取批判性思维倾向考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");

        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/marked.png");
        cacheList.add("/assets/images/familyQuestionnaire/ladder.png");
        return cacheList;
    }

    /**
     * 获取批判性思维倾向书签页面的缓存文件配置信息
     * @return
     */
    public List<String> getMarkManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/stylesheets/exam/question.css");

        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/dingzi.png");
        return cacheList;
    }

    /**
     * 根据机构编码和题型查询题目
     * @param tpid
     * @param qType
     * @return
     */
    @Transactional
    public List<Map<String, Object>> findQuestions(Integer tpid, Integer qType, String ispractice){

        List<Map<String, Object>> questionList = new ArrayList();
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
        for (Question question : questions) {
            String choices = question.getChoices();
            JsonMapper jsonMapper = new JsonMapper();
            Map options = jsonMapper.fromJson(choices, LinkedHashMap.class);// 选项

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("question", question.getQuestion());
            map.put("options", options);
            questionList.add(map);
        }
        return questionList;
    }
}
