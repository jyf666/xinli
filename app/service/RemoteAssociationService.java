package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SystemConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/11/10.
 */
@Service
public class RemoteAssociationService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取距离联想说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取距离联想练习页面的缓存文件配置信息
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
     * 获取距离联想练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange1.png");
        return cacheList;
    }

    /**
     * 获取距离联想考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/javascripts/question/test.js");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/marked.png");
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
    public List<List<Map<String, Object>>> findQuestions(Integer tpid, Integer qType, String ispractice){
        List<List<Map<String, Object>>> questionList = new ArrayList();
        List<Map<String, Object>> subQuestionList = new ArrayList();
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, false);
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String ques = question.getQuestion();
            String answer = question.getAnswer();
            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("question", ques);
            map.put("answer", answer);
            if(ispractice.equals(SystemConstant.QUESTION_ISPRACTICE_YES)){
                String reminder = question.getChoices();
                map.put("reminder", reminder);
            }
            if((i+1)%5==0){
                subQuestionList.add(map);
                questionList.add(subQuestionList);
                subQuestionList = new ArrayList();
            } else {
                subQuestionList.add(map);
            }
        }
        if(subQuestionList.size()>0){
            questionList.add(subQuestionList);
        }
        return questionList;
    }
}

