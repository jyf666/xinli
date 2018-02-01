package service;

import dao.QuestionDao;
import dao.QuestionTypeDao;
import models.Question;
import models.Questiontype;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SystemConstant;

import java.util.*;

/**
 * Created by XIAODA on 2015/8/6.
 */
@Service
public class GrapfSearchService extends QuestionService {

    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取图片搜索说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/grapfSearch/grapfSearchExample.png");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取图片搜索练习页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getPracticeManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        Set set = getQuestionImgManifest(tpid, qtype, ispractice);
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            String imgNum = (String) iterator.next();
            cacheList.add("/assets/images/grapfSearch/" + imgNum + ".png");
        }
        cacheList.add("/assets/images/common/right.png");
        cacheList.add("/assets/images/common/wrong.png");
        cacheList.add("/assets/images/common/hongquan.png");

        cacheList.add("/assets/javascripts/amazeUI/amazeui.js");
        cacheList.add("/assets/stylesheets/amazeUI/amazeui.css");
        cacheList.add("/assets/javascripts/question/test.js");
        return cacheList;
    }

    /**
     * 获取图片搜索练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback0.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange2.png");
        return cacheList;
    }

    /**
     * 获取图片搜索考试页面的缓存文件配置信息
     * @param tpid
     * @param qtype
     * @return
     */
    public List<String> getTestManifest(Integer tpid, Integer qtype, String ispractice){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");
        cacheList.add("/assets/javascripts/amazeUI/amazeui.js");
        cacheList.add("/assets/stylesheets/amazeUI/amazeui.css");
        Set set = getQuestionImgManifest(tpid, qtype, ispractice);
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            String imgNum = (String) iterator.next();
            cacheList.add("/assets/images/grapfSearch/" + imgNum + ".png");
        }
        return cacheList;
    }

    /**
     * 获取图片搜索题目对应的图片缓存文件配置信息
     * @param tpid
     * @param qtype
     * @param ispractice
     * @return
     */
    private Set getQuestionImgManifest(Integer tpid, Integer qtype, String ispractice){
        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice( tpid, qtype, ispractice);
        Set set = new HashSet();
        for (Question question:questions){
            String ques = question.getQuestion();
            String[] imgNumArray = ques.split(",");
            for (int i = 0; i < imgNumArray.length; i++) {
                set.add(imgNumArray[i]);
            }
        }
        return  set;
    }

    /**
     * 根据机构编码和题型查询题目
     *
     * @param qType
     * @return
     */
    @Transactional
    public List<Map<String,List<List<String>>>> findQuestions(Integer tpid, Integer qType,String ispractice){
        List<Map<String,List<List<String>>>> questionList = new ArrayList<Map<String,List<List<String>>>>();

        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice( tpid, qType, ispractice);
        for (Question question : questions) {
            String ques = question.getQuestion();
            String answer = question.getAnswer();

            List<List<String>> grapfList = new ArrayList<List<String>>();
            List list = getShuffleQuestion(ques);
            for (int i = 0; i < 5; i++) {
                grapfList.add(list.subList(i*8, i*8+8));
            }

            Map map = new HashMap();
            map.put("qid", question.getId());
            map.put("answer", answer);
            map.put("grapfList", grapfList);
            questionList.add(map);
        }
        return questionList;
    }

    /**
     * 将图形搜索题型插到数据库题库表中
     */
    public List<Question> addGrapfSearch(String ispractice){

        Set grapfSearchSet = getGrapfSearch();
        Iterator it = grapfSearchSet.iterator();

        List<Question> questionList = new ArrayList<Question>();
        while(it.hasNext()){
            String ques = (String)it.next();
            String answer = ques.substring(0,ques.indexOf(','));
            Question question = new Question();
            question.setQuestion(ques+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");// 测试题目
            question.setChoices("");// 测试选项
            question.setChoicesType("");// 诱选项类型
            question.setAnswer(answer);// 参考答案
            question.setQtype(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);// 题目类型1-11
            question.setIspractice(ispractice);// 子类型
            question.setDifficulty("");// 难度（根据难度公式计算得出）
            question.setVersion("");// 修订版本
            question.setDateCreated(new Date());// 创建日期
            questionList.add(question);
        }
        if(!questionList.isEmpty()){
            return questionList;
        }
        return null;
    }

    /**
     * 随机生成图形搜索试题
     */
    public Set getGrapfSearch(){
        List<Graphics> list = new ArrayList<Graphics>();
        list.add(new Graphics(1,1,1,1));
        list.add(new Graphics(2,1,1,2));
        list.add(new Graphics(3,1,1,3));

        list.add(new Graphics(4,1,2,1));
        list.add(new Graphics(5,1,2,2));
        list.add(new Graphics(6,1,2,3));

        list.add(new Graphics(7,1,3,1));
        list.add(new Graphics(8,1,3,2));
        list.add(new Graphics(9,1,3,3));

        list.add(new Graphics(10,1,4,1));
        list.add(new Graphics(11,1,4,2));
        list.add(new Graphics(12,1,4,3));

        list.add(new Graphics(13,2,2,1));
        list.add(new Graphics(14,2,2,2));
        list.add(new Graphics(15,2,2,3));

        list.add(new Graphics(16,2,1,1));
        list.add(new Graphics(17,2,1,2));
        list.add(new Graphics(18,2,1,3));

        list.add(new Graphics(19,2,3,1));
        list.add(new Graphics(20,2,3,2));
        list.add(new Graphics(21,2,3,3));

        list.add(new Graphics(22,2,4,1));
        list.add(new Graphics(23,2,4,2));
        list.add(new Graphics(24,2,4,3));

        list.add(new Graphics(25,3,1,1));
        list.add(new Graphics(26,3,1,2));
        list.add(new Graphics(27,3,1,3));

        list.add(new Graphics(28,3,2,1));
        list.add(new Graphics(29,3,2,2));
        list.add(new Graphics(30,3,2,3));

        list.add(new Graphics(31,3,3,1));
        list.add(new Graphics(32,3,3,2));
        list.add(new Graphics(33,3,3,3));

        list.add(new Graphics(34,3,4,1));
        list.add(new Graphics(35,3,4,2));
        list.add(new Graphics(36,3,4,3));

        list.add(new Graphics(37,4,1,1));
        list.add(new Graphics(38,4,1,2));
        list.add(new Graphics(39,4,1,3));

        list.add(new Graphics(40,4,2,1));
        list.add(new Graphics(41,4,2,2));
        list.add(new Graphics(42,4,2,3));

        list.add(new Graphics(43,4,3,1));
        list.add(new Graphics(44,4,3,2));
        list.add(new Graphics(45,4,3,3));

        list.add(new Graphics(46,4,4,1));
        list.add(new Graphics(47,4,4,2));
        list.add(new Graphics(48,4,4,3));

        Set setAll = new HashSet();
        Collections.shuffle(list);// 打乱顺序
        for(Graphics graphics:list){
            List<Integer> tong2list =  new ArrayList<Integer>();// 与答案有两个相同属性的所有图形
            List<Graphics> tong1list =  new ArrayList<Graphics>();// 与答案只有一个相同属性的所有图形
            Set set2 = new HashSet();// 与答案有两个相同属性的图形三个分一组
            Set set236 = new HashSet();// 与答案有且只有一个相同属性的图形按照236规则分组

            int num = graphics.getNum();// 答案图形对映的图片编号
            int xz=graphics.getShape();
            int wl=graphics.getTexture();
            int cl=graphics.getColor();
            for(Graphics tx:list){
                int num2 = tx.getNum();
                int xz2=tx.getShape();
                int wl2=tx.getTexture();
                int cl2=tx.getColor();
                if(xz==xz2 && wl==wl2 && cl!=cl2){
                    tong2list.add(num2);
                }else if(xz==xz2 && wl!=wl2 && cl==cl2){
                    tong2list.add(num2);
                }else if(xz!=xz2 && wl==wl2 && cl==cl2){
                    tong2list.add(num2);
                }

                else if(xz==xz2 && wl!=wl2 && cl!=cl2){
                    tx.setType(1);
                    tong1list.add(tx);
                }else if(xz!=xz2 && wl==wl2 && cl!=cl2){
                    tx.setType(2);
                    tong1list.add(tx);
                }else if(xz!=xz2 && wl!=wl2 && cl==cl2){
                    tx.setType(3);
                    tong1list.add(tx);
                }
            }

            for(Graphics tx1:tong1list){
                int num1 = tx1.getNum();
                int type1=tx1.getType();

                for(Graphics tx2:tong1list){
                    int num2 = tx2.getNum();
                    int type2=tx2.getType();
                    if(type1!=type2){
                        for(Graphics tx3:tong1list){
                            int num3 = tx3.getNum();
                            int type3=tx3.getType();
                            if(type1!=type3 && type2 != type3){
                                Set set = new HashSet();
                                set.add(num1);
                                set.add(num2);
                                set.add(num3);
                                set236.add(set);
                            }
                        }
                    }
                }
            }

            for(int i=0; i< tong2list.size()-1;i++){
                int nm1 = (int)tong2list.get(i);
                for(int j=i+1; j< tong2list.size()-1;j++){
                    int nm2 = (int)tong2list.get(j);
                    for(int k = j+1; k< tong2list.size()-1;k++) {
                        int nm3 = (int) tong2list.get(k);
                        Set set = new HashSet();
                        set.add(nm1);
                        set.add(nm2);
                        set.add(nm3);
                        set2.add(set);
                    }
                }
            }

            Iterator it1 = set2.iterator();
            whileOne:
            while(it1.hasNext()){
                String pic="";
                Set st1= (HashSet)it1.next();
                Iterator it2 = st1.iterator();
                while(it2.hasNext()){
                    int picNum= (int)it2.next();
                    pic += ","+picNum + ","+picNum;
                }
                if(StringUtils.isNotBlank(pic)){
                    Iterator it3 = set236.iterator();
                    while(it3.hasNext()){
                        Set st3= (HashSet)it3.next();

                        List list236 = new ArrayList(st3);
                        String img_1= num + pic + ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2);
                        String img_2= num + pic + ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1);
                        String img_3= num + pic + ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2);
                        String img_4= num + pic + ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(2) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0);
                        String img_5= num + pic + ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0);
                        String img_6= num + pic + ","+list236.get(2) + ","+list236.get(2)+ ","+list236.get(0) + ","+list236.get(0)+ ","+list236.get(0) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1) + ","+list236.get(1)+ ","+list236.get(1);
// 生成固定数量的题目时注释掉下面的img_2，img_4，img_6，并添加下面的if判断
                        setAll.add(img_1);
//                        setAll.add(img_2);
                        setAll.add(img_3);
//                        setAll.add(img_4);
                        setAll.add(img_5);
//                        setAll.add(img_6);
                        if (setAll != null && setAll.size() > 150) {
                            return setAll;
                        } else {
                            break whileOne;
                        }
                    }
                }
            }

        }

        return setAll;

    }

    /**
     * 图形搜索图片对应的类
     */
    private class Graphics{

        private int num;// 图形对映的图片编号
        private int shape;// 形状
        private int texture;// 纹理
        private int color;// 颜色

        private int type;// 形状：1；纹理：2；颜色：3；

        public Graphics(int num,int shape,int texture,int color) {
            this.num = num;
            this.shape = shape;
            this.texture = texture;
            this.color = color;
        }

        public int getNum() {
            return num;
        }

        public int getColor() {
            return color;
        }

        public int getTexture() {
            return texture;
        }

        public int getShape() {
            return shape;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
