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
 * Created by XIAODA on 2015/8/7.
 */
@Service
public class SymbolicOperationService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取符号运算说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/symbolicOperation/4gif.gif");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取符号运算练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");

        for (int i = 0; i < 10; i++) {
            cacheList.add("/assets/images/symbolicOperation/answer/answer" + i + ".png");
        }
        cacheList.add("/assets/images/symbolicOperation/reminder.png");
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        return cacheList;
    }

    /**
     * 获取符号运算练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange1.png");
        return cacheList;
    }

    /**
     * 获取符号运算考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");

        for (int i = 0; i < 10; i++) {
            cacheList.add("/assets/images/symbolicOperation/answer/answer" + i + ".png");
        }
        return cacheList;
    }

    /**
     * 查找符号运算考试题
     * @param tpid
     * @param qType
     * @param ispractice
     * @return
     */
    @Transactional
    public List<Map<String,Object>> findQuestions(Integer tpid, Integer qType, String ispractice){
        List<Map<String,Object>> questionList = new ArrayList<Map<String,Object>>();

        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
//        List<Question> shuffledQuestions = getShuffledQuestionList(questions, tpid, qType, true);
        for (Question question : questions) {
            String ques = question.getQuestion().replaceAll(" ","");
            String answer = question.getAnswer();// 答案
            String choices = question.getChoices();
            JsonMapper jsonMapper = new JsonMapper();
            Map options = jsonMapper.fromJson(choices, Map.class);// 选项
            String[] q = ques.split("");// 题目
            String symbol = ques.replaceAll("\\d","");// 本试题的运算符

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("answer", answer);
            map.put("options", options);
            map.put("question", q);
            map.put("symbol", symbol);
            questionList.add(map);
        }
        return questionList;
    }


    /**
     * 生成考试试题
     *
     * @param ispractice 题目类型
     * @param questionNumber 生成题目数量
     * @return
     */
    public List<Question> makeQuestion(String ispractice,Integer questionNumber){
        SymbolicOperationFactory symbolicOperationFactory = new SymbolicOperationFactory();
        List<Question> questions = new ArrayList<Question>();
        Integer i  = 0;
        while (i<questionNumber){
            Question question = new Question();
            if(i%7 ==0){
                question = symbolicOperationFactory.add(ispractice);
            }
            if(i%7 ==1){
                question = symbolicOperationFactory.subtract(ispractice);
            }
            if(i%7 ==2){
                question = symbolicOperationFactory.multipliedTypeOne(ispractice);
            }
            if(i%7 ==3){
                question = symbolicOperationFactory.divide(ispractice);
            }
            if(i%7 ==4){
                question = symbolicOperationFactory.add(ispractice);
            }
            if(i%7 ==5){
                question = symbolicOperationFactory.subtract(ispractice);
            }
            if(i%7 ==6){
                question = symbolicOperationFactory.multipliedTypeTwo(ispractice);
            }
            if(!questions.contains(question)) {
                questions.add(question);
                i++;
            }
        }

        return questions;
    }



    private class SymbolicOperationFactory{

        public SymbolicOperationFactory(){}



        //生成不为0的一位数
        private Integer getRandomOne(){
            return new Random().nextInt(8) + 2;
        }

        //生成个位与十位不同的两位数
        private Integer getRandomTwo(){
            String str =  Integer.toString(new Random().nextInt(90) + 10);
            while (true) {
                if (str.charAt(0) == str.charAt(1)) {
                    str = Integer.toString(new Random().nextInt(90) + 10);
                }else {
                    break;
                }
            }
            return Integer.parseInt(str);
        }

        //获取c
        private Integer getC(Integer answer){
            int c[] = {-1,1,-2,2};
            while (true) {
                Integer random = new Random().nextInt(4);
                Integer num = (answer % 10) + c[random] ;
                if (num >= 0 && num <= 9) {
                    return c[random];
                }
            }
        }

        public Question add(String ispractice){
            Integer leftNum = this.getRandomTwo();
            Integer rightNum = this.getRandomTwo();
            while (true){
                if(leftNum + rightNum >= 100){
                    leftNum = this.getRandomTwo();
                    rightNum = this.getRandomTwo();
                }else {
                    break;
                }
            }
            Integer answer = leftNum + rightNum;
            Question question = new Question();
            question.setQuestion(Integer.toString(leftNum) + " + " + Integer.toString(rightNum));
            question.setQtype(2);
            question.setIspractice(ispractice);
            question.setDateCreated(new Date());
            question.setAnswer(Integer.toString(answer));
            String A = Integer.toString(answer);
            String B = Integer.toString(answer + this.getC(answer));
            String C = Integer.toString(answer - 10);
            if(new Random().nextInt(2)==1){
                C = Integer.toString(answer + 10);
            }
            if(Integer.parseInt(C)>=100){
                C = Integer.toString(answer - 10);
            }
            if(Integer.parseInt(C)<=0){
                C = Integer.toString(answer + 10);
            }
            String D = Integer.toString(Integer.parseInt(C)/10 + Integer.parseInt(B)%10);
            List<String> choice = new ArrayList<String>();
            choice.add(A);
            choice.add(B);
            choice.add(C);
            choice.add(D);
            Collections.shuffle(choice);
            Collections.shuffle(choice);
            StringBuilder choiceStr = new StringBuilder();
            for(int i = 0;i<choice.size();i++){
                switch (i){
                    case 0:
                        choiceStr.append("{\"A\":\""+choice.get(i)+"\",");
                        break;
                    case 1:
                        choiceStr.append("\"B\":\""+choice.get(i)+"\",");
                        break;
                    case 2:
                        choiceStr.append("\"C\":\""+choice.get(i)+"\",");
                        break;
                    case 3:
                        choiceStr.append("\"D\":\""+choice.get(i)+"\"}");
                        break;
                }
            }
            question.setChoices(choiceStr.toString());
            return question;
        }

        public  Question subtract(String ispractice){
            Integer leftNum = this.getRandomTwo();
            Integer rightNum = this.getRandomTwo();
            while (true){
                if(leftNum - rightNum < 0){
                    leftNum = this.getRandomTwo();
                    rightNum = this.getRandomTwo();
                }else {
                    break;
                }
            }
            Integer answer = leftNum - rightNum;
            Question question = new Question();
            question.setQuestion(Integer.toString(leftNum) + " - " + Integer.toString(rightNum));
            question.setQtype(2);
            question.setIspractice(ispractice);
            question.setDateCreated(new Date());
            question.setAnswer(Integer.toString(answer));
            String A = Integer.toString(answer);
            String B = Integer.toString(answer + this.getC(answer));
            String C = Integer.toString(answer - 10);
            if(new Random().nextInt(2)==1){
                C = Integer.toString(answer + 10);
            }
            if(Integer.parseInt(C)>=100){
                C = Integer.toString(answer - 10);
            }
            if(Integer.parseInt(C)<=0){
                C = Integer.toString(answer + 10);
            }
            String D = Integer.toString(Integer.parseInt(C)/10 + Integer.parseInt(B)%10);
            List<String> choice = new ArrayList<String>();
            choice.add(A);
            choice.add(B);
            choice.add(C);
            choice.add(D);
            Collections.shuffle(choice);
            Collections.shuffle(choice);
            StringBuilder choiceStr = new StringBuilder();
            for(int i = 0;i<choice.size();i++){
                switch (i){
                    case 0:
                        choiceStr.append("{\"A\":\""+choice.get(i)+"\",");
                        break;
                    case 1:
                        choiceStr.append("\"B\":\""+choice.get(i)+"\",");
                        break;
                    case 2:
                        choiceStr.append("\"C\":\""+choice.get(i)+"\",");
                        break;
                    case 3:
                        choiceStr.append("\"D\":\""+choice.get(i)+"\"}");
                        break;
                }
            }
            question.setChoices(choiceStr.toString());
            return question;
        }

        public Question  multipliedTypeOne(String ispractice){
            Integer leftNum = this.getRandomTwo();
            Integer rightNum = this.getRandomOne();
            while (true){
                if(leftNum % 10 == 0){
                    leftNum = this.getRandomTwo();
                }else if(leftNum/10 == rightNum || leftNum % 10 ==rightNum){
                    rightNum = this.getRandomOne();
                }else if(leftNum * rightNum>=100){
                    leftNum = this.getRandomTwo();
                    rightNum = this.getRandomOne();
                }else {
                    break;
                }
            }
            Integer answer = leftNum * rightNum;
            Question question = new Question();
            question.setQuestion(Integer.toString(leftNum) + " x " + Integer.toString(rightNum));
            question.setQtype(2);
            question.setIspractice(ispractice);
            question.setDateCreated(new Date());
            question.setAnswer(Integer.toString(answer));
            String A = Integer.toString(answer);
            String B = Integer.toString(answer + this.getC(answer));
            String C = Integer.toString(answer - 10);
            if(new Random().nextInt(2)==1){
                C = Integer.toString(answer + 10);
            }
            if(Integer.parseInt(C)>=100){
                C = Integer.toString(answer - 10);
            }
            if(Integer.parseInt(C)<=0){
                C = Integer.toString(answer + 10);
            }
            String D = Integer.toString(leftNum * (rightNum + 1));
            if(new Random().nextInt(2)==1){
                D = Integer.toString(leftNum * (rightNum - 1));
            }
            List<String> choice = new ArrayList<String>();
            choice.add(A);
            choice.add(B);
            choice.add(C);
            choice.add(D);
            Collections.shuffle(choice);
            Collections.shuffle(choice);
            StringBuilder choiceStr = new StringBuilder();
            for(int i = 0;i<choice.size();i++){
                switch (i){
                    case 0:
                        choiceStr.append("{\"A\":\""+choice.get(i)+"\",");
                        break;
                    case 1:
                        choiceStr.append("\"B\":\""+choice.get(i)+"\",");
                        break;
                    case 2:
                        choiceStr.append("\"C\":\""+choice.get(i)+"\",");
                        break;
                    case 3:
                        choiceStr.append("\"D\":\""+choice.get(i)+"\"}");
                        break;
                }
            }
            question.setChoices(choiceStr.toString());
            return question;
        }

        public Question  multipliedTypeTwo(String ispractice){
            Integer leftNum = this.getRandomTwo();
            Integer rightNum = this.getRandomOne();
            while (true){
                if(leftNum % 10 == 0){
                    leftNum = this.getRandomTwo();
                }else if(leftNum/10 == rightNum || leftNum % 10 ==rightNum){
                    rightNum = this.getRandomOne();
                }else if(leftNum * rightNum <=100 || leftNum * rightNum >110 ){
                    leftNum = this.getRandomTwo();
                    rightNum = this.getRandomOne();
                }else {
                    break;
                }
            }
            Integer answer = leftNum * rightNum;
            Question question = new Question();
            question.setQuestion(Integer.toString(leftNum) + " x " + Integer.toString(rightNum));
            question.setQtype(2);
            question.setIspractice(ispractice);
            question.setDateCreated(new Date());
            question.setAnswer(Integer.toString(answer));
            String A = Integer.toString(answer);
            String B = Integer.toString(answer + this.getC(answer));
            String C = Integer.toString(answer - 10);
            if(new Random().nextInt(2)==1){
                C = Integer.toString(answer + 10);
            }
            if(Integer.parseInt(C)>=100){
                C = Integer.toString(answer - 10);
            }
            if(Integer.parseInt(C)<=0){
                C = Integer.toString(answer + 10);
            }
            String D = Integer.toString(leftNum * (rightNum + 1));
            if(new Random().nextInt(2)==1){
                D = Integer.toString(leftNum * (rightNum - 1));
            }
            List<String> choice = new ArrayList<String>();
            choice.add(A);
            choice.add(B);
            choice.add(C);
            choice.add(D);
            Collections.shuffle(choice);
            Collections.shuffle(choice);
            StringBuilder choiceStr = new StringBuilder();
            for(int i = 0;i<choice.size();i++){
                switch (i){
                    case 0:
                        choiceStr.append("{\"A\":\""+choice.get(i)+"\",");
                        break;
                    case 1:
                        choiceStr.append("\"B\":\""+choice.get(i)+"\",");
                        break;
                    case 2:
                        choiceStr.append("\"C\":\""+choice.get(i)+"\",");
                        break;
                    case 3:
                        choiceStr.append("\"D\":\""+choice.get(i)+"\"}");
                        break;
                }
            }
            question.setChoices(choiceStr.toString());
            return question;
        }

        public Question  divide(String ispractice){
            Integer leftNum = this.getRandomTwo();
            Integer rightNum = this.getRandomOne();
            while (true){
                if(leftNum % 10 == 0){
                    leftNum = this.getRandomTwo();
                }else if(leftNum/10 == rightNum || leftNum % 10 ==rightNum){
                    rightNum = this.getRandomOne();
                }else if(leftNum % rightNum !=0 ){
                    leftNum = this.getRandomTwo();
                    rightNum = this.getRandomOne();
                }else {
                    break;
                }
            }
            Integer answer = leftNum / rightNum;
            Question question = new Question();
            question.setQuestion(Integer.toString(leftNum) + " ÷ " + Integer.toString(rightNum));
            question.setQtype(2);
            question.setIspractice(ispractice);
            question.setDateCreated(new Date());
            question.setAnswer(Integer.toString(answer));
            String A = Integer.toString(answer);
            String B = Integer.toString(answer + this.getC(answer));
            String C = Integer.toString(answer - 10);
            if(new Random().nextInt(2)==1){
                C = Integer.toString(answer + 10);
            }
            if(Integer.parseInt(C)>=100){
                C = Integer.toString(answer - 10);
            }
            if(Integer.parseInt(C)<=0){
                C = Integer.toString(answer + 10);
            }
            String D = Integer.toString(Integer.parseInt(B)+1);
            if(new Random().nextInt(2)==1){
                D = Integer.toString(Integer.parseInt(B)+2);
            }
            List<String> choice = new ArrayList<String>();
            choice.add(A);
            choice.add(B);
            choice.add(C);
            choice.add(D);
            Collections.shuffle(choice);
            Collections.shuffle(choice);
            StringBuilder choiceStr = new StringBuilder();
            for(int i = 0;i<choice.size();i++){
                switch (i){
                    case 0:
                        choiceStr.append("{\"A\":\""+choice.get(i)+"\",");
                        break;
                    case 1:
                        choiceStr.append("\"B\":\""+choice.get(i)+"\",");
                        break;
                    case 2:
                        choiceStr.append("\"C\":\""+choice.get(i)+"\",");
                        break;
                    case 3:
                        choiceStr.append("\"D\":\""+choice.get(i)+"\"}");
                        break;
                }
            }
            question.setChoices(choiceStr.toString());
            return question;
        }
    }

}
