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
 * Created by XIAODA on 2015/9/17.
 */
@Service
public class SpaceRotationService extends QuestionService {

    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取空间旋转说明页面的缓存文件配置信息
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
     * 获取空间旋转练习页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getPracticeManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        List<String> questionCacheList = getQuestionImgManifest(tpid, qtype, ispractice);
        cacheList.addAll(questionCacheList);
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取空间旋转练习结束说明页面的缓存文件配置信息
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
     * 获取空间旋转考试页面的缓存文件配置信息
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
        return cacheList;
    }

    /**
     * 获取空间旋转书签页面的缓存文件配置信息
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
     * 获取空间旋转题目对应的图片缓存文件配置信息
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
            cacheList.add("/assets/images/spaceRotation/question/" + ques + "_t.png");
            cacheList.add("/assets/images/spaceRotation/answer/" + ques + "_1.png");
            cacheList.add("/assets/images/spaceRotation/answer/" + ques + "_2.png");
            cacheList.add("/assets/images/spaceRotation/answer/" + ques + "_3.png");
            cacheList.add("/assets/images/spaceRotation/answer/" + ques + "_4.png");
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