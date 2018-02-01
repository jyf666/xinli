package service.exam.criticalThinking;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;
import service.QuestionService;
import service.QuestionTypeService;
import utils.SystemConstant;

import java.util.*;

/**
 * Created by XIAODA on 2016/3/17.
 */
@Service
public class AbilityService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取批判性思维能力说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取批判性思维能力练习结束说明页面的缓存文件配置信息
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
     * 获取批判性思维能力练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/stylesheets/exam/criticalThinking/ability.css");
        cacheList.add("/assets/javascripts/exam/criticalThinking/ability/practice.js");

        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取批判性思维能力考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/stylesheets/exam/criticalThinking/ability.css");
        cacheList.add("/assets/javascripts/question/test.js");
        cacheList.add("/assets/javascripts/exam/criticalThinking/ability/test.js");

        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/marked.png");
        cacheList.add("/assets/images/familyQuestionnaire/ladder.png");
        return cacheList;
    }

    /**
     * 获取批判性思维能力书签页面的缓存文件配置信息
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
     * @param subType
     * @return
     */
    @Transactional
    public List<Map<String, Object>> findQuestions(Integer tpid, Integer qType, String subType, String ispractice){

        List<Map<String, Object>> questionList = new ArrayList();
        List<Question> questions = questionDao.findAll(tpid, qType, subType, ispractice);
        for (Question question : questions) {
            String choices = question.getChoices();
            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("material", question.getMaterial());
            map.put("question", question.getQuestion());
            map.put("answer", question.getAnswer());
            map.put("options", choices.split(","));
            map.put("prompt", question.getPrompt());
            questionList.add(map);
        }
        return questionList;
    }

    public List<Map<String, Object>> getQuestionList(List<Map<String, Object>> questions){
        Set<Map<String, Object>> set = new HashSet();
        for (Map<String, Object> questionMap: questions) {
            String material = questionMap.get("material").toString();
            Map<String, Object> map = new HashMap();
            map.put("material", material);
            map.put("subQuestionList", new ArrayList());
            set.add(map);
        }

        for (Map<String, Object> map : set) {
            String material1 = map.get("material").toString();

            for (Map<String, Object> questionMap: questions) {
                String material2 = questionMap.get("material").toString();
                if(material1.equals(material2)){
                    ((List)map.get("subQuestionList")).add(questionMap);
                }
            }
        }
        return new ArrayList<Map<String, Object>>(set);
    }
}


