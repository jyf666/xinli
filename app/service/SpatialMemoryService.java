package service;

import dao.QuestionDao;
import models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.SystemConstant;

import java.util.*;

/**
 * Created by XIAODA on 2015/8/11.
 */
@Service
public class SpatialMemoryService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取空间记忆说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/spatialMemory/lizi/lizi.png");
        return cacheList;
    }

    /**
     * 获取空间记忆练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/javascripts/question/spatialMemory.js");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取空间记忆练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback1.png");
        cacheList.add("/assets/images/common/clock0.png");
        cacheList.add("/assets/images/common/exchange2.png");
        return cacheList;
    }

    /**
     * 获取空间记忆考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");
        cacheList.add("/assets/javascripts/question/spatialMemory.js");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 根据机构编码和题型查询题目
     *
     * @param tpid
     * @param qType
     * @param ispractice
     * @return
     */
    public String[] getQuestions(Integer tpid, Integer qType, String ispractice){
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspracticeOrderByDifficulty(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, false);
        StringBuilder questionBuilder = new StringBuilder();
        StringBuilder qidBuilder = new StringBuilder();
        String[] questionArr = new String[2];
        for(Question question : questions){
            questionBuilder.append(question.getQuestion() + "!");
            qidBuilder.append(question.getId() + "!");
        }
        if (questionBuilder.length() > 0) {
            questionArr[0] = questionBuilder.substring(0, questionBuilder.length() - 1).toString();
        }
        if (qidBuilder.length() > 0) {
            questionArr[1] = qidBuilder.substring(0, qidBuilder.length() - 1).toString();
        }
        return questionArr;
    }

    /**
     * 生成考试试题
     *
     * @param ispractice 题目类型
     * @param questionNumber 生成题目数量
     * @return
     */
    public List<Question> makeQuestion(String ispractice, Integer questionNumber) {
        List<Question> questions = new ArrayList<Question>();
        Integer i = 0;
        if (ispractice.equals("01")) {
            while (i < questionNumber) {
                Question question = new Question();
                Integer idx = i / 3 % 13;
                Integer difficulty = idx + 3;
                if (idx == 0 || idx == 1 || idx == 2 || idx == 3 || idx == 4 || idx == 5 ||
                    idx == 6 || idx == 7 || idx == 8 || idx == 9 || idx == 10 || idx == 11 || idx == 12) {
                    question = this.addSpatialMemoryQuestion(difficulty, ispractice);
                }
                if (!questions.contains(question)) {
                    questions.add(question);
                    i++;
                }
            }
        } else {
            while (i < questionNumber) {
                Question question = this.addSpatialMemoryQuestion(3, ispractice);
                if (!questions.contains(question)) {
                    questions.add(question);
                    i++;
                }
            }
        }
        return questions;
    }

    public Question addSpatialMemoryQuestion(int difficulty, String ispractice) {
        List<Integer> list = new ArrayList<>();
        StringBuilder sbf = new StringBuilder();
        for (int i = 0; i < difficulty; i++) {
            int randomNum = 0;
            boolean f = true;
            while (f) {
                randomNum = new Random().nextInt(24);
                if (list.contains(randomNum)) {
                    continue;
                }
                list.add(randomNum);
                f = false;
            }
            String c = Integer.toString(randomNum / 5 + 1);  //显示的方块的行数
            String l = Integer.toString(randomNum % 5 + 1);  //显示的方块的列数
            sbf.append(c + ":" + l + ";");
        }
        String question = sbf.toString().substring(0, sbf.toString().length() - 1);
        Question q = new Question();
        q.setQuestion(question);
        q.setChoices("");
        q.setAnswer("");
        q.setChoicesType("1");
        q.setDifficulty(Integer.toString(difficulty));
        q.setQtype(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
        q.setIspractice(ispractice);
        q.setDateCreated(new Date());
        return q;
    }
}
