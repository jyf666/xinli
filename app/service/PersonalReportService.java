package service;

import com.fasterxml.jackson.databind.JsonNode;
import dao.*;
import models.*;
import models.vo.ScoreVo;
import models.vo.TestVo;
import models.vo.TestpaperVo;
import models.vo.UserVo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.SearchFilter;
import play.libs.Json;
import utils.SystemConstant;
import utils.enums.DimensionEnum;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wangbin on 2015/11/26.
 */
@Service
public class PersonalReportService {

    @Autowired
    private ScoreDao scoreDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionTypeDao questionTypeDao;
    @Autowired
    private AdmissionsOrgDao admissionsOrgDao;
    @Autowired
    private TestpaperDao testpaperDao;
    @Autowired
    private TestDao testDao;

    private JsonNode descriptionData;

    public Map<String, Object> getUserAbstract(Integer uid,Integer tid,Integer orgCode){
        Map<String,Object> pdfData = new HashMap<>();
        this.getDescriptionData();

        //获取该用户的信息
        UserVo userVo = userDao.findUserVoByUidHaveGradeAndClass(uid);
        //第3章节模拟数据
        Map<String,Object> sectionsThree = this.getThirdChapterData(userVo, tid, orgCode);

        Map<String,Object> ability = getAbilityData(userVo, tid, orgCode);
        pdfData.put("ability", ability);
        pdfData.put("sectionsThree",sectionsThree);

        return pdfData;
    }

    public  Map<String,Object> getUserPdfData(Integer uid,Integer tid,Integer orgCode){
        this.getDescriptionData();
        Map<String,Object> pdfData = new HashMap<>();

        //获取该用户的信息
        UserVo userVo = userDao.findUserVoByUidHaveGradeAndClass(uid);
        //第2章节模拟数据
        Map<String,Object>  sectionsTwo = this.getSecondChapterData(userVo, tid, orgCode);
        //第3章节模拟数据
        Map<String,Object> sectionsThree = this.getThirdChapterData(userVo, tid, orgCode);
        //第4章节模拟数据
        Map<String,Object> sectionsFour = this.getFourChapterData(userVo, tid, orgCode);

        pdfData.put("sectionsTwo", sectionsTwo);
        pdfData.put("sectionsThree", sectionsThree);
        pdfData.put("sectionsFour",sectionsFour);

        return pdfData;
    }

    public Map<String,Object> getAbilityData(UserVo userVo,Integer tid,Integer orgCode){
        Map<String,Object> ability = new HashedMap();
        StringBuilder explain = new StringBuilder();
        Score totalScore = scoreDao.findOne(SearchFilter.eq("uid", userVo.getUid()), SearchFilter.eq("qtype", 0));
        Integer lowThanUser = 0;//低于用户分数的人
        List<Score> totalScores = scoreDao.findTotalByProvience(orgCode);
        for (int i = 0; i < totalScores.size(); i++) {
            Score score = totalScores.get(i);
            if(Integer.parseInt(totalScore.getStandardScore()) > Integer.parseInt(score.getStandardScore()))
                lowThanUser++;

        }
        explain.append("你的认知总能力为" + totalScore.getStandardScore() + "分，位于" + this.getScoreLevelStr(totalScore.getStandardScore()) + "等级，整体认知能力较强，处于此等级的个体占到整个人群的" + String.format("%.1f", lowThanUser * 1.0 / totalScores.size() * 100) + "%。其中，你的能力强项是");
        Map<String,String> abilities = this.getStrongAndWeak(userVo, tid, orgCode);
        explain.append(abilities.get("excellent") + "，能力弱项为");
        explain.append(abilities.get("weak") + "。");
        ability.put("explain",explain.toString());
        return ability;
    }

    /**
     * 获取能力强项和弱项
     * @param
     * @return
     */
    private Map<String,String> getStrongAndWeak(UserVo userVo,Integer tid,Integer orgCode){
        Map<String,String> ability = new HashedMap();
        List<ScoreVo> scores = scoreDao.findScoreVoByUidAndTidAndOrgCode(userVo.getUid(),tid,orgCode);
        List<ScoreVo> excellent = new ArrayList<>();//能力强项
        excellent.add(scores.get(0));
        List<ScoreVo> weak =  new ArrayList<>();//能力弱项
        weak.add(scores.get(scores.size()-1));
        for (int i = 1; i < scores.size() - 1; i++) {
            if (scores.get(i).getQtype() != SystemConstant.QUESTION_TYPE_PERSONALITY &&
                    scores.get(i).getQtype() != SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE) {
                if (Integer.parseInt(scores.get(0).getStandardScore()) == Integer.parseInt(scores.get(i).getStandardScore()))
                    excellent.add(scores.get(i));
                if (Integer.parseInt(scores.get(scores.size() - 1).getStandardScore()) == Integer.parseInt(scores.get(i).getStandardScore()))
                    weak.add(scores.get(i));
            }
        }
        String excellentStr = "";
        for (ScoreVo scoreVo: excellent) {
            excellentStr +=scoreVo.getIntroduce()+"和";
        }
        String weakStr = "";
        for (ScoreVo scoreVo: weak) {
            weakStr += scoreVo.getIntroduce()+"和";
        }
        ability.put("excellent", excellentStr.substring(0, excellentStr.length() - 1));
        ability.put("weak", weakStr.substring(0, weakStr.length() - 1));
        return ability;
    }

    /**
     * 获取Json数据
     * @param
     * @return
     */
    private void getDescriptionData(){
        StringBuilder descriptionData = new StringBuilder();
        String encoding = "gbk";
        File file = new File("./public/json/personalReport.json");
        try{
            BufferedReader filecontent=new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
            String line = null;
            while ((line = filecontent.readLine()) != null) {
                descriptionData.append(line);
            }
            JsonNode descripData =  Json.parse(descriptionData.toString());
            filecontent.close();
            this.descriptionData = descripData;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取第二章的数据
     */
    public  Map<String,Object>  getSecondChapterData(UserVo userVo,Integer tid,Integer orgCode){
        Map<String,Object> sectionsTwo = new HashMap();

        //第二章的认知能力描述
        Map<String,Object> ability = this.getAbilityData(userVo, tid, orgCode);



        //第二章的各种排名
        List<Map<String,Object>> sort = new ArrayList();// 第二章存放各种排名的容器一个Map对象为前端第二章的一页显示数据
        //
        for (int j = 0; j < 1 ; j++) {
            List<Map<String,String>>  sortDatas = this.getSortData(userVo,orgCode);
            Map<String,Object>  section = new HashMap<>();
            section.put("index",j+4);
            section.put("name","排名");
            section.put("data", sortDatas);
            sort.add(section);
        }
        sectionsTwo.put("sort", sort);

        //第二章的智力年龄
        Map<String,Object> intelligenceAge = this.getIntelligenceAgeData(userVo, orgCode);

        //第二章的发展均衡性
        Map<String,Object> developmentEquilibrium  =  this.getDevelopmentEquilibrium(userVo, tid, orgCode);

        sectionsTwo.put("intelligenceAge",intelligenceAge);
        sectionsTwo.put("developmentEquilibrium",developmentEquilibrium);
        sectionsTwo.put("ability",ability);
        return  sectionsTwo;
    }

    /**
     * 获取第三章的数据
     * @param userVo
     * @param tid
     * @param orgCode
     * @return
     */
    public Map<String,Object> getThirdChapterData(UserVo userVo,Integer tid,Integer orgCode){
        Map<String,Object> sectionsThree = new HashMap<>();
        List<Score> userScores = scoreDao.findByUidAndTidAndOrgCode(userVo.getUid(), tid, orgCode);

        List<Map<String,Object>> intelligence = this.getIntelligenceData(userVo, userScores, orgCode);//智力
        Map<String,Object> nonIntelligence = this.getNonIntelligenceData(userScores, intelligence.size());//非智力

        sectionsThree.put("intelligence",intelligence);
        sectionsThree.put("nonIntelligence",nonIntelligence);
        return sectionsThree;
    }

    /**
     * 获取第四章的数据
     */
//    public Map<String,Object> getFourChapterData(UserVo userVo,Integer tid,Integer orgCode){
//        List<Map<String,Object>> environments = new ArrayList<>();
//        Map<String,Object> sectionsFour = new HashMap<>();
//        List<Score> userScores = scoreDao.findByUidAndTidAndOrgCode(userVo.getUid(),tid,orgCode);
//        //班级环境
//        Map<String,Object> classEnvironment = new HashMap<>();
//        Map<String,String> classEnvironmentData = this.getClassEnvironmentData(userVo.getUid(),userVo.getClass_(),userVo.getGrade(),orgCode);
//        classEnvironment.put("index","2");
//        classEnvironment.put("name","班级环境");
//        classEnvironment.put("intro","所在班级中学生成绩等级分布为：");
//        classEnvironment.put("explain",classEnvironmentData.get("explain"));
//        classEnvironment.put("classAvageScore",classEnvironmentData.get("classAvageScoreStr"));
//        classEnvironment.put("schoolAvageScore",classEnvironmentData.get("gradeAvageScoreStr"));
//        classEnvironment.put("questiontypeNameStr",classEnvironmentData.get("questiontypeNameStr"));
//        classEnvironment.put("legend","班级均值,年级均值");
//        classEnvironment.put("userPercentage",Float.parseFloat(classEnvironmentData.get("userPercentage").substring(0,classEnvironmentData.get("userPercentage").length()-1))*800/100-42);
//        List<Map<String,String>> percentage =  new ArrayList<>();
//        Map<String,String> percentage11 = new HashMap<>();
//        float qualifiedPercentage = Float.parseFloat(classEnvironmentData.get("qualifiedPercentage").substring(0,classEnvironmentData.get("qualifiedPercentage").length()-1));
//        if(qualifiedPercentage!=0) {
//            percentage11.put("level", "合格 ");
//            percentage11.put("percentage", classEnvironmentData.get("qualifiedPercentage"));
//            percentage11.put("color", "#FC5E43");
//            percentage11.put("width", Double.toString(qualifiedPercentage / 100 * 800));
//            percentage.add(percentage11);
//        }
//        Map<String,String> percentage12 = new HashMap<>();
//        float middlingPercentage = Float.parseFloat(classEnvironmentData.get("middlingPercentage").substring(0,classEnvironmentData.get("middlingPercentage").length()-1));
//        if(middlingPercentage!=0) {
//            percentage12.put("level", "中等");
//            percentage12.put("percentage", classEnvironmentData.get("middlingPercentage"));
//            percentage12.put("color", "#FEBB3E");
//            percentage12.put("width", Double.toString(middlingPercentage / 100 * 800));
//            percentage.add(percentage12);
//        }
//        Map<String,String> percentage13 = new HashMap<>();
//        float excellentPercentage = Float.parseFloat(classEnvironmentData.get("excellentPercentage").substring(0,classEnvironmentData.get("excellentPercentage").length()-1));
//        if(excellentPercentage!=0) {
//            percentage13.put("level", "优秀");
//            percentage13.put("percentage", classEnvironmentData.get("excellentPercentage"));
//            percentage13.put("color", "#82BA59");
//            percentage13.put("width", Double.toString(excellentPercentage / 100 * 800));
//            percentage.add(percentage13);
//        }
//        classEnvironment.put("percentage",percentage);
//        environments.add(classEnvironment);
//
//        //学校环境
//        Map<String,String> schoolEnvironmentData = this.getSchoolEnvironmentData(userVo.getUid(),orgCode);
//        Map<String,Object> schoolEnvironment = new HashMap<>();
//        schoolEnvironment.put("index","3");
//        schoolEnvironment.put("name","学校环境");
//        schoolEnvironment.put("intro","所在学校中学生成绩等级分布为：");
//        schoolEnvironment.put("explain",schoolEnvironmentData.get("explain"));
//        schoolEnvironment.put("classAvageScore",schoolEnvironmentData.get("schoolAvageScoreStr"));
//        schoolEnvironment.put("schoolAvageScore",schoolEnvironmentData.get("townAvageScoreStr"));
//        schoolEnvironment.put("questiontypeNameStr",schoolEnvironmentData.get("questiontypeNameStr"));
//        schoolEnvironment.put("legend","学校均值,区县均值");
//        schoolEnvironment.put("userPercentage",Float.parseFloat(schoolEnvironmentData.get("userPercentage").substring(0,schoolEnvironmentData.get("userPercentage").length()-1))*800/100-42);
//        List<Map<String,String>> percentage1 =  new ArrayList<>();
//        Map<String,String> percentage111 = new HashMap<>();
//        qualifiedPercentage = Float.parseFloat(schoolEnvironmentData.get("qualifiedPercentage").substring(0,schoolEnvironmentData.get("qualifiedPercentage").length()-1));
//        if(qualifiedPercentage!=0) {
//            percentage111.put("level", "合格 ");
//            percentage111.put("percentage", schoolEnvironmentData.get("qualifiedPercentage"));
//            percentage111.put("color", "#ED7D31");
//            percentage111.put("width", Double.toString(qualifiedPercentage/ 100 * 800));
//            percentage1.add(percentage11);
//        }
//        Map<String,String> percentage122 = new HashMap<>();
//        middlingPercentage = Float.parseFloat(schoolEnvironmentData.get("middlingPercentage").substring(0,schoolEnvironmentData.get("middlingPercentage").length()-1));
//        if(middlingPercentage!=0) {
//            percentage122.put("level", "中等");
//            percentage122.put("percentage", schoolEnvironmentData.get("middlingPercentage"));
//            percentage122.put("color", "#FFC000");
//            percentage122.put("width", Double.toString(middlingPercentage / 100 * 800));
//            percentage1.add(percentage12);
//        }
//        Map<String,String> percentage133 = new HashMap<>();
//        excellentPercentage = Float.parseFloat(schoolEnvironmentData.get("excellentPercentage").substring(0,schoolEnvironmentData.get("excellentPercentage").length()-1));
//        if(excellentPercentage!=0) {
//            percentage133.put("level", "优秀");
//            percentage133.put("percentage", schoolEnvironmentData.get("excellentPercentage"));
//            percentage133.put("color", "#70AD47");
//            percentage133.put("width", Double.toString(excellentPercentage / 100 * 800));
//            percentage1.add(percentage13);
//        }
//        schoolEnvironment.put("percentage", percentage1);
//        environments.add(schoolEnvironment);
//
//        Map<String,String> familyEnvironment = this.getFamilyEnvironmentData(userScores);
//        sectionsFour.put("environments", environments);
//        sectionsFour.put("familyEnvironment",familyEnvironment);
//        return sectionsFour;
//
//    }
    public Map<String,Object> getFourChapterData(UserVo userVo,Integer tid,Integer orgCode){
        Map<String,Object> sectionsFour = new HashMap<>();
        List<Score> userScores = scoreDao.findByUidAndTidAndOrgCode(userVo.getUid(),tid,orgCode);

        Map<String,String> familyEnvironment = this.getFamilyEnvironmentData(userScores, orgCode);
        sectionsFour.put("familyEnvironment",familyEnvironment);
        return sectionsFour;

    }



    /**
     * 计算班级基尼平局分
     * @param orgCode
     * @param grade
     * @param class_
     * @return
     */
    private double caculateAvarageClassGeordie(Integer orgCode,String grade,String class_){
        List<User> users =  userDao.findUserByOrgCodeAndGradeAndClass(orgCode,grade,class_);
        double totalGeordie = 0;
        for (int i = 0; i < users.size(); i++) {
            User user =  users.get(i);
            double userGeordie = this.caculateGeordie(user.getId());
            totalGeordie += userGeordie;
        }

        return totalGeordie/users.size();
    }
    /**
     * 计算用户的基尼系数
     * @param uid
     * @return
     */
    private double caculateGeordie(Integer uid){
        List<Score> userScores = scoreDao.findByUidOrderByStandardAsc(uid);
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < userScores.size(); i++) {
            Score score = userScores.get(i);
            if(score.getQtype() != SystemConstant.QUESTION_TYPE_PERSONALITY && score.getQtype() !=SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE && score.getQtype() !=0){
                list.add(Double.parseDouble(score.getStandardScore()));
            }
        }
        List<Double> doubles = new ArrayList<>();
        List<Double> totalPercentage = new ArrayList<>();
        double sum = 0; //所有分数的总分
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        //求每个分数与总分所占的比例
        for (int i = 0; i < list.size(); i++) {
            if(i == list.size()-1)
                break;
            doubles.add((list.get(i) * 1.0 / sum));
        }
        //累加每个前i个值
        for (int i = 0; i < doubles.size(); i++) {
            Double percentage = 0.0;
            for (int j = 0; j <= i; j++) {
                percentage += doubles.get(j);
            }
            totalPercentage.add(percentage);
        }
        //将累加出来的值求和
        double total = 0 ;
        for (int i = 0; i < totalPercentage.size(); i++) {
            total += totalPercentage.get(i);
        }
        int n = list.size();
        double G = 1 -2 * 1.0/n *total - 1.0/n;
        return Math.abs(G);
    }

    /**
     * 发展均衡性的数据
     * @param userVo
     * @param orgCode
     * @return
     */
    private Map<String,Object> getDevelopmentEquilibrium(UserVo userVo,Integer tid, Integer orgCode){
        Map<String,Object> developmentEquilibrium = new HashMap<>();
        double userGeordie = this.caculateGeordie(userVo.getUid());//生成报告用户的基尼系数
        DecimalFormat    df   = new DecimalFormat("######0.00");
        developmentEquilibrium.put("name", "能力发展均衡性");
        String explain;
        Map<String,String> abilities = this.getStrongAndWeak(userVo, tid, orgCode);


        if(1 - userGeordie < 0.6){
            explain = this.descriptionData.findPath("firstSection").findPath("equilibriumCondition")
                    .findPath("low").asText().replaceFirst("@@@@", df.format(1-userGeordie)).replace("\"", "");
            explain = explain.replaceFirst("@@@@", abilities.get("excellent"))
                    .replaceFirst("@@@@", abilities.get("weak"));
        }else if (1 - userGeordie < 0.7){
            explain = this.descriptionData.findPath("firstSection").findPath("equilibriumCondition")
                    .findPath("pass").asText().replaceFirst("@@@@", df.format(1-userGeordie)).replace("\"", "");
        }else if(1 - userGeordie < 0.8){
            explain = this.descriptionData.findPath("firstSection").findPath("equilibriumCondition")
                    .findPath("middle").asText().replaceFirst("@@@@", df.format(1-userGeordie)).replace("\"", "");
            explain = explain.replaceFirst("@@@@", abilities.get("weak"));
        }else if(1 - userGeordie < 0.9){
            explain = this.descriptionData.findPath("firstSection").findPath("equilibriumCondition")
                    .findPath("good").asText().replaceFirst("@@@@", df.format(1-userGeordie)).replace("\"", "");
            explain = explain.replaceFirst("@@@@", abilities.get("weak"));
        }else{
            explain = this.descriptionData.findPath("firstSection").findPath("equilibriumCondition")
                    .findPath("excellent").asText().replaceFirst("@@@@", df.format(1-userGeordie)).replace("\"", "");
        }
        developmentEquilibrium.put("explain", explain);

        return developmentEquilibrium;
    }


    /**
     * 第二章用户的排名数据
     * @param userVo
     * @param orgCode
     * @return
     */
    private List<Map<String,String>> getSortData(UserVo userVo,Integer orgCode){
        List<Map<String,String>>  sortDatas = new ArrayList<>();
        Score userScore = scoreDao.findOne(SearchFilter.eq("uid", userVo.getUid()), SearchFilter.eq("qtype", 0));
        //班级排名
        Map<String,String>  classSort = this.getClassSort(userScore.getStandardScore(), userVo, orgCode);
        //年级排名
        Map<String,String>  gradeSort = this.getGradeSort(userScore.getStandardScore(), userVo, orgCode);
        //按照年级的性别排名
        Map<String,String>  genderSort = this.getGenderGradeSort(userScore.getStandardScore(), userVo, orgCode);
        //按照区县排名
        Map<String,String>  townSort = this.getTownSort(userScore.getStandardScore(), userVo, orgCode);

        sortDatas.add(classSort);
        sortDatas.add(gradeSort);
        sortDatas.add(genderSort);
        sortDatas.add(townSort);
        return sortDatas;
    }

    /**
     * 计算用户的正确率
     * @param uid
     * @param testpaperVos
     * @return
     */
    public String caculateUserAgeData(Integer uid, List<TestpaperVo> testpaperVos ){
        List<Score> userScores  =  scoreDao.findByUidOrderByStandardAsc(uid);
        float totalAccuracy = 0;
        Integer questionTypeNum = 0;


        for (int i = 0; i < userScores.size(); i++) {
            Score score = userScores.get(i);
            if(score.getQtype() != SystemConstant.QUESTION_TYPE_PERSONALITY && score.getQtype() !=SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE && score.getQtype() !=0){
                float accuracy = 0;
                for (int j = 0; j < testpaperVos.size(); j++) {
                    TestpaperVo testpaperVo = testpaperVos.get(j);
                    if(score.getQtype() == testpaperVo.getQtype() && score.getQtype() != SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY){
                        accuracy = (float)(Integer.parseInt(score.getOriginalScore()) * 1.0/testpaperVo.getQtypeQuestionNumber());
                        totalAccuracy +=accuracy;
                        System.out.println(score.getQtype()+":::"+Integer.parseInt(score.getOriginalScore()) + "/" + testpaperVo.getQtypeQuestionNumber() +"=" + accuracy );
                    }
                    //顺序记忆的正确率
                    if(score.getQtype() == testpaperVo.getQtype() && score.getQtype() == SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY){
                        accuracy = (float)(Integer.parseInt(score.getOriginalScore()) * 1.0/15);
                        totalAccuracy +=accuracy;
                        System.out.println(score.getQtype()+":::"+Integer.parseInt(score.getOriginalScore()) + "/" + testpaperVo.getQtypeQuestionNumber() +"=" + accuracy );
                    }
                }
                questionTypeNum++;
            }
        }
        return String.format("%.2f", totalAccuracy/questionTypeNum);
    }

    /**
     * 获取年级的智力年龄数据
     * @param userAgaData
     * @param userVo
     * @param orgCode
     * @param testpaperVos
     * @return
     */
    private Map<String,Object> getGradeIntelligenceAgeData(String userAgaData,UserVo userVo,Integer orgCode,List<TestpaperVo> testpaperVos){

        List<Map<String,String>> accuracyPercentage =  new ArrayList<>();
        String ageData = "0,0.14,0.2,0.31,0.45,0.77,0.78,0.87,0.95,1";
        String ageCategory = "8岁以下,8岁,9岁,10岁,11岁,12岁,13岁,14岁,15岁,15岁以上";
        String colors = "#DEF3FE,#B3E5FD,#81D4FB,#50C4F7,#2AB7F7,#03AAF5,#0294D5,#0087C3,#0170A2,#016996";
        String[] ageDataArray = ageData.split(",");
        String[] ageCategoryArray = ageCategory.split(",");
        String[] colorsArray = colors.split(",");

        List<User> gradeUser =  userDao.findUserByOrgCodeAndGrade(orgCode, userVo.getGrade());
        Integer lowThanNum =0; //低于用户正确率的人数
        Integer equalNum = 0;//与用户相等正确率的人数
        Integer overNum = 0 ;// 超出用户正确率的人数
        for (int j = 0; j < gradeUser.size() ; j++) {
            User user = gradeUser.get(j);
            String userAccuracy = this.caculateUserAgeData(user.getId(),testpaperVos);
            if(Float.parseFloat(userAccuracy) < Float.parseFloat(userAgaData)) {
                lowThanNum++;
            }else if(Float.parseFloat(userAccuracy) == Float.parseFloat(userAgaData)) {
                equalNum++;
            }else{
                overNum++;
            }
        }
        Integer sameAgeNum = 0;//相同智力年龄的人
        Integer sameAndMoreNum = 0 ;//相同智力年龄段的人 但是智力年龄大于你的人
        for (int i = 0; i < ageDataArray.length-1; i++) {
            Map<String,String> map = new HashMap<>();
            String age = ageCategoryArray[i];
            Integer lowNum = 0;
            for (int j = 0; j < gradeUser.size() ; j++) {
                User user = gradeUser.get(j);
                String userAccuracy = this.caculateUserAgeData(user.getId(),testpaperVos);
                if(Float.parseFloat(userAccuracy) < Float.parseFloat(ageDataArray[i+1]) && Float.parseFloat(userAccuracy) >= Float.parseFloat(ageDataArray[i])) {
                    lowNum++;
                }
                if(Float.parseFloat(userAccuracy) < Float.parseFloat(ageDataArray[i+1]) && Float.parseFloat(userAccuracy) >= Float.parseFloat(ageDataArray[i]) &&  Float.parseFloat(userAgaData) < Float.parseFloat(ageDataArray[i+1]) &&  Float.parseFloat(userAgaData) >= Float.parseFloat(ageDataArray[i])) {
                    sameAgeNum++;
                    if(Float.parseFloat(userAccuracy) > Float.parseFloat(userAgaData))
                        sameAndMoreNum++;
                }
            }
            map.put("age",age);
            map.put("percentage",String.format("%.1f", lowNum * 1.0 / gradeUser.size() * 100) + "%");
            map.put("color",colorsArray[i]);
            if(lowNum * 1.0 / gradeUser.size() * 100 < 3 && lowNum * 1.0 / gradeUser.size() * 100 > 0)
                map.put("height", "20");
            else
                map.put("height", Double.toString( lowNum * 1.0 / gradeUser.size() * 100 / 100 * 400));
            if(lowNum!=0) {
                accuracyPercentage.add(map);
            }
        }
        Map<String,Object> percentage  = new HashMap<>();
        percentage.put("accuracyPercentage",accuracyPercentage);
        percentage.put("percentageIntro","年级中和你相同智力年龄的学生所占的比例为"+String.format("%.1f", sameAgeNum * 1.0 / gradeUser.size() * 100) +"%，比你智力年龄高的学生共有"+ String.format("%.1f",(overNum-sameAndMoreNum) * 1.0 / gradeUser.size() * 100)+"%。");
        percentage.put("userPosition",((lowThanNum+equalNum) * 1.0 / gradeUser.size() * 100) /100 * 800-42);
        percentage.put("userColor", "#5B9BD5");

        percentage.put("width", Double.toString(750 * 1.0/accuracyPercentage.size()));


        return percentage;
    }


    /**
     * 获取智力年龄的数据
     * @param userVo
     * @param orgCode
     * @return
     */
    private  Map<String,Object>  getIntelligenceAgeData(UserVo userVo,Integer orgCode){
        Map<String,Object> intelligenceAge = new HashMap<>();
        TestVo test = testDao.findTestByOrgCode(orgCode).get(0);
        List<TestpaperVo> testpaperVos = testpaperDao.findTestpaperPerQuestiontypeQuestionNum(test.getPid());
        StringBuilder introduce = new StringBuilder("你的测试正确率为");
        String ageData = "0.14,0.2,0.31,0.45,0.77,0.78,0.87,0.95";
        String ageCategory = "8岁,9岁,10岁,11岁,12岁,13岁,14岁,15岁";

        //计算用户智力年龄
        String userAgeData = this.caculateUserAgeData(userVo.getUid(),testpaperVos);
        introduce.append(userAgeData+"，");
        //判断智力年龄所处阶段
        String[] ageDataArray = ageData.split(",");
        String[] ageCategoryArray = ageCategory.split(",");
        for (int i = 0; i <ageDataArray.length; i++) {
            Integer age = Integer.parseInt(ageCategoryArray[i].substring(0, ageCategoryArray[i].length() - 1));
            if(Float.parseFloat(userAgeData) <= Float.parseFloat(ageDataArray[i])){
                if(i==0){
                    introduce.append("你的智力年龄在8岁以下,你的实际年龄为" + userVo.getAge()+"岁，相当于你的实际年龄低于智力年龄"+ (userVo.getAge()-age) +"岁多。");
                }else{
                    introduce.append("介于"+ageCategoryArray[i-1] + "和" + ageCategoryArray[i] + "之间，你的智力年龄为"+ ageCategoryArray[i-1]);
                    float ageUserDiffer =  Float.parseFloat(userAgeData) -  Float.parseFloat(ageDataArray[i-1]);
                    float ageDiffer = Float.parseFloat(ageDataArray[i]) - Float.parseFloat(ageDataArray[i-1]);
                    int month = (int)(ageUserDiffer / ageDiffer * 12);
                    if(month>10){
                        introduce.append(month+"个月，");
                    }else{
                        introduce.append("零"+month+"个月，");
                    }
                    if(userVo.getAge()-age > 0)
                        introduce.append("相当于你的智力年龄低于实际年龄"+ (userVo.getAge()-age)+"岁。");
                    else
                        introduce.append("相当于你的智力年龄低于实际年龄"+ (age-userVo.getAge())+"岁。");
                }
                break;
            }else if(i==ageDataArray.length-1){
                if(userVo.getAge()-age > 0)
                    introduce.append("你的智力年龄在15岁以上，你的实际年龄为" + userVo.getAge()+"岁，相当于你的智力年龄低于实际年龄"+ (userVo.getAge()-age) +"岁多。");
                else
                    introduce.append("你的智力年龄在15岁以上，你的实际年龄为" + userVo.getAge()+"岁，相当于你的智力年龄超过实际年龄"+ (age-userVo.getAge()) +"岁多。");
            }
        }
        intelligenceAge.put("index","3");
        intelligenceAge.put("name","智力年龄");
        intelligenceAge.put("ageData",userAgeData + ","+ageData);
        intelligenceAge.put("category","你,"+ageCategory);
        intelligenceAge.put("introduce",introduce.toString());



        Map<String,Object> percentage  = this.getGradeIntelligenceAgeData(userAgeData,userVo,orgCode,testpaperVos);
        List<Map<String,String>> gradePercentage = (List<Map<String,String>>)percentage.get("accuracyPercentage");
        intelligenceAge.put("percentageIntro",percentage.get("percentageIntro"));
        intelligenceAge.put("userPosition",percentage.get("userPosition"));
        intelligenceAge.put("userColor", "#5B9BD5");
        intelligenceAge.put("width", percentage.get("width"));

        intelligenceAge.put("percentage",gradePercentage);

        return intelligenceAge;
    }
    /**
     * 用户所属性别的总分的年级排名
     * @param userScore
     * @param userVo
     * @param orgCode
     * @return
     */
    private  Map<String,String> getGenderGradeSort(String userScore,UserVo userVo,Integer orgCode){
        Map<String,String>  sortData = new HashMap<>();
        List<Score> gradeScores = new ArrayList<>();
        if(userVo.getSex().equals("男")) {
            gradeScores = scoreDao.findByGradeAndGender(userVo.getGrade(), 0, orgCode,"男");
        }else{
            gradeScores = scoreDao.findByGradeAndGender(userVo.getGrade(), 0, orgCode,"女");
        }
        Integer exceedUserNum = 0;
        for (int i = 0; i < gradeScores.size(); i++) {
            Score totalScore = gradeScores.get(i);
            if(Integer.parseInt(totalScore.getStandardScore()) > Integer.parseInt(userScore))
                exceedUserNum++;
        }
        String percentage =  String.format("%.1f", 100-exceedUserNum * 1.0 / gradeScores.size() * 100) + "%";

        sortData.put("name", "全年级所有"+userVo.getSex()+"生");
        sortData.put("percentage", percentage);
        sortData.put("evaluate", "你超过你所在区县" + percentage + "的" + userVo.getSex() + "生，总体成绩超过平均水平。");
        return sortData;
    }

    /**
     * 用户总分的区域排名
     * @param userScore
     * @param userVo
     * @param orgCode
     * @return
     */
    private  Map<String,String> getTownSort(String userScore,UserVo userVo,Integer orgCode){
        AdmissionsOrg admissionsOrg = admissionsOrgDao.findOne(orgCode);
        Map<String,String>  sortData = new HashMap<>();
        List<Score> gradeScores = scoreDao.findByTown(admissionsOrg.getTown(), 0);
        Integer exceedUserNum = 0;
        for (int i = 0; i < gradeScores.size(); i++) {
            Score totalScore = gradeScores.get(i);
            if(Integer.parseInt(totalScore.getStandardScore()) > Integer.parseInt(userScore))
                exceedUserNum++;
        }
        String percentage =  String.format("%.1f", 100-exceedUserNum * 1.0 / gradeScores.size() * 100) + "%";

        sortData.put("name", "全区县所有学生");
        sortData.put("percentage", percentage);
        sortData.put("evaluate", "你超过你所在区县" + percentage + "的学生，总体成绩超过平均水平。");
        return sortData;
    }

    /**
     * 用户总分的年级排名
     * @param userScore
     * @param userVo
     * @param orgCode
     * @return
     */
    private  Map<String,String> getGradeSort(String userScore,UserVo userVo,Integer orgCode){
        Map<String,String>  sortData = new HashMap<>();
        List<Score> gradeScores = scoreDao.findByGradeHaveTotalScore(userVo.getGrade(), orgCode);
        Integer exceedUserNum = 0;
        for (int i = 0; i < gradeScores.size(); i++) {
            Score totalScore = gradeScores.get(i);
            if(Integer.parseInt(totalScore.getStandardScore()) > Integer.parseInt(userScore))
                exceedUserNum++;
        }
        String percentage =  String.format("%.1f", 100-exceedUserNum * 1.0 / gradeScores.size() * 100) + "%";

        sortData.put("name", "全年级所有学生");
        sortData.put("percentage", percentage);
        sortData.put("evaluate","你超过你所在年级"+ percentage +"的学生，总体成绩超过平均水平。");
        return sortData;
    }

    /**
     * 用户总分的班级排名
     * @param userScore
     * @param userVo
     * @param orgCode
     * @return
     */
    private  Map<String,String> getClassSort(String userScore,UserVo userVo,Integer orgCode){
        Map<String,String>  sortData = new HashMap<>();
        List<Score> classScores = scoreDao.findByGradeAndClassHaveTotalScore(userVo.getGrade(), userVo.getClass_(), orgCode);
        Integer exceedUserNum = 0;
        for (int i = 0; i < classScores.size(); i++) {
            Score totalScore = classScores.get(i);
            if(Integer.parseInt(totalScore.getStandardScore()) > Integer.parseInt(userScore))
                exceedUserNum++;
        }
        String percentage =  String.format("%.1f", 100-exceedUserNum * 1.0 / classScores.size() * 100) + "%";

        sortData.put("name", "全班级所有学生");
        sortData.put("percentage", percentage);
        sortData.put("evaluate","你超过你所在班级"+ percentage +"的学生，总体成绩超过平均水平。");
        return sortData;
    }



    /**
     * 第四章家庭环境数据
     * @param userScores
     * @return
     */
    private Map<String,String> getFamilyEnvironmentData(List<Score> userScores, Integer orgCode){
        Map<String,String> familyEnvironment = new HashMap<>();
        List<Score> familyScores = new ArrayList<>();

        List<Questiontype>  questiontypes = questionTypeDao.findByOrgCode(orgCode);
        Integer index = questiontypes.size() - 2;
        familyEnvironment.put("index", index.toString());
        familyEnvironment.put("name", "家庭环境");
        familyEnvironment.put("explain", "");

        for (Score score: userScores) {
            if(score.getQtype().equals(SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE))
                familyScores.add(score);
        }

        Integer fatherCareScore = Integer.parseInt(familyScores.get(0).getStandardScore());
        Integer fatherOverProtectionScore = Integer.parseInt(familyScores.get(2).getStandardScore());
        Integer motherCareScore = Integer.parseInt(familyScores.get(1).getStandardScore());
        Integer motherOverProtectionScore = Integer.parseInt(familyScores.get(3).getStandardScore());

        Map<String,String> fatherData = this.getFamilyExplain("father", fatherCareScore, fatherOverProtectionScore);
        Map<String,String> motherData = this.getFamilyExplain("mother", motherCareScore, motherOverProtectionScore);

        familyEnvironment.put("fathertop",fatherData.get("top"));
        familyEnvironment.put("mothertop",motherData.get("top"));
        familyEnvironment.put("fatherleft",fatherData.get("left"));
        familyEnvironment.put("motherleft",motherData.get("left"));

        String fatherParenting = getParentingStyle(fatherCareScore / 24, fatherOverProtectionScore / 14);
        String motherParenting = getParentingStyle(motherCareScore / 27, motherOverProtectionScore / 7);
        if(fatherParenting.equals(motherParenting)) {
            familyEnvironment.put("explain", "父母的教养方式为" + this.flects(motherParenting) + "。;" +
                    this.descriptionData.findPath("PBI").findPath(fatherParenting).asText().replace("\"", ""));
        }else {
            familyEnvironment.put("explain", "父亲的教养方式为" + this.flects(fatherParenting) + "。;" +
                    this.descriptionData.findPath("PBI").findPath(fatherParenting).asText().replace("\"","") + ";" +
                    "母亲的教养方式为" + this.flects(motherParenting) + "。;" + this.descriptionData.findPath("PBI").
                    findPath(motherParenting).asText().replace("\"", ""));
        }
        return  familyEnvironment;
    }

    /**
     * 获取父母教养方式
     * @param
     * @return
     */
    private String getParentingStyle(Integer x,Integer y){
        if(x >= 1){
            x = 1;
        }
        if(y >= 1){
            y = 1;
        }
        String[][] parentingStyle = {{"authoritative", "autocratic"}, {"democratic", "indulgent"}};
        return parentingStyle[x][y];
    }

    /**
     * 获取学校环境数据
     * @param orgCode
     * @return
     */
    private Map<String,String> getSchoolEnvironmentData(Integer uid,Integer orgCode){
        Score totalScore = scoreDao.findOne(SearchFilter.eq("uid", uid), SearchFilter.eq("qtype", 0));
        Map<String,String> environmentData = new HashMap<>();

        StringBuilder totalScoreexplain =  new StringBuilder("与全区县成绩比较,个体所在的学校在总分上"); //总分解释
        StringBuilder overAvarageScoreexplain = new StringBuilder();
        StringBuilder equalAvarageScoreexplain = new StringBuilder();
        StringBuilder lowAvarageScoreexplain = new StringBuilder();


        List<Questiontype> questiontypes = questionTypeDao.findByUseStatus();
        AdmissionsOrg admissionsOrg = admissionsOrgDao.findOne(orgCode);
        StringBuilder schoolAvageScoreStr  = new StringBuilder();
        StringBuilder townAvageScoreStr  = new StringBuilder();
        StringBuilder questiontypeNameStr = new StringBuilder();
        for (int i = 0; i < questiontypes.size(); i++) {
            Questiontype questiontype = questiontypes.get(i);
            Integer schoolSumScore = 0;
            Integer townSumScore = 0;
            if(questiontype.getId() != SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE && questiontype.getId() != SystemConstant.QUESTION_TYPE_PERSONALITY){
                List<ScoreVo> schoolScoreVos = scoreDao.findScoreVoByOrgCode(questiontype.getId(), orgCode);
                List<ScoreVo> townScoreVos = scoreDao.findScoreVoByTown(admissionsOrg.getTown(), questiontype.getId());
                if(schoolScoreVos.size() != 0 &&  townScoreVos.size() !=0){
                    for (int j = 0; j < schoolScoreVos.size(); j++) {
                        ScoreVo schoolScoreVo =  schoolScoreVos.get(j);
                        schoolSumScore += Integer.parseInt(schoolScoreVo.getStandardScore());
                        ScoreVo townScoreVo =  townScoreVos.get(j);
                        townSumScore += Integer.parseInt(townScoreVo.getStandardScore());
                    }
                    Integer schoolAvageScore = (int)(schoolSumScore * 1.0 /schoolScoreVos.size()+0.5);
                    Integer townAvageScore = (int)(townSumScore * 1.0 /townScoreVos.size()+0.5);

                    if(schoolAvageScore < townAvageScore)
                        lowAvarageScoreexplain.append(questiontype.getIntroduce() +"、");
                    if(schoolAvageScore == townAvageScore)
                        equalAvarageScoreexplain.append(questiontype.getIntroduce()+"、");
                    if(schoolAvageScore > townAvageScore)
                        overAvarageScoreexplain.append(questiontype.getIntroduce()+"、");

                    schoolAvageScoreStr.append(schoolAvageScore.toString()+",");
                    townAvageScoreStr.append(townAvageScore.toString()+",");
                    questiontypeNameStr.append(schoolScoreVos.get(0).getIntroduce()+",");
                }
            }
        }
        Integer schoolSumScore = 0;
        Integer townSumScore = 0;
        Integer exceedUserNum = 0;//超过该学生的数量
        Integer qualifiedNum = 0;//合格的人数
        Integer middlingNum = 0;//中等的人数
        Integer excellentNum = 0;//优秀的人数
        List<Score> schoolScoreVos = scoreDao.findByOrgCode(0, orgCode);
        List<Score> townScoreVos = scoreDao.findByTown(admissionsOrg.getTown(), 0);
        if(schoolScoreVos.size() != 0 &&  townScoreVos.size() !=0){
            for (int j = 0; j < schoolScoreVos.size(); j++) {
                Score schoolScoreVo =  schoolScoreVos.get(j);
                Integer schoolScore = Integer.parseInt(schoolScoreVo.getStandardScore());
                if(Integer.parseInt(totalScore.getStandardScore()) < schoolScore)
                    exceedUserNum++;
                if(schoolScore < 90)
                    qualifiedNum++;
                else if(schoolScore>=90 && schoolScore<=110)
                    middlingNum++;
                else
                    excellentNum++;
                schoolSumScore += schoolScore;
                Score townScoreVo =  townScoreVos.get(j);
                townSumScore += Integer.parseInt(townScoreVo.getStandardScore());
            }
            Integer schoolAvageScore = (int)(schoolSumScore * 1.0 /schoolScoreVos.size()+0.5);
            Integer townAvageScore = (int)(townSumScore * 1.0 /townScoreVos.size()+0.5);

            if(schoolAvageScore< townAvageScore)
                totalScoreexplain.append("低于");
            if(schoolAvageScore == townAvageScore)
                totalScoreexplain.append("等于");
            if(schoolAvageScore > townAvageScore)
                totalScoreexplain.append("大于");
            schoolAvageScoreStr.append(schoolAvageScore.toString());
            townAvageScoreStr.append(townAvageScore.toString());
            questiontypeNameStr.append("总分");
        }
        totalScoreexplain.append("全区县均值，其中");
        if(overAvarageScoreexplain.length() != 0) {
            overAvarageScoreexplain.deleteCharAt(overAvarageScoreexplain.length() - 1).append("高于全区县的平均成绩");
            if(equalAvarageScoreexplain.length() == 0 && lowAvarageScoreexplain.length() == 0)
                overAvarageScoreexplain.append("。");
            else
                overAvarageScoreexplain.append("，");
        }
        if(equalAvarageScoreexplain.length() != 0) {
            equalAvarageScoreexplain.deleteCharAt(equalAvarageScoreexplain.length() - 1).append("与全区县的平均成绩保持持平");
            if(overAvarageScoreexplain.length() == 0 && lowAvarageScoreexplain.length() == 0)
                equalAvarageScoreexplain.append("。");
            else
                equalAvarageScoreexplain.append("，");
        }
        if(lowAvarageScoreexplain.length() != 0) {
            lowAvarageScoreexplain.deleteCharAt(lowAvarageScoreexplain.length() - 1).append("低于全区县的平均成绩");
            if(overAvarageScoreexplain.length() == 0 && equalAvarageScoreexplain.length() == 0)
                lowAvarageScoreexplain.append("。");
            else
                lowAvarageScoreexplain.append("。");
        }

        environmentData.put("schoolAvageScoreStr",schoolAvageScoreStr.toString());
        environmentData.put("townAvageScoreStr",townAvageScoreStr.toString());
        environmentData.put("questiontypeNameStr",questiontypeNameStr.toString());
        environmentData.put("qualifiedPercentage", String.format("%.1f", qualifiedNum * 1.0 / schoolScoreVos.size() * 100) + "%");
        environmentData.put("middlingPercentage", String.format("%.1f",  middlingNum * 1.0 / schoolScoreVos.size() * 100) + "%");
        environmentData.put("excellentPercentage", String.format("%.1f", excellentNum * 1.0 / schoolScoreVos.size() * 100) + "%");
        environmentData.put("userPercentage", String.format("%.1f", 100-exceedUserNum * 1.0 / schoolScoreVos.size() * 100) + "%");
        environmentData.put("explain",totalScoreexplain.toString()+overAvarageScoreexplain.toString()+equalAvarageScoreexplain.toString()+lowAvarageScoreexplain.toString());
        return environmentData;
    }


    /**
     * 获取班级环境数据
     * @param class_
     * @param grade
     * @param orgCode
     * @return
     */
    private Map<String,String> getClassEnvironmentData(Integer uid,String class_,String grade,Integer orgCode){
        Score totalScore = scoreDao.findOne(SearchFilter.eq("uid", uid), SearchFilter.eq("qtype", 0));
        Map<String,String> environmentData = new HashMap<>();

        StringBuilder totalScoreexplain =  new StringBuilder("与年级成绩比较,个体所在的班级在总分上"); //总分解释
        StringBuilder overAvarageScoreexplain = new StringBuilder();
        StringBuilder equalAvarageScoreexplain = new StringBuilder();
        StringBuilder lowAvarageScoreexplain = new StringBuilder();

        List<Questiontype> questiontypes = questionTypeDao.findByUseStatus();
        StringBuilder classAvageScoreStr  = new StringBuilder();
        StringBuilder gradeAvageScoreStr  = new StringBuilder();
        StringBuilder questiontypeNameStr = new StringBuilder();
        for (int i = 0; i < questiontypes.size(); i++) {
            Questiontype questiontype = questiontypes.get(i);
            Integer classSumScore = 0;
            Integer gradeSumScore = 0;
            if(questiontype.getId() != SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE && questiontype.getId() != SystemConstant.QUESTION_TYPE_PERSONALITY){
                List<ScoreVo> classScoreVos = scoreDao.findScoreVoByGradeAndClass(grade, class_, questiontype.getId(), orgCode);
                List<ScoreVo> gradeScoreVos = scoreDao.findScoreVoByGrade(grade, questiontype.getId(), orgCode);
                if(classScoreVos.size() != 0 &&  gradeScoreVos.size() !=0){
                    for (int j = 0; j < classScoreVos.size(); j++) {
                        ScoreVo classScoreVo =  classScoreVos.get(j);
                        classSumScore += Integer.parseInt(classScoreVo.getStandardScore());
                    }
                    for (int j = 0; j < gradeScoreVos.size(); j++) {
                        ScoreVo gradeScoreVo =  gradeScoreVos.get(j);
                        gradeSumScore += Integer.parseInt(gradeScoreVo.getStandardScore());
                    }
                    Integer classAvageScore = (int)(classSumScore * 1.0 /classScoreVos.size()+0.5);
                    Integer gradeAvageScore = (int)(gradeSumScore * 1.0 /gradeScoreVos.size()+0.5);
                    if(classAvageScore < gradeAvageScore)
                        lowAvarageScoreexplain.append(questiontype.getIntroduce() +"、");
                    if(classAvageScore == gradeAvageScore)
                        equalAvarageScoreexplain.append(questiontype.getIntroduce()+"、");
                    if(classAvageScore > gradeAvageScore)
                        overAvarageScoreexplain.append(questiontype.getIntroduce()+"、");
                    classAvageScoreStr.append(classAvageScore.toString()+",");
                    gradeAvageScoreStr.append(gradeAvageScore.toString()+",");
                    questiontypeNameStr.append(classScoreVos.get(0).getIntroduce()+",");
                }
            }
        }
        Integer classSumScore = 0;
        Integer gradeSumScore = 0;
        Integer exceedUserNum = 0;//超过该学生的数量
        Integer qualifiedNum = 0;//合格的人数
        Integer middlingNum = 0;//中等的人数
        Integer excellentNum = 0;//优秀的人数
        List<Score> classScoreVos = scoreDao.findByGradeAndClass(grade, class_, 0, orgCode);
        List<Score> gradeScoreVos = scoreDao.findByGrade(grade, 0, orgCode);
        if(classScoreVos.size() != 0 &&  gradeScoreVos.size() !=0){
            for (int j = 0; j < classScoreVos.size(); j++) {
                Score classScoreVo =  classScoreVos.get(j);
                Integer classScore = Integer.parseInt(classScoreVo.getStandardScore());
                if(Integer.parseInt(totalScore.getStandardScore()) < classScore)
                    exceedUserNum++;
                if(classScore < 90)
                    qualifiedNum++;
                else if(classScore>=90 && classScore<=110)
                    middlingNum++;
                else
                    excellentNum++;
                classSumScore += classScore;
            }
            for (int i = 0; i <gradeScoreVos.size() ; i++) {
                Score gradeScoreVo =  gradeScoreVos.get(i);
                gradeSumScore += Integer.parseInt(gradeScoreVo.getStandardScore());
            }
            Integer classAvageScore = (int)(classSumScore * 1.0 /classScoreVos.size()+0.5);
            Integer gradeAvageScore = (int)(gradeSumScore * 1.0 /gradeScoreVos.size()+0.5);

            if(classAvageScore< gradeAvageScore)
                totalScoreexplain.append("低于");
            if(classAvageScore == gradeAvageScore)
                totalScoreexplain.append("等于");
            if(classAvageScore > gradeAvageScore)
                totalScoreexplain.append("大于");

            classAvageScoreStr.append(classAvageScore.toString());
            gradeAvageScoreStr.append(gradeAvageScore.toString());
            questiontypeNameStr.append("总分");
        }


        totalScoreexplain.append("年纪均值，其中");
        if(overAvarageScoreexplain.length() != 0) {
            overAvarageScoreexplain.deleteCharAt(overAvarageScoreexplain.length() - 1).append("高于年级的平均成绩");
            if(equalAvarageScoreexplain.length() == 0 && lowAvarageScoreexplain.length() == 0)
                overAvarageScoreexplain.append("。");
            else
                overAvarageScoreexplain.append("，");
        }
        if(equalAvarageScoreexplain.length() != 0) {
            equalAvarageScoreexplain.deleteCharAt(equalAvarageScoreexplain.length() - 1).append("与年级的平均成绩保持持平");
            if(overAvarageScoreexplain.length() == 0 && lowAvarageScoreexplain.length() == 0)
                equalAvarageScoreexplain.append("。");
            else
                equalAvarageScoreexplain.append("，");
        }
        if(lowAvarageScoreexplain.length() != 0) {
            lowAvarageScoreexplain.deleteCharAt(lowAvarageScoreexplain.length() - 1).append("低于年级的平均成绩");
            if(overAvarageScoreexplain.length() == 0 && equalAvarageScoreexplain.length() == 0)
                lowAvarageScoreexplain.append("。");
            else
                lowAvarageScoreexplain.append("。");
        }



        environmentData.put("classAvageScoreStr",classAvageScoreStr.toString());
        environmentData.put("gradeAvageScoreStr",gradeAvageScoreStr.toString());
        environmentData.put("questiontypeNameStr",questiontypeNameStr.toString());
        environmentData.put("qualifiedPercentage", String.format("%.1f", qualifiedNum * 1.0 / classScoreVos.size() * 100) + "%");
        environmentData.put("middlingPercentage", String.format("%.1f",  middlingNum * 1.0 / classScoreVos.size() * 100) + "%");
        environmentData.put("excellentPercentage", String.format("%.1f", excellentNum * 1.0 / classScoreVos.size() * 100) + "%");
        environmentData.put("userPercentage", String.format("%.1f", 100-exceedUserNum * 1.0 / classScoreVos.size() * 100) + "%");
        environmentData.put("explain",totalScoreexplain.toString()+overAvarageScoreexplain.toString()+equalAvarageScoreexplain.toString()+lowAvarageScoreexplain.toString());
        return environmentData;
    }




    /**
     * 获取家庭问卷的具体解释
     * @param type 父亲或母亲
     * @param careScore 关心分
     * @param overProtectionScore 过度保护分
     * @return
     */
    private Map<String,String> getFamilyExplain(String type,Integer careScore,Integer overProtectionScore){
        Map<String,Object> familyDatas = (Map<String,Object>)this.getTypeDatasData("2").get("family");
        Map<String,String> map = new HashMap<>();
        if(type.equals("father")){
            if(careScore <= 24 && overProtectionScore <= 14){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("hushi");
                map.put("top","270");
                map.put("left","50");
                map.put("type","hushi");
                map.put("explain",qtypeInfo.getExplainSecond().get(1));
            }
            if(careScore <= 24 && overProtectionScore > 14){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("zhuanzhi");
                map.put("top","90");
                map.put("left","50");
                map.put("type","zhuanzhi");
                map.put("explain",qtypeInfo.getExplainSecond().get(1));
            }
            if(careScore > 24 && overProtectionScore <= 14){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("minzhu");
                map.put("top","270");
                map.put("left","250");
                map.put("type","minzhu");
                map.put("explain",qtypeInfo.getExplainSecond().get(1));
            }
            if(careScore > 24 && overProtectionScore > 14){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("quanwei");
                map.put("top","90");
                map.put("left","250");
                map.put("type","quanwei");
                map.put("explain",qtypeInfo.getExplainSecond().get(1));
            }
        }

        if(type.equals("mother")){
            if(careScore <= 27 && overProtectionScore <= 7){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("hushi");
                map.put("top","270");
                map.put("left","50");
                map.put("type","hushi");
                map.put("explain",qtypeInfo.getExplainSecond().get(2));
            }
            if(careScore <= 27 && overProtectionScore > 7){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("zhuanzhi");
                map.put("top","90");
                map.put("left","50");
                map.put("type","zhuanzhi");
                map.put("explain",qtypeInfo.getExplainSecond().get(2));
            }
            if(careScore > 27 && overProtectionScore <= 7){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("minzhu");
                map.put("top","270");
                map.put("left","250");
                map.put("type","minzhu");
                map.put("explain",qtypeInfo.getExplainSecond().get(2));
            }
            if(careScore > 27 && overProtectionScore > 7){
                QtypeInfo qtypeInfo = (QtypeInfo)familyDatas.get("quanwei");
                map.put("top","90");
                map.put("left","250");
                map.put("type","quanwei");
                map.put("explain",qtypeInfo.getExplainSecond().get(2));
            }
        }
        return map;
    }

    /**
     * 从用户分数中构建出该用户的非智力因素题目的数据结构
     * @param userScores 用户分数
     * @return
     */
    private Map<String,Object> getNonIntelligenceData(List<Score> userScores,Integer intelligenceSize){
        Map<String,Object> nonIntelligence = new HashMap<>();

        List<Score> scores = new ArrayList<>();
        for (Score score: userScores) {
            if(score.getQtype().equals(SystemConstant.QUESTION_TYPE_PERSONALITY))
                scores.add(score);
        }
        Map<String,String> personailty = new HashMap<>();
        StringBuilder personailtyExplainSecond = new StringBuilder();
        String[] dimensions = {"neuroticism", "responsibility" , "exploratory", "openness", "independent",
                "agreeableness", "socialing"};
        String[] dimnames = {"情绪稳定", "责任感", "探究性", "开放性", "独立性", "宜人性", "人际交往"};
        StringBuilder personailtyScore = new StringBuilder();//存放人格的分数
        StringBuilder personailtyDimName = new StringBuilder();//存放维度名字
        for (Score score: scores){
            int i = Integer.parseInt(score.getDimension()) - 1;
            personailtyScore.append(score.getStandardScore()+",");
            personailtyDimName.append(dimnames[i]+",");
            JsonNode secondSection = this.descriptionData.findPath("secondSection").findPath(dimensions[i]);
            if (Double.parseDouble(score.getStandardScore()) < 85) {
                personailtyExplainSecond.append(secondSection.findPath("low").asText() + ";");
            }
            else if (Double.parseDouble(score.getStandardScore()) > 115) {
                personailtyExplainSecond.append(secondSection.findPath("high").asText() + ";");
            }
            else {
                personailtyExplainSecond.append(secondSection.findPath("middle").asText() + ";");
            }
        }
        personailty.put("name","人格特征描述");
        personailty.put("score",personailtyScore.toString().substring(0, personailtyScore.toString().length() - 1));
        personailty.put("dimname",personailtyDimName.toString().substring(0, personailtyDimName.toString().length() - 1));
        personailty.put("explainSecond",personailtyExplainSecond.toString().substring(0, personailtyExplainSecond.toString().length() - 1));
        nonIntelligence.put("personailty",personailty);
        return nonIntelligence;
    }




    /**
     * 从用户分数中构建出该用户的智力因素题目的数据结构
     * @param userScores 用户分数
     * @return
     */
    private  List<Map<String,Object>> getIntelligenceData(UserVo userVo,List<Score> userScores,Integer orgCode){
        List<Map<String,Object>> intelligence = new ArrayList<>();//智力
        List<Score> attention = new ArrayList<>();//注意力
        List<Score> memory = new ArrayList<>();//记忆力
        List<Score> spaceCapacity = new ArrayList<>();//空间能力
        List<Score> reasoningAbility = new ArrayList<>();//推理能力
        List<Score> emotionalIntelligence = new ArrayList<>();//情绪智力
        List<Score> criticalThinking = new ArrayList<>();//批判性思维
        List<Score> creativity = new ArrayList<>();//创造力
        for (Score score: userScores) {
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_SHAPE_LINKING || score.getQtype() == SystemConstant.QUESTION_TYPE_GRAPF_SEARCH)
                attention.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT || score.getQtype() == SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION || score.getQtype() == SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY )
                memory.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_SPACE_ROTATION)
                spaceCapacity.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING || score.getQtype() == SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING  || score.getQtype() == SystemConstant.QUESTION_TYPE_MATRIX_REASONING )
                reasoningAbility.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION || score.getQtype() == SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING)
                emotionalIntelligence.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY || score.getQtype() == SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY)
                criticalThinking.add(score);
            if(score.getQtype() == SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION)
                creativity.add(score);
        }
        List<List<Score>> scores = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if(attention.size()!=0){
            scores.add(attention);
            names.add("注意力");
        }
        if(memory.size()!=0){
            scores.add(memory);
            names.add("记忆力");
        }
        if(spaceCapacity.size()!=0){
            scores.add(spaceCapacity);
            names.add("空间能力");
        }
        if(reasoningAbility.size()!=0){
            scores.add(reasoningAbility);
            names.add("推理能力");
        }
        if(emotionalIntelligence.size()!=0){
            scores.add(emotionalIntelligence);
            names.add("情绪智力");
        }
        if(criticalThinking.size()!=0){
            scores.add(criticalThinking);
            names.add("批判性思维");
        }
        if(creativity.size()!=0){
            scores.add(creativity);
            names.add("创造力");
        }

        for (int i = 0; i < scores.size(); i++) {
            List<Map<String,String>> lists = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();
            for (int j = 0; j < scores.get(i).size(); j++) {
                Map<String,String> sortData = new HashMap<>();
                Score score = scores.get(i).get(j);
                List<Questiontype> questiontypes = questionTypeDao.findByUseStatus();
                String introduce = "";
                for (Questiontype questiontype: questiontypes){
                    if (questiontype.getId().equals(score.getQtype()))
                        introduce = questiontype.getIntroduce();
                }
                sortData.put("index",Integer.toString(j + 1));//第三层目录序号
                sortData.put("name", introduce);//小节名字
                sortData.put("scoreStr","成绩");
                sortData.put("sortStr","排名");
                sortData.put("score",score.getStandardScore());
                Integer socreLevel = this.getScoreLevel(score.getStandardScore());
                sortData.put("socreLevel",socreLevel.toString());
                sortData.put("lineWidth", this.getLineWidth(socreLevel, score.getStandardScore()));
                String percentage = this.getSortPercentage(userVo.getGrade(), score.getStandardScore(), score.getQtype(), orgCode);
                sortData.put("percentage", percentage);

                String scoreLevelStr = this.getScoreLevelStr(score.getStandardScore());
                sortData.put("level", this.descriptionData.findPath(this.flects(introduce))
                        .findPath("level").asText().replace("\"", "")
                        .replaceFirst("@@@@", score.getStandardScore()).replaceFirst("@@@@", scoreLevelStr)
                        .replaceFirst("@@@@", percentage));
                sortData.put("explain", this.descriptionData.findPath(this.flects(introduce))
                        .findPath(this.flects(scoreLevelStr)).asText().replace("\"", ""));
                lists.add(sortData);
            }
            map.put("index",Integer.toString(i + 3));
            map.put("data",lists);
            map.put("color",this.getBackGroundColor(i+1));
            map.put("name", names.get(i));
            intelligence.add(map);
        }

        return intelligence;
    }

    /**
     * 建立英文和汉语的映射
     * @param
     * @return
     */
    private String flects(String ability){
        Map<String, String> contents = new HashMap<>();
        contents.put("注意转移能力", "attentionShifting");
        contents.put("视觉搜索能力", "visualSearch");
        contents.put("长时记忆能力", "longTermMemory");
        contents.put("短时记忆能力", "shortTermMemory");
        contents.put("心理旋转能力", "psychologicalRevolving");
        contents.put("言语推理能力", "verbalReasoning");
        contents.put("矩阵推理能力", "matrixReasoning");
        contents.put("类比推理能力", "analogicalReasoning");
        contents.put("批判性思维能力", "criticalThinkingCompetence");
        contents.put("批判性思维素质", "criticalThinkingDisposition");
        contents.put("远距离联想", "RAT");
        contents.put("情绪识别", "emotionalPerceiving");
        contents.put("情绪理解", "emotionalUnderstanding");

        contents.put("偏低", "low");
        contents.put("合格", "qualified");
        contents.put("中等", "middle");
        contents.put("优秀", "good");
        contents.put("超常", "excellent");

        contents.put("权威型", "authoritative");
        contents.put("专制型", "autocratic");
        contents.put("民主型", "democratic");
        contents.put("忽视型", "indulgent");

        String result = "";
        for(Map.Entry<String, String> entry: contents.entrySet()){
            if (ability.equals(entry.getKey()))
                result = entry.getValue();
            if(ability.equals(entry.getValue()))
                result = entry.getKey();
        }
        return result;
    }
    /**
     * 计算第三章的中的各个题型在年级的百分比
     * @param grade
     * @param standardScore
     * @param qtype
     * @param orgCode
     * @return
     */
    private String getSortPercentage(String grade,String standardScore,Integer qtype,Integer orgCode){
        List<Score> scores = scoreDao.findByGrade(grade,qtype,orgCode);
        Integer exceedUserNum = 0;
        for (int i = 0; i < scores.size(); i++) {
            Score  score = scores.get(i);

            if(Integer.parseInt(score.getStandardScore()) > Integer.parseInt(standardScore))
                exceedUserNum++;

        }
        String percentage =  String.format("%.1f", 100-exceedUserNum * 1.0 / scores.size() * 100) + "%";
        return percentage;
    }


    /**
     * 第三章的分数矩形框div的长度
     * @param scoreLevel
     * @param score
     * @return
     */
    private String getLineWidth(Integer scoreLevel,String score){
        if(scoreLevel == 1){
            return String.valueOf(Integer.parseInt(score) / 70.0 * 73);
        }else if(scoreLevel == 2){
            return String.valueOf(73 + (Integer.parseInt(score)-70) / 20.0 * 113);
        }else if(scoreLevel == 3){
            return String.valueOf(186 + (Integer.parseInt(score)-90) / 20.0 * 113);
        }else if(scoreLevel == 4){
            return String.valueOf(299 + (Integer.parseInt(score)-110) / 20.0 * 113);
        }else {
            return String.valueOf(412 + (Integer.parseInt(score)-130) / 10.0 * 112);
        }
    }

    /**
     * 第三章的分数矩形框的颜色定义
     * @param index
     * @return
     */
    private String getBackGroundColor(Integer index){
        if(index == 4)
            return "#CCEF7F";
        if(index == 5)
            return "#FEAB3C";
        else
            return "#FE6F6E";

    }

    private QtypeInfo getQtypeInfoData(String type,String outIndex,String innerIndex){
        Map<String,Map<String,Object>> reportDatas = this.initReportInfo();
        Map<String,Object> typeDatas = reportDatas.get(type);
        Map<String,Object> qtypedatas= (Map<String,Object>)typeDatas.get(outIndex);
        return (QtypeInfo)qtypedatas.get(innerIndex);

    }

    private  Map<String,Object> getTypeDatasData(String type){
        Map<String,Map<String,Object>> reportDatas = this.initReportInfo();
        Map<String,Object> typeDatas = reportDatas.get(type);
        return typeDatas;

    }

    /**
     * 获取分数的数字等级
     * @param scoreStr
     * @return
     */
    private Integer getScoreLevel(String scoreStr){
        Integer score = Integer.parseInt(scoreStr);
        if(score >= 130)
            return 5;
        else if(score >= 110)
            return 4;
        else if(score >= 90)
            return 3;
        else if(score >= 70)
            return 2;
        else
            return 1;
    }

    /**
     * 获取分数的文字等级
     * @param scoreStr
     * @return
     */
    private String getScoreLevelStr(String scoreStr){
        Integer score = Integer.parseInt(scoreStr);
        if(score >= 130)
            return "超常";
        else if(score >= 110)
            return "优秀";
        else if(score >= 90)
            return "中等";
        else if(score >= 70)
            return "合格";
        else
            return "偏低";
    }

    /**
     * 按照机构id打包pdf文件
     * @param orgCode
     */
    public void zipPdf(Integer orgCode){
        List<User> users = userDao.findUserByOrgCodeWithScore(orgCode);
        try {
            byte[] buffer = new byte[1024];
            String strZipName = "F://demo.rar";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
            for(int i=0;i<users.size();i++) {
                User user = users.get(i);
                File file = new File("F://bb_" +user.getId() +".pdf");
                FileInputStream fis = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(user.getName()+".pdf"));
                int len;
                //读入需要下载的文件的内容，打包到zip文件
                while((len = fis.read(buffer))>0) {
                    out.write(buffer,0,len);
                }
                out.closeEntry();
                fis.close();
            }
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 个人报告中的数据容器
     * @return
     */
    private Map<String,Map<String,Object>> initReportInfo(){
        Map<Integer,String> explain1 = new HashMap<>();
        Map<Integer,String> suggestion1 = new HashMap<>();
        explain1.put(1,"你的注意转移能力测试得分为***，此项能力偏低，注意的转移能力非常弱。;" + "1.你很难将注意力从上一项事物中抽离出来，在开始下一项任务时，注意力容易停留在上一项任务中。;" + "2.你很难及时将注意力投入学习任务，容易有“拖延症”情况的存在。;" + "3.你很难快速进入学习状态，不能灵活转移注意力。所以在学习中，你不能很快开始新的学习任务，容易产生“万事开头难”的情况。");
        explain1.put(2,"你的注意转移能力测试得分为***，此项能力刚好合格，注意的转移能力不太强。;" + "1.你比较难将注意力从上一项事物中抽离出来，在开始下一项任务时，注意力较易停留在上一项任务中。;" + "2.你比较难及时将注意力投入学习任务，较易有“拖延症”情况的存在。;" + "3.你比较难快速进入学习状态，不太能灵活转移注意力。所以在学习中，你比较难很快开始新的学习任务，比较容易产生“万事开头难”的情况。");
        explain1.put(3,"你的注意转移能力测试得分为***，此项能力处于中等，注意的转移能力一般。;" + "1.你的注意力从上一项任务中抽离出来的能力一般，有时会停留在上一项任务中。;" + "2.你注意转移能力一般，可以将注意力投入学习任务，不太容易有“拖延症”。;" + "3.在某些情况下，你可以进入学习状态，可以转移注意力。在学习中，你能较快开始新的学习任务，出现“万事开头难”的情况较少。");
        explain1.put(4,"你的注意转移能力测试得分为***，此项能力优秀，注意的转移能力较强。;" + "1.你可以较快的将注意力从上一项事物中抽离出来。;" + "2.你注意转移能力比较好，可以比较及时将注意力投入学习任务的能力比较好，不会有“拖延症”情况的存在。;" + "3.你较易快速进入学习状态，可以较灵活转移注意力。所以在学习中，你能较快开始新的学习任务，不会产生“万事开头难”的情况。");
        explain1.put(5,"你的注意转移能力测试得分为***，此项能力超常，注意的转移能力非常强。;" + "1.你可以很快将注意力从上一项事物中抽离出来，将注意力转移到下一项任务对你来说非常容易。;" + "2.你注意转移能力非常好，能及时投入学习任务，转移速度快。;" + "3.你能快速进入学习状态，灵活转移注意力。所以在学习中，你能快速开始新的学习任务。");
        suggestion1.put(1,"你的注意转移能力较弱，需要比常人花更多的时间在注意转移能力提升方面，可以通过一些方法在此方面进行提升：;" + "转换训练：收集各种类型知识的资料，迅速转换不同类的知识进行快速的反应回答，可以提升个体的注意转移能力。;" + "抗干扰训练：增加外部的干扰因素，在规定的时间内解答题目，并注重提高自己解答的正确率。");
        suggestion1.put(2,"你的注意转移能力刚合格，需要比常人花更多的时间在注意转移能力提升方面，可以通过一些方法在此方面进行提升：;" + "转换训练：收集各种类型知识的资料，迅速转换不同类的知识进行快速的反应回答，可以提升个体的注意转移能力。;" + "抗干扰训练：增加外部的干扰因素，在规定的时间内解答题目，并注重提高自己解答的正确率。");
        suggestion1.put(3,"你的注意转移能力一般，可以通过一些方法在此方面进行提升：;" + "转换训练：收集各种类型知识的资料，迅速转换不同类的知识进行快速的反应回答，可以提升个体的注意转移能力。;" + "抗干扰训练：增加外部的干扰因素，在规定的时间内解答题目，并注重提高自己解答的正确率。");
        suggestion1.put(4,"你的注意转移能力较强，可以比常人花更少的时间在注意转移能力方面，如果想要进一步提升可以参考下面的方法：;" + "转换训练：收集各种类型知识的资料，迅速转换不同类的知识进行快速的反应回答，可以提升个体的注意转移能力。;" + "抗干扰训练：增加外部的干扰因素，在规定的时间内解答题目，并注重提高自己解答的正确率。");
        suggestion1.put(5, "你的注意转移能力非常强，可以花比较少的时间在注意转移能力方面，如果想要进一步提升可以参考下面的方法：;" + "转换训练：收集各种类型知识的资料，迅速转换不同类的知识进行快速的反应回答，可以提升个体的注意转移能力。;" + "抗干扰训练：增加外部的干扰因素，在规定的时间内解答题目，并注重提高自己解答的正确率。");
        QtypeInfo qtypeInfo1 = new QtypeInfo("注意转移能力","朱琪","注意的转移是指根据人的需要、目的与意愿，把对某;" +
                "刺激信息的注意转移到对另一个刺激信息的关注上。具体来讲，注意转移就是在视觉环境中，伴随着头部和眼睛的运动或不伴随头部和眼睛的运动，我们的注意从一种信息指向另一种信息的过程。;" +
                "注意转移能力高的个体具有更强的注意转移能力，能迅速地从上一项工作中抽离，开始下一项工作，较少的遇到“万事开头难”或者“拖延症”这样的情况。所以，注意转移能力高的个体更容易投入学习任务，能按时完成作业。","注意转移是指当环境或任务发生变化时，注意从一个对象或活动转到另一个对象或活动上。",explain1,suggestion1);

        Map<Integer,String> explain2 = new HashMap<>();
        Map<Integer,String> suggestion2 = new HashMap<>();
        explain2.put(1,"你的视觉搜索能力测试得分为***，此项能力偏低，从视觉范围内找出自己需要的信息的能力非常弱。;" + "1.你很难快速的接收知识，比如在看书时，“一目十行”对你来说非常难。;" + "2.你搜索外部信息的能力很弱，不擅长快速阅读文章，搜索自己想要的信息。;" + "3.你很难快速的学习和获取经验，接受知识的效率很低。所以在学习中，你不能根据书本的内容快速掌握相关知识，很难适应现代多媒体教育和远程教育。");
        explain2.put(2,"你的视觉搜索能力测试得分为***，此项能力刚好合格，从视觉范围内找出自己需要的信息的能力不太强。;" + "1.你较难快速的接收知识，比如在看书时，“一目十行”对你来说比较难。;" + "2.你搜索外部信息的能力较弱，不太擅长快速阅读文章，搜索自己想要的信息。;" + "3.你也比较难快速的学习和获取经验，接受知识的效率较低。在学习中，你不太能根据书本的内容快速掌握相关知识，较难适应现代多媒体教育和远程教育。");
        explain2.put(3,"你的视觉搜索能力测试得分为***，此项能力处于中等，从视觉范围内找出自己需要的信息的能力一般。;" + "1.你快速的接收知识的能力一般，比如在看书时，能否“一目十行”取决于知识的难易程度。;" + "2.你搜索外部信息的能力一般，能够对简单文章进行阅读，搜索自己想要的信息。;" + "3.在某些情境下你能快速的学习和获取经验，接受知识的效率一般。在学习中，你一定程度上能快速掌握相关知识，适应现代多媒体教育和远程教育的水平一般。");
        explain2.put(4,"你的视觉搜索能力测试得分为***，此项能力较为优秀，从视觉范围内找出自己需要的信息的能力较强。;" + "1.你能够比较快速的接收知识，比如在看书时，你能较好的对文章做到“一目十行”。;" + "2.你搜索外部信息的能力较好，能够快速地对简单文章进行阅读，搜索自己想要的信息。;" + "3.你能够较快速的学习和获取经验，接受知识的效率较高。所以在学习中，你能较快速掌握相关知识，较好的适应现代多媒体教育和远程教育。");
        explain2.put(5,"你的视觉搜索能力测试得分为***，此项能力超常，从视觉范围内找出自己需要的信息的能力非常强。;" + "1.你能够快速的接收知识，比如在看书时，做到“一目十行”对你来说非常容易；;" + "2.你搜索外部信息的能力非常好，能够快速地对各类文章进行阅读，搜索自己想要的信息。;" + "3.你能够快速的学习和获取经验，接受知识的效率非常高。所以在学习中，你能快速掌握相关知识，非常适应现代多媒体教育和远程教育。");
        suggestion2.put(1,"你的视觉搜索能力较弱，需要比常人花更多的时间在视觉搜索能力提升方面，可以通过一些方法在此方面进行提升：;" + "搜索能力训练：训练自己快速的在一篇文章中找出关键词。;" + "速度训练：在一定时间内阅读一篇文章并说出大体的意思。");
        suggestion2.put(2,"你的视觉搜索能力刚合格，需要比常人花更多的时间在视觉搜索能力提升方面，可以通过一些方法在此方面进行提升：;" + "搜索能力训练：训练自己快速的在一篇文章中找出关键词。;" + "速度训练：在一定时间内阅读一篇文章并说出大体的意思。");
        suggestion2.put(3,"你的视觉搜索能力一般，可以通过一些方法在此方面进行提升：;" + "搜索能力训练：训练自己快速的在一篇文章中找出关键词。;" + "速度训练：在一定时间内阅读一篇文章并说出大体的意思。");
        suggestion2.put(4,"你的视觉搜索能力较强，可以比常人花更少的时间在视觉搜索能力方面，如果想要进一步提升可以参考下面的方法：;" + "搜索能力训练：训练自己快速的在一篇文章中找出关键词。;" + "速度训练：在一定时间内阅读一篇文章并说出大体的意思。");
        suggestion2.put(5,"你的视觉搜索能力非常强，可以花比较少的时间在视觉搜索能力方面，如果想要进一步提升可以参考下面的方法：;" + "搜索能力训练：训练自己快速的在一篇文章中找出关键词。;" + "速度训练：在一定时间内阅读一篇文章并说出大体的意思。");
        QtypeInfo qtypeInfo2 = new QtypeInfo("视觉搜索能力","朱琪","    视觉搜索能力是指从视觉范围内出现的许多客体刺激中快速的找到自己所想要的目标客体的过程。;" +
                "    视觉搜索能力高的个体在学习知识和获取经验上更快速，接收信息的效率也更高。所以，视觉搜索能力高的个体学习接受知识的效率更高，更能适应现代多媒体教育和远程教育。","视觉搜索能力是指从视觉范围内出现的许多客体刺激中快速的找到自己所想要的目标客体的过程。",explain2,suggestion2);

        Map<String,Object> qtypes1 = new HashMap<>();
        qtypes1.put("name","注意力");
        qtypes1.put("5",qtypeInfo1);
        qtypes1.put("4",qtypeInfo2);
        qtypes1.put("introduce","注意力就是心理活动或意识对一定对象的指向和集中。注意的有四个特性，包括：注意广度、注意稳定性、注意分配和注意转移。本系统主要测量注意转移和注意广度两个维度");


        Map<Integer,String> explain3 = new HashMap<>();
        Map<Integer,String> suggestion3 = new HashMap<>();
        explain3.put(1,"你的长时记忆测试得分为***，此项能力偏低，对知识的记忆能力非常弱。;" + "1.你很难再认出学习过的知识，比如，学过的知识再次出现在你面前，你不能确定你学习过。;" + "2.你很难回忆起学习过的知识，比如，默写课文对你来说非常难。;" + "3.你很难将新学的知识和从前学过的知识结合起来，不能做到融会贯通。所以在学习中，你不能回忆旧知识，就很难发现新旧知识间的联系，掌握新知识会非常困难。");
        explain3.put(2,"你的长时记忆测试得分为***，此项能力刚好合格，对对知识的记忆能力不太强。;" + "1.你比较难再认出学习过的知识，比如，学过的知识再次出现在你面前，你较难确定你学习过。;" + "2.你比较难回忆起学习过的知识，比如，默写课文对你来说比较难。;" + "3.你也比较难将新学的知识和从前学过的知识结合起来，较难做到融会贯通。所以在学习中，你较难回忆旧知识，就较难发现新旧知识间的联系，掌握新知识较困难。");
        explain3.put(3,"你的长时记忆测试得分为***，此项能力处于中等，对知识的记忆能力一般。;" + "1.你可以再认出学习过的知识，比如，学过的知识再次出现在你面前，你能确定你学习过。;" + "2.你能够一定程度上回忆起学习过的知识，比如，默写课文时，你能回忆起课文的大体内容，但还存在一定错误。;" + "3.在某些情境下你能将新学的知识和从前学过的知识结合起来，可以做到融会贯通。所以在学习中，你能回忆起一些学过的知识，一定程度上发现新旧知识间的联系，掌握新知识能力一般。");
        explain3.put(4,"你的长时记忆测试得分为***，此项能力较为优秀，对知识的记忆能力较强。;" + "1.你可以再认出学习过的知识，比如，学过的知识再次出现在你面前，你能确定你学习过。;" + "2.你能够较好的回忆起学习过的知识，比如，默写课文时，你能默写课文，但还存在一些细节的错误。;" + "3.你能较好的将新学的知识和从前学过的知识结合起来，可以较好的做到融会贯通。所以在学习中，你能较好回忆起学过的知识，发现新旧知识间的联系，掌握新知识能力较好。");
        explain3.put(5,"你的长时记忆测试得分为***，此项能力超常，对知识的记忆能力非常强。;" + "1.你可以再认出学习过的知识，比如，学过的知识再次出现在你面前，你能非常快的意识到这个知识是学过的。;" + "2.你能够很好的回忆起学习过的知识，比如，正确的默写课文对你来说非常容易。;" + "3.你能够非常快的将新学的知识和从前学过的知识结合起来，做到融会贯通。所以在学习中，你能回忆起学过的知识，发现新旧知识间的联系，掌握新知识能力对你来说非常容易。");
        suggestion3.put(1,"你的长时记忆能力较弱，需要比常人花更多的时间在长时记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "改善记忆策略：要在理解的基础上背诵，而不是机械的一直念，一直念。;" + "及时复习：之前学过的知识要经常拿出来再看一遍，要温故知新。");
        suggestion3.put(2,"你的长时记忆能力刚合格，需要比常人花更多的时间在长时记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "改善记忆策略：要在理解的基础上背诵，而不是机械的一直念，一直念。;" + "及时复习：之前学过的知识要经常拿出来再看一遍，要温故知新。");
        suggestion3.put(3,"你的长时记忆能力一般，可以通过一些方法在此方面进行提升：;" + "改善记忆策略：要在理解的基础上背诵，而不是机械的一直念，一直念。;" + "及时复习：之前学过的知识要经常拿出来再看一遍，要温故知新。");
        suggestion3.put(4,"你的长时记忆能力较强，可以比常人花更少的时间在长时记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "改善记忆策略：要在理解的基础上背诵，而不是机械的一直念，一直念。;" + "及时复习：之前学过的知识要经常拿出来再看一遍，要温故知新。");
        suggestion3.put(5,"你的长时记忆能力非常强，可以花比较少的时间在长时记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "改善记忆策略：要在理解的基础上背诵，而不是机械的一直念，一直念。;" + "及时复习：之前学过的知识要经常拿出来再看一遍，要温故知新。");
        QtypeInfo qtypeInfo3 = new QtypeInfo("长时记忆","朱琪","    长时记忆是将信息在头脑中进行长时间储存，并通过回忆和再认进行提取的能力。;" +
                "    长时记忆能力强的个体具有更强的长时记忆力，能较多的回忆起过去学习过的知识,对新知识的学习和已学知识的掌握能力好。当个体学习新知识时,长时记忆能力高的个体更容易记住并学会。所以，长时记忆能力高的个体更容易得到好成绩。;","长时记忆是将信息在头脑中进行长时间储存，并通过回忆和再认进行提取的能力。",explain3,suggestion3);

        Map<Integer,String> explain4 = new HashMap<>();
        Map<Integer,String> suggestion4 = new HashMap<>();
        explain4.put(1,"你的操作记忆测试得分为***，此项能力偏低，存储加工信息的能力非常弱。;" + "1.你很难暂时存储和加工信息，比如，打电话时暂时记住电话号码对你来说非常困难。;" + "2.你非常难高效加工感知到的信息和线索，比如，数学的心算对你来说非常难。;" + "3.你很难有效掌握需要记忆的信息，所以在学习中，你在推理、问题解决和智力活动方面存在非常大的困难。");
        explain4.put(2,"你的操作记忆能力测试得分为***，此项能力刚好合格，存储加工信息的能力不太强。;" + "1.你比较难暂时存储和加工信息，比如，打电话时暂时记住电话号码对你来说比较难。;" + "2.你很难高效加工感知到的信息和线索，比如，数学的心算对你来说比较难。;" + "3.你也比较难有效掌握需要记忆的信息，所以在学习中，你在推理、问题解决和智力活动方面存在较大的困难。");
        explain4.put(3,"你的操作记忆能力测试得分为***，此项能力处于中等，存储加工信息的能力一般。;" + "1.你暂时存储和加工信息的能力一般，比如，打电话时暂时记住电话号码对你来说不是很困难。;" + "2.你高效加工感知到的信息和线索的能力一般，比如，你能较好的完成简单的数学心算题。;" + "3.在某些情境下你能掌握需要记忆的信息，所以在学习中，你在推理、问题解决和智力活动方面的能力一般。");
        explain4.put(4,"你的操作记忆能力测试得分为***，此项能力较为优秀，存储加工信息的能力较强。;" + "1.你暂时存储和加工信息的能力较好，比如，打电话时暂时记住电话号码对你来说不难。;" + "2.你高效加工感知到的信息和线索的能力较好，比如，你能完成简单的数学心算题。;" + "3.你能够较好的掌握需要记忆的信息，所以在学习中，你在推理、问题解决和智力活动方面的能力较好。");
        explain4.put(5,"你的操作记忆能力测试得分为***，此项能力超常，存储加工信息的能力非常强。;" + "1.你暂时存储和加工信息的能力非常好，比如，打电话时暂时记住电话号码对你来说非常容易。;" + "2.你高效加工感知到的信息和线索的能力非常好，比如，完成数学心算题对你来说非常容易。;" + "3.你能够非常好的掌握需要记忆的信息，所以在学习中，你在推理、问题解决和智力活动方面的能力非常强。");
        suggestion4.put(1, "你的操作记忆能力较弱，需要比常人花更多的时间在操作记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "逻辑整理练习：学生在看书的同时，对信息进行逻辑分析。看完后概括原文的中心意思，然后说出围绕中心意思，原文阐述了哪几方面内容，其相互之间逻辑关系是什么。;" + "影子跟读练习：朗读课本，提高注意的集中，有助于记忆");
        suggestion4.put(2,"你的操作记忆能力刚合格，需要比常人花更多的时间在操作记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "逻辑整理练习：学生在看书的同时，对信息进行逻辑分析。看完后概括原文的中心意思，然后说出围绕中心意思，原文阐述了哪几方面内容，其相互之间逻辑关系是什么。;" + "影子跟读练习：朗读课本，提高注意的集中，有助于记忆。");
        suggestion4.put(3,"你的操作记忆能力一般，可以通过一些方法在此方面进行提升：;" + "逻辑整理练习：学生在看书的同时，对信息进行逻辑分析。看完后概括原文的中心意思，然后说出围绕中心意思，原文阐述了哪几方面内容，其相互之间逻辑关系是什么。;" + "影子跟读练习：朗读课本，提高注意的集中，有助于记忆。");
        suggestion4.put(4,"你的操作记忆能力较强，可以比常人花更少的时间在操作记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "逻辑整理练习：学生在看书的同时，对信息进行逻辑分析。看完后概括原文的中心意思，然后说出围绕中心意思，原文阐述了哪几方面内容，其相互之间逻辑关系是什么。;" + "影子跟读练习：朗读课本，提高注意的集中，有助于记忆。");
        suggestion4.put(5,"你的操作记忆能力非常强，可以花比较少的时间在操作记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "逻辑整理练习：学生在看书的同时，对信息进行逻辑分析。看完后概括原文的中心意思，然后说出围绕中心意思，原文阐述了哪几方面内容，其相互之间逻辑关系是什么。;" + "影子跟读练习：朗读课本，提高注意的集中，有助于记忆。");
        QtypeInfo qtypeInfo4 = new QtypeInfo("工作记忆","朱琪","操作记忆是指个体在执行认知任务过程中, 暂时储存与加工信息的能量有限的系统,是学习、推理、问题解决和智力活动的重要成分。;" +
                "   操作记忆强的个体具有更强的操作记忆力，能高效加工感知到的信息和线索, 有效掌握需要记忆的信息。所以，操作记忆能力强的人，记忆和掌握知识更高效。","操作记忆是指个体在执行认知任务过程中, 暂时储存与加工信息的能量有限的系统,是学习、推理、问题解决和智力活动的重要成分。",explain4,suggestion4);

        Map<Integer,String> explain5 = new HashMap<>();
        Map<Integer,String> suggestion5 = new HashMap<>();
        explain5.put(1,"你的空间工作记忆测试得分为***，此项能力偏低，产生、操作和保持视觉映象的能力非常弱。;" + "1.你很难将视觉空间信息暂时存储在头脑中，比如，你非常难记住情景中的线索，非常容易迷路。;" + "2.你非常难对存储的信息进行加工，所以，在涉及绘画、心理想象、空间运动、定向等的相关方面，你的能力非常弱。");
        explain5.put(2,"你的空间工作记忆测试得分为***，此项能力刚好合格，产生、操作和保持视觉映象的能力不太强。;" + "1.你较难将视觉空间信息暂时存储在头脑中，比如，你较难记住情景中的线索，比较容易迷路。;" + "2.你比较难对存储的信息进行加工，所以，在涉及绘画、心理想象、空间运动、定向等的相关方面，你的能力比较弱。");
        explain5.put(3,"你的空间工作记忆测试得分为***，此项能力处于中等，产生、操作和保持视觉映象的能力一般。;" + "1.你将视觉空间信息暂时存储在头脑中的能力一般，比如，你能大体记住情景中的线索，不太容易迷路。;" + "2.在某些情况下，你能对存储的信息进行加工，所以，在涉及绘画、心理想象、空间运动、定向等的相关方面，你的能力一般。");
        explain5.put(4,"你的空间工作记忆测试得分为***，此项能力较为优秀，产生、操作和保持视觉映象的能力较强。;" + "1.你能比较好的将视觉空间信息暂时存储在头脑中，比如，你能较好的记住情景中的线索，不会迷路。;" + "2.你能较好的对存储的信息进行加工，所以，在涉及绘画、心理想象、空间运动、定向等的相关方面，你的能力较好。");
        explain5.put(5,"你的空间工作记忆测试得分为***，此项能力超常，产生、操作和保持视觉映象的能力非常强。;" + "1.你能够很好的将视觉空间信息暂时存储在头脑中，比如，你非常容易记住情景中的线索，迷路对你来说不可能发生。;" + "2.你能很好的对存储的信息进行加工，所以，在涉及绘画、心理想象、空间运动、定向等的相关方面，你的能力非常强。");
        suggestion5.put(1,"你的空间视觉记忆能力较弱，需要比常人花更多的时间在空间视觉记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "空间视觉记忆训练：看一张图片，然后让别人随机问图片中的物品，你来回答刚才看到的图片中该物品的位置。;空间位置记忆训练：在空间上显示一组数字，显示完后按呈现的顺序依次点击，每天训练40分钟，长期坚持能够提高工作记忆。");
        suggestion5.put(2,"你的空间视觉记忆能力刚合格，需要比常人花更多的时间在空间视觉记忆能力提升方面，可以通过一些方法在此方面进行提升：;" + "空间视觉记忆训练：看一张图片，然后让别人随机问图片中的物品，你来回答刚才看到的图片中该物品的位置。;空间位置记忆训练：在空间上显示一组数字，显示完后按呈现的顺序依次点击，每天训练40分钟，长期坚持能够提高工作记忆。");
        suggestion5.put(3,"你的空间视觉记忆能力一般，可以通过一些方法在此方面进行提升：;" + "空间视觉记忆训练：看一张图片，然后让别人随机问图片中的物品，你来回答刚才看到的图片中该物品的位置。;空间位置记忆训练：在空间上显示一组数字，显示完后按呈现的顺序依次点击，每天训练40分钟，长期坚持能够提高工作记忆。");
        suggestion5.put(4,"你的空间视觉记忆能力较强，可以比常人花更少的时间在空间视觉记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "空间视觉记忆训练：看一张图片，然后让别人随机问图片中的物品，你来回答刚才看到的图片中该物品的位置。;空间位置记忆训练：在空间上显示一组数字，显示完后按呈现的顺序依次点击，每天训练40分钟，长期坚持能够提高工作记忆。");
        suggestion5.put(5,"你的空间视觉记忆能力非常强，可以花比较少的时间在空间视觉记忆能力方面，如果想要进一步提升可以参考下面的方法：;" + "空间视觉记忆训练：看一张图片，然后让别人随机问图片中的物品，你来回答刚才看到的图片中该物品的位置。;空间位置记忆训练：在空间上显示一组数字，显示完后按呈现的顺序依次点击，每天训练40分钟，长期坚持能够提高工作记忆。");
        QtypeInfo qtypeInfo5 = new QtypeInfo("空间工作记忆","朱琪","空间工作记忆用于存贮视觉和空间信息, 主要负责产生、操作和保持视觉映象, 是工作记忆的重要组成部分。;" +
                "     空间工作记忆强的个体具有更强的对存储的信息进行精细加工的能力，在绘画、心理想象、空间运动、定向等认知活动方面能力更强，在涉及这些方面的学业上也更容易学会。","空间工作记忆用于存贮视觉和空间信息, 主要负责产生、操作和保持视觉映象, 是工作记忆的重要组成部分。",explain5,suggestion5);

        Map<String,Object> qtypes2 = new HashMap<>();
        qtypes2.put("name","记忆力");
        qtypes2.put("introduce","记忆力是在头脑中积累和保存个体经验的心理过程。");
        qtypes2.put("7",qtypeInfo3);
        qtypes2.put("2",qtypeInfo4);
        qtypes2.put("3",qtypeInfo5);

        Map<Integer,String> explain6 = new HashMap<>();
        Map<Integer,String> suggestion6 = new HashMap<>();
        explain6.put(1,"你的空间能力测试得分为***，此项能力偏低，空间理解、空间想象和空间操作能力非常弱。;" + "1.你很难理解几何概念，比如分析计算几何图形对你来说非常难。;" + "2.你空间想象能力很弱，不擅长对头脑中的图形进行旋转或操作。;" + "3.你很难掌握与空间有关的科目，比如数学、物理、地理等，不能想象空间中的情境。所以在学习中，你不能根据这些课程中题目所给的情境理解题目内容，很难根据题意想象和操作空间概念。");
        explain6.put(2,"你的空间能力测试得分为***，此项能力刚好合格，对空间理解、空间想象和空间操作能力不太强。;" + "1.你比较难理解几何概念，比如分析计算几何图形对你来说比较难。;" + "2.你空间想象能力较弱，不太擅长对头脑中的图形进行旋转或操作。;" + "3.你也比较难掌握与空间有关的科目，比如数学、物理、地理等，比较难想象空间中的情境。在学习中，你不太能根据这些课程中题目所给的情境理解题目内容，比较难根据题意想象和操作空间概念。");
        explain6.put(3,"你的空间能力测试得分为***，此项能力处于中等，空间理解、空间想象和空间操作能力一般。;" + "1.你可以理解比较简单的几何概念，比如分析计算几何图形，但是速度和准确性一般；;" + "2.你空间想象能力一般，能对头脑中的图形进行简单的旋转或操作。;" + "3.在某些情境下你能够掌握与空间有关的科目，比如数学、物理、地理等，能想象空间中的情境，但是并不完全准确。在学习中，你能根据这些课程中题目所给的情境理解题目内容，能根据题意想象和操作空间概念，但准确性和速度不能保证。");
        explain6.put(4,"你的空间能力测试得分为***，此项能力较为优秀，对空间理解、空间想象和空间操作能力较强。;" + "1.你能够比较好的理解比较简单的几何概念，比如分析计算几何图形。;" + "2.你空间想象能力较好，能对头脑中的图形进行旋转或操作。;" + "3.你能够掌握大部分与空间有关的科目，比如数学、物理、地理等，能想象空间中的情境，能够快速推测题目的空间情境，准确率较高。所以在学习中，你能较好的根据这些课程中题目所给的情境理解题目内容，较快也较准确。");
        explain6.put(5,"你的空间能力测试得分为***，此项能力超常，对对空间理解、空间想象和空间操作能力非常强。;" + "1.你能够很好地理解几何概念，比如分析计算几何图形，这对你来说非常容易；;" + "2.你空间想象能力很好好，能对头脑中的图形进行快速正确地旋转或操作。;" + "3.你能够非常好的掌握与空间有关的科目，比如数学、物理、地理等，能够想象空间中的情境，能够快速推测题目的空间情境，准确率较高。在学习中，你能非常好的根据这些课程中题目所给的情境理解题目内容，速度和准确率都非常高。");
        suggestion6.put(1,"你的空间能力较弱，需要比常人花更多的时间在空间能力提升方面，可以通过一些方法在此方面进行提升：;" + "形体表达：在规定时间内画出看到的复杂形体，想象形体旋转并画出形体 ;" + "即时反馈：在几何题目学习时，通过与老师的交流沟通，获得对几何图形理解是否正确的即时反馈，修正自己的理解方式和方法。");
        suggestion6.put(2,"你的空间能力刚合格，需要比常人花更多的时间在空间能力提升方面，可以通过一些方法在此方面进行提升：形体表达：在规定时间内画出看到的复杂形体，想象形体旋转并画出形体 ;" + "即时反馈：在几何题目学习时，通过与老师的交流沟通，获得对几何图形理解是否正确的即时反馈，修正自己的理解方式和方法。");
        suggestion6.put(3,"你的空间能力一般，可以通过一些方法在此方面进行提升：;" + "形体表达：在规定时间内画出看到的复杂形体，想象形体旋转并画出形体 ;" + "即时反馈：在几何题目学习时，通过与老师的交流沟通，获得对几何图形理解是否正确的即时反馈，修正自己的理解方式和方法。");
        suggestion6.put(4,"你的空间能力较强，可以比常人花更少的时间在空间能力方面，如果想要进一步提升可以参考下面的方法：;" + "形体表达：在规定时间内画出看到的复杂形体，想象形体旋转并画出形体 ;" + "即时反馈：在几何题目学习时，通过与老师的交流沟通，获得对几何图形理解是否正确的即时反馈，修正自己的理解方式和方法。");
        suggestion6.put(5,"你的空间能力非常强，可以花比较少的时间在空间能力方面，如果想要进一步提升可以参考下面的方法：;" + "形体表达：在规定时间内画出看到的复杂形体，想象形体旋转并画出形体 ;" + "即时反馈：在几何题目学习时，通过与老师的交流沟通，获得对几何图形理解是否正确的即时反馈，修正自己的理解方式和方法。");
        QtypeInfo qtypeInfo6 = new QtypeInfo("空间能力","朱琪","空间能力是指人的空间感知能力,是智力在空间认知系统中的一般表现。空间能力包括空间定位能力和空间控制能力,本系统主要关注空间控制能力方面。;" +
                "    空间能力强的个体具有更强的空间感知能力，对色彩、线条、形状、形式、空间及它们之间关系的敏感性很高，感受、辨别、记忆、改变物体的空间关系并借此表达思想和情感的能力比较强，能准确地感觉视觉空间，并把所知觉到的表现出来。在几何、绘画等方面更易习得。","空间能力是指人的空间感知能力,是智力在空间认知系统中的一般表现。",explain6,suggestion6);

        Map<String,Object> qtypes3 = new HashMap<>();
        qtypes3.put("name","空间能力");
        qtypes3.put("introduce","  空间能力是指人的空间感知能力,是智力在空间认知系统中的一般表现。空间能力包括空间定位能力和空间控制能力,本系统主要关注空间控制能力方面。;" +
                "    空间能力强的个体具有更强的空间感知能力，对色彩、线条、形状、形式、空间及它们之间关系的敏感性很高，感受、辨别、记忆、改变物体的空间关系并借此表达思想和情感的能力比较强，能准确地感觉视觉空间，并把所知觉到的表现出来。在几何、绘画等方面更易习得。");
        qtypes3.put("12",qtypeInfo6);


        Map<Integer,String> explain7 = new HashMap<>();
        Map<Integer,String> suggestion7 = new HashMap<>();
        explain7.put(1,"你的言语推理能力测试得分为***，此项能力偏低，对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息的能力非常弱。;" + "1.语言理解能力非常弱，也就是说难以理解别人表达的语言。;" + "2.对语句隐含的信息进行合理推断能力非常弱。也就是说很难推断出别人表达出的隐含信息。;" + "3.在阅读理解时准确地辨明主旨并筛选信息的能力非常低。");
        explain7.put(2,"你的言语推理能力测试得分为***，此项能力刚好合格，对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息的能力不太强。;" + "1.语言理解能力比较弱，也就是说不容易理解别人表达的语言。;" + "2.对语句隐含的信息进行合理推断能力比较弱。也就是说很难推断出别人表达出的隐含信息。;" + "3.在阅读理解时准确地辨明主旨并筛选信息的能力比较低。");
        explain7.put(3,"你的言语推理能力测试得分为***，此项能力处于中等，对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息的能力一般。;" + "1.语言理解能力一般，也就是说在一定程度上难以理解别人表达的语言。;" + "2.对语句隐含的信息进行合理推断能力一般。也就是说在一定程度上不容易推断出别人表达出的隐含信息。;" + "3.在阅读理解时准确地辨明主旨并筛选信息的能力一般。");
        explain7.put(4,"你的言语推理能力测试得分为***，此项能力较强，对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息的能力较强。;" + "1.语言理解能力强，也就是说能理解别人表达的语言。;" + "2.对语句隐含的信息进行合理推断能力强。也就是说在能推断出别人表达出的隐含信息。;" + "3.在阅读理解时准确地辨明主旨并筛选信息的能力强。");
        explain7.put(5,"你的言语推理能力测试得分为***，此项能力超常，对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息的能力非常强。;" + "1.语言理解能力非常强，也就是说很容易理解别人表达的语言。;" + "2.对语句隐含的信息进行合理推断能力非常强。也就是说在能推断出别人表达出的隐含信息。;" + "3.在阅读理解时准确地辨明主旨并筛选信息的能力非常强。");
        suggestion7.put(1,"你的言语推理能力较弱，需要比常人花更多的时间在言语推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "阅读理解练习：大量的阅读名著，并了解其中的言语逻辑，从而提升自己的言语推理能力。;" + "练习写作：通过具有逻辑性的文章来提高自己的言语推理能力。");
        suggestion7.put(2,"你的言语推理能力刚合格，需要比常人花更多的时间在言语推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "阅读理解练习：大量的阅读名著，并了解其中的言语逻辑，从而提升自己的言语推理能力。;" + "练习写作：通过具有逻辑性的文章来提高自己的言语推理能力。");
        suggestion7.put(3,"你的言语推理能力一般，可以通过一些方法在此方面进行提升：;" + "练习写作：通过具有逻辑性的文章来提高自己的言语推理能力;" + "主旨提取练习：做一些提取文章主旨的练习。来提升自己准确地辨明主旨并筛选信息的能力。");
        suggestion7.put(4,"你的言语推理能力较强，可以比常人花更少的时间在语言推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "主旨提取练习：做一些提取文章主旨的练习。来提升自己准确地辨明主旨并筛选信息的能力。;" + "阅读理解练习：大量的阅读名著，并了解其中的言语逻辑，从而提升自己的言语推理能力。");
        suggestion7.put(5,"你的言语推理能力非常强，可以花比较少的时间在言语推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "主旨提取练习：做一些提取文章主旨的练习。来提升自己准确地辨明主旨并筛选信息的能力。;" + "阅读理解练习：大量的阅读名著，并了解其中的言语逻辑，从而提升自己的言语推理能力。");
        QtypeInfo qtypeInfo7 = new QtypeInfo("言语推理能力","葛雷","言语推理是对某些事件、概念陈述的理解和推断的过程。言语推理测验主要考察对语言文字的综合分析能力，所给的文字材料不会很长，但要求受测者对句子的一般意思和特定意义进行理解、对概念和观点进行准确的理解、对语句隐含的信息进行合理推断、比较准确地辨明主旨并筛选信息。;" +
                "言语推理能力较高的个体会具有较强的语言理解能力、信息筛选能力、以及推断能力。","言语推理能力是通过理解句子的含义，对这个语句隐含的信息进行合理推断、从而做出判断的能力。",explain7,suggestion7);

        Map<Integer,String> explain8 = new HashMap<>();
        Map<Integer,String> suggestion8 = new HashMap<>();
        explain8.put(1,"你的类比推理能力测试得分为***，此项能力偏低，在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力非常弱。;" + "1.联想能力非常弱。也就是说很难把一个物体与它相似的物体联系起来。;" + "2.创造性思维非常弱。;" + "3.观察对比能力非常弱。很难观察到物体之间的相似之处并且也很难将不同物体进行对比。");
        explain8.put(2,"你的类比推理能力测试得分为***，此项能力刚合格，在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力比较弱。;" + "1.联想能力比较差。也就是说不容易把一个物体与它相似的物体联系起来。;" + "2.创造性思维比较差。;" + "3.观察对比能力比较差。不容易观察到物体之间的相似之处并且也不容易将不同物体进行对比。");
        explain8.put(3,"你的类比推理能力测试得分为***，此项能力处于中等，在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力一般。;" + "1.联想能力一般。也就是说在一定程度上能把一个物体与它相似的物体联系起来。;" + "2.创造性思维一般。;" + "3.观察对比能力一般。在一定程度上能观察到物体之间的相似之处并且也在一定程度上能将不同物体进行对比。");
        explain8.put(4,"你的类比推理能力测试得分为***，在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力较强。;" + "1.联想能力强。也就是说能把一个物体与它相似的物体联系起来。;" + "2.创造性思维强。;" + "3.观察对比能力强。能观察到物体之间的相似之处并且也能将不同物体进行对比。");
        explain8.put(5,"你的类比推理能力测试得分为***，此项能力超常，在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力非常强。;" + "1.联想能力非常强。也就是说很容易把一个物体与它相似的物体联系起来。;" + "2.创造性思维非常强。;" + "3.观察对比能力非常强。很容易观察到物体之间的相似之处并且也很容易将不同物体进行对比。");
        suggestion8.put(1,"你的类比推理能力较弱，需要比常人花更多的时间在类比推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "发挥想象在推理中的作用：必须丰富自己的想象素材，扩大自己的知识范围。知识基础越坚实，知识面越广，就越能发挥自己的想象力。想象力越丰富就越容易提升自己的推理能力。;" + "进行创造性的训练：可以进行一些发散性思维的训练，培养自己的求知欲。");
        suggestion8.put(2,"你的类比推理能力刚合格，需要比常人花更多的时间在类比推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "发挥想象在推理中的作用：必须丰富自己的想象素材，扩大自己的知识范围。知识基础越坚实，知识面越广，就越能发挥自己的想象力。想象力越丰富就越容易提升自己的推理能力。;" + "进行创造性的训练：可以进行一些发散性思维的训练，培养自己的求知欲。");
        suggestion8.put(3, "你的类比推理能力一般，可以通过一些方法在此方面进行提升：;" + "进行创造性的训练：可以进行一些发散性思维的训练，培养自己的求知欲。;" + "培养思维的灵活性：应该多自由联想与迅速反应的训练。比如：急骤的联想或暴风雨式的联想的方法。");
        suggestion8.put(4,"你的类比推理能力较强，可以比常人花更少的时间在类比推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "培养思维的灵活性：应该多自由联想与迅速反应的训练。比如：急骤的联想或暴风雨式的联想的方法。;" + "保持良好的情绪状态：应该学会用意识去调节和控制自己的情绪和心境，使自己保持平静、轻松的情绪和心境，提高自己逻辑推理的水平和质量。");
        suggestion8.put(5,"你的类比推理能力非常强，可以花比较少的时间在类比推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "培养思维的灵活性：应该多自由联想与迅速反应的训练。比如：急骤的联想或暴风雨式的联想的方法。;" + "保持良好的情绪状态：应该学会用意识去调节和控制自己的情绪和心境，使自己保持平静、轻松的情绪和心境，提高自己逻辑推理的水平和质量。");
        QtypeInfo qtypeInfo8 = new QtypeInfo("类比推理能力","葛雷"," 类比是根据两个(或两类)思维对象间的相同或相似,从而推出它们在其它方面也相同或相似的一种思维方法。类比推理能力就是能够根据两个或两类对象有部分属性相同，从而推出它们的其他属性也相同的能力。;" +
                "类比推理能力较高的个体会具有较强的联想能力、创造性思维能力以及观察对比能力。","类比推理能力是在理解成对事物间关系的相似性的基础上，做出关于事物、事件或概念的推理的认知能力。",explain8,suggestion8);

        Map<Integer,String> explain9 = new HashMap<>();
        Map<Integer,String> suggestion9 = new HashMap<>();
        explain9.put(1,"你的矩阵推理能力测试得分为***，此项能力偏低，归纳和演绎推理能力非常弱。");
        explain9.put(2,"你的矩阵推理能力测试得分为***，此项能力刚合格，归纳和演绎推理能力比较弱。");
        explain9.put(3,"你的矩阵推理能力测试得分为***，此项能力一般，归纳和演绎推理能力一般。");
        explain9.put(4,"你的矩阵推理能力测试得分为***，此项能力强，归纳和演绎推理能力强。");
        explain9.put(5,"你的矩阵推理能力测试得分为***，此项能力超常，归纳和演绎推理能力非常强。");
        suggestion9.put(1,"你的矩阵推理能力较弱，需要比常人花更多的时间在矩阵推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "多角度的理解问题：学会“同中求异”的思考习惯：将相同事物进行比较，找出其中在某个方面的不同之处，将相同的事物区别开来。同时还必须学会“异中求同”的思考习惯：对不同的事物进行比较，找出其中在某个方面的相同之处，将不同的事物归纳起来;" + "多进行因果思考：面对生活中的事情的时候多去探索原因。");
        suggestion9.put(2,"你的矩阵推理能力刚合格，需要比常人花更多的时间在矩阵推理能力提升方面，可以通过一些方法在此方面进行提升：;" + "多角度的理解问题：学会“同中求异”的思考习惯：将相同事物进行比较，找出其中在某个方面的不同之处，将相同的事物区别开来。同时还必须学会“异中求同”的思考习惯：对不同的事物进行比较，找出其中在某个方面的相同之处，将不同的事物归纳起来;" + "多进行因果思考：面对生活中的事情的时候多去探索原因。");
        suggestion9.put(3,"你的矩阵推理能力一般，可以通过一些方法在此方面进行提升：;" + "多角度的理解问题：学会“同中求异”的思考习惯：将相同事物进行比较，找出其中在某个方面的不同之处，将相同的事物区别开来。同时还必须学会“异中求同”的思考习惯：对不同的事物进行比较，找出其中在某个方面的相同之处，将不同的事物归纳起来。;" + "丰富有关思维的理论知识：应该多了解一些思维发展的理论知识，有意识地用理论指导自己的类比推理能力的发展。");
        suggestion9.put(4,"你的矩阵推理能力较强，可以比常人花更少的时间在矩阵推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "丰富有关思维的理论知识：应该多了解一些思维发展的理论知识，有意识地用理论指导自己的类比推理能力的发展。;" + "保持良好的情绪状态：应该学会用意识去调节和控制自己的情绪和心境，使自己保持平静、轻松的情绪和心境，提高自己矩阵推理的水平和质量。 ");
        suggestion9.put(5,"你的矩阵推理能力非常强，可以花比较少的时间在矩阵推理能力方面，如果想要进一步提升可以参考下面的方法：;" + "丰富有关思维的理论知识：应该多了解一些思维发展的理论知识，有意识地用理论指导自己的类比推理能力的发展。;" + "保持良好的情绪状态：应该学会用意识去调节和控制自己的情绪和心境，使自己保持平静、轻松的情绪和心境，提高自己矩阵推理的水平和质量。");
        QtypeInfo qtypeInfo9 = new QtypeInfo("矩阵推理能力","葛雷","矩阵推理测试是测量流体智力很好的测验形式，使用较广的瑞文测验使用的就是矩阵推理测验。;" +
                "矩阵推理能力较强的个体可能会在以后的学习和工作中表现出较好的学业成就和工作表现。","矩阵推理测试是测量流体智力很好的测验形式，使用较广的瑞文测验使用的就是矩阵推理测验。",explain9,suggestion9);

        Map<String,Object> qtypes4 = new HashMap<>();
        qtypes4.put("name","推理能力");
        qtypes4.put("introduce","推理是从已知的或假设的事实中引出结论的过程，推理能力是智力的重要成分，是思维的一种形式，同时也参与知觉、记忆、表象、问题解决等一系列智力活动。推理是形式逻辑的核心内容，它是由一个或一些已知判断得出一个新判断的思维形式。");
        qtypes4.put("6",qtypeInfo7);
        qtypes4.put("8",qtypeInfo8);
        qtypes4.put("9",qtypeInfo9);


        Map<String,Object> intelligence = new HashMap<>();
        intelligence.put("name","智力因素");
        intelligence.put("1",qtypes1);
        intelligence.put("2",qtypes2);
        intelligence.put("3",qtypes3);
        intelligence.put("4",qtypes4);

        Map<Integer,String> explain10 = new HashMap<>();
        Map<Integer,String> suggestion10 = new HashMap<>();
        explain10.put(1,"你的情绪识别测试得分为***，此项能力偏低，知觉、辨认和表达情绪的能力非常弱。;" + "1.你很难从自己的生理状态、情绪体验和思想中辨认自己的情绪，也就是说，你大部分情况下不明白自己处于何种情绪。;" + "2.你很难感知到他人言行中的情绪信息和线索，他人的面部表情和语言传达出来的情绪对你来说很难看懂。所以当你的同学朋友遇到挫折而出现情绪低落或沮丧时，你不能识别他的情绪并给予鼓励或安慰。;" + "3.你无法区分他人情绪表达的准确性和诚实性，也就是说，你很难理解他们表象表情下的深层情绪，举个例子，如果有人微笑着用嘲讽的语气和你说话，你可能只能接受到微笑传达的情绪，而不能发现他人隐含着的嘲讽情绪。");
        explain10.put(2,"你的情绪识别测试得分为***，此项能力刚好合格，知觉、辨认和表达情绪的能力不太强。;" + "1.你不太能从自己的生理状态、情绪体验和思想中辨认自己的情绪，你在很多情况下不知道自己处于何种情绪。;" + "2.你比较难辨认他人的情绪，不太能感知到他人言行中的情绪信息和线索。当你的同学朋友遇到挫折而出现情绪低落或沮丧时，你很难快速准确地识别他的情绪并给予鼓励或安慰。;" + "3.你几乎不能区分他人情绪表达的准确性和诚实性，较难理解他人表象表情下的深层情绪。所以很多时候，你会发现你不理解周围其他人正在传达的情绪。");
        explain10.put(3,"你的情绪识别测试得分为***，此项能力处于中等，知觉、辨认和表达情绪的能力一般。;" + "1.你一定程度上可以从自己的生理状态、情绪体验和思想中辨认自己的情绪，知道自己处于何种情绪。;" + "2.你在一定程度上能感知到他人言行中的情绪信息和线索, 进而把握他人深层次的需求,  并在此基础给予满足。当你的同学朋友遇到挫折而出现情绪低落或沮丧时,你能尝试给予鼓励或安慰。;" + "3.你不太能区分他人情绪表达的准确性和诚实性，在很多时候你只能隐约地觉察表层情绪下可能有深层情绪，但是很难识别深层情绪是何种情绪。");
        explain10.put(4,"你的情绪识别测试得分为***，此项能力较为优秀，知觉、辨认和表达情绪的能力较强。;" + "1.你可以很好地从自己的生理状态、情绪体验和思想中辨认自己的情绪，对自己所处的情绪有明确的认知。;" + "2.你能较为敏锐感知到他人言行中的情绪信息和线索, 有效把握他人深层次的需求,并在此基础给予满足。当你的同学朋友遇到挫折而出现情绪低落或沮丧时,你能比较及时给予鼓励或安慰。;" + "3.你在一定程度上能区分他人情绪表达的准确性和诚实性，能够挖掘他人深层次的情绪。所以，你能得到善解人意的称赞，获得他人的良好印象，拥有比较好的人际关系。");
        explain10.put(5,"你的情绪识别测试得分为***，此项能力超常，知觉、辨认和表达情绪的能力非常强。;" + "1.你可以很好地从自己的生理状态、情绪体验和思想中辨认自己的情绪，对自己所处的情绪有非常明确的认知。;" + "2.你能非常敏锐地感知到他人言行中的情绪信息和线索, 有效把握他人深层次的需求,并在此基础给予满足。当你的同学朋友遇到挫折而出现情绪低落或沮丧时,你能非常及时给予鼓励或安慰。;" + "3.你能比较好地区分他人情绪表达的准确性和诚实性，能比较快而准确的体会别人的“言下之意”表层情绪下的深层情绪。所以，你能得到善解人意的称赞，获得他人的良好印象，拥有比较好的人际关系。");
        suggestion10.put(1,"你的情绪识别能力较弱，需要比常人花更多的时间在情绪识别能力提升方面，可以通过一些方法在此方面进行提升：;" + "表情训练：收集表情的资料，对表情的特征、代表情绪等进行训练，可以提升个体的情绪识别能力；;" + "即时反馈：在与他人进行互动时，通过交流询问等方式，获得对他人情绪识别的即时反馈，修正自己的情绪识别经验和方法；");
        suggestion10.put(2,"你的情绪识别能力刚合格，需要比常人花更多的时间在情绪识别能力提升方面，可以通过一些方法在此方面进行提升：;" + "表情训练：收集表情的资料，对表情的特征、代表情绪等进行训练，可以提升个体的情绪识别能力；;" + "即时反馈：在与他人进行互动时，通过交流询问等方式，获得对他人情绪识别的即时反馈，修正自己的情绪识别经验和方法；");
        suggestion10.put(3,"你的情绪识别能力一般，可以通过一些方法在此方面进行提升：;" + "即时反馈：在与他人进行互动时，通过交流询问等方式，获得对他人情绪识别的即时反馈，修正自己的情绪识别经验和方法；;" + "积极倾听：认真倾听对方的讲话，观察对方说话的表情、身体语言和语言，从中发现对方想要传达的情绪；");
        suggestion10.put(4,"你的情绪识别能力较强，可以比常人花更少的时间在情绪识别能力方面，如果想要进一步提升可以参考下面的方法：;" + "情绪阅读：平时注意观察他人的面部表情等身体语言，结合当时的情境、行为反应等，获取他人情绪的线索，积累更多的情绪资料；;" + "积极倾听：认真倾听对方的讲话，观察对方说话的表情、身体语言和语言，从中发现对方想要传达的情绪；");
        suggestion10.put(5,"你的情绪识别能力非常强，可以花比较少的时间在情绪识别能力方面，如果想要进一步提升可以参考下面的方法：;" + "情绪阅读：平时注意观察他人的面部表情等身体语言，结合当时的情境、行为反应等，获取他人情绪的线索，积累更多的情绪资料；;" + "积极倾听：认真倾听对方的讲话，观察对方说话的表情、身体语言和语言，从中发现对方想要传达的情绪；");
        QtypeInfo qtypeInfo10 = new QtypeInfo("情绪识别能力","姜莉","情绪识别能力是指知觉、辨认和表达情绪的能力，具体来说，就是从自己的生理状态 、情感体验和思想中辨认和表达情绪的能力；从他人、艺术活动、语言中辨认和表达情绪的能力；准确表达情绪和相关需要的能力；区分情感表达的准确性和诚实性的能力。;" +
                "情绪识别能力高的个体具有更强的情绪认知能力，能敏锐感知到他人言行中的情绪信息和线索, 有效把握他人深层次的需求,并在此基础给予满足。当他人遇到挫折而出现情绪低落或沮丧时,情绪识别能力高的个体能及时给予鼓励或安慰。所以，情绪识别能力高的个体更容易得到善解人意的称赞，获得他人的良好印象，拥有更好的人际关系。","情绪识别能力是指知觉、辨认和表达情绪的能力。",explain10,suggestion10);

        Map<Integer,String> explain11 = new HashMap<>();
        Map<Integer,String> suggestion11 = new HashMap<>();
        explain11.put(1,"你的情绪理解测试得分为***，此项能力偏低，对情绪的理解和分析能力非常弱。;" + "1.你很难理解词语与情绪本身的关系，比如区分“喜欢”和“爱”所代表的情绪之间的差异对你来说非常难。;" + "2.你很难理解复杂的情绪，比如“憎恨”包含其他哪些情绪。;" + "3.你很难理解情绪所传递的意义，不能推测情绪产生的情境，比如伤心一般出现在失去这样的情境中。所以在生活中，你不能根据他人所在的情境理解他的情绪，较难与其他个体共情，与他人合作也会有非常多的困难。");
        explain11.put(2,"你的情绪理解测试得分为***，此项能力刚好合格，对情绪的理解和分析能力不太强。;" + "1.你比较难理解词语与情绪本身的关系，比如区分“喜欢”和“爱”对你来说比较难。;" + "2.你比较难理解复杂的情绪，比如“憎恨”包含其他哪些情绪。;" + "3.你也比较难理解情绪所传递的意义，比较难推测情绪产生的情境。所以在生活中，你不太能根据他人所在的情境理解他的情绪，比较难与其他个体共情，与他人合作也会有比较多的困难。");
        explain11.put(3,"你的情绪理解测试得分为***，此项能力处于中等，对情绪的理解和分析能力一般。;" + "1.你可以理解比较简单的词语与情绪本身的关系，比如你可以区分“喜欢”和“爱”，但是比较难区分“讨厌”与“厌恶”。;" + "2.你能够理解比较简单的复杂情绪，比如“憎恨”包含哪些情绪。;" + "3.在某些情境下你能够理解情绪所传递的意义，推测情绪产生的情境，但是并不完全准确。在生活中，你可以根据他人所在的情境理解他的情绪，在一定程度上与其他个体共情，与他人合作。");
        explain11.put(4,"你的情绪理解测试得分为***，此项能力较为优秀，对情绪的理解和分析能力较强。;" + "1.你能够比较好的理解词语与情绪本身的关系，比如区分“喜欢”和“爱”对你来说比较容易。;" + "2.你能够较好地理解复杂的情绪，比如“憎恨”包含哪些情绪。;" + "3.你能够理解大部分情绪所传递的意义，能够快速推测情绪产生的情境，准确理解他人在某情境下会产生的情绪。所以在生活中，你能够较好地根据他人所在的情境理解他的情绪，与其他个体共情，较好地与他人合作。");
        explain11.put(5,"你的情绪理解测试得分为***，此项能力超常，对情绪的理解和分析能力非常强。;" + "1.你能够很好地理解词语与情绪本身的关系，比如区分“喜欢”和“爱”对你来说非常容易；;" + "2.你能够很好地理解情绪所传递的意义，同时很好地理解复杂的情绪，比如“憎恨”包含哪些情绪。;" + "3.你能够非常准确的理解情绪所传递的意义，快速地推测出情绪产生的情境。在生活中，你能够很好地根据他人所在的情境理解他的情绪，很容易地与其他个体共情，与他人合作。");
        suggestion11.put(1,"你的情绪理解能力较弱，需要比常人花更多的时间在情绪理解能力提升方面，可以通过一些方法在此方面进行提升：;" + "情绪追踪：每天都注意自己的情绪反应，无时无刻不在观察自己的情绪。当它发生变化时，就询问自己：刚才发生了什么？是什么让我产生刚才的情绪？对自己情绪的追踪能够让自己更明确情绪产生的情境。;" + "自我反思：思考自己产生的情绪、相应的情境及当时的行为反应，比如悲伤可能让自己四肢沉重，会让自己不知道做什么，从而中断当前工作，在对自己的情绪做出反思后，能更好的理解他人的情绪。");
        suggestion11.put(2,"你的情绪理解能力刚合格，需要比常人花更多的时间在情绪理解能力提升方面，可以通过一些方法在此方面进行提升：;" + "情绪追踪：每天都注意自己的情绪反应，无时无刻不在观察自己的情绪。当它发生变化时，就询问自己：刚才发生了什么？是什么让我产生刚才的情绪？对自己情绪的追踪能够让自己更明确情绪产生的情境。;" + "自我反思：思考自己产生的情绪、相应的情境及当时的行为反应，比如悲伤可能让自己四肢沉重，会让自己不知道做什么，从而中断当前工作，在对自己的情绪做出反思后，能更好的理解他人的情绪。");
        suggestion11.put(3,"你的情绪理解能力一般，可以通过一些方法在此方面进行提升：;" + "情绪追踪：每天都注意自己的情绪反应，无时无刻不在观察自己的情绪。当它发生变化时，就询问自己：刚才发生了什么？是什么让我产生刚才的情绪？对自己情绪的追踪能够让自己更明确情绪产生的情境。;" + "自我反思：思考自己产生的情绪、相应的情境及当时的行为反应，比如悲伤可能让自己四肢沉重，会让自己不知道做什么，从而中断当前工作，在对自己的情绪做出反思后，能更好的理解他人的情绪。");
        suggestion11.put(4,"你的情绪理解能力较强，可以比常人花更少的时间在情绪理解能力方面，如果想要进一步提升可以参考下面的方法：;" + "情绪追踪：每天都注意自己的情绪反应，无时无刻不在观察自己的情绪。当它发生变化时，就询问自己：刚才发生了什么？是什么让我产生刚才的情绪？对自己情绪的追踪能够让自己更明确情绪产生的情境。;" + "自我反思：思考自己产生的情绪、相应的情境及当时的行为反应，比如悲伤可能让自己四肢沉重，会让自己不知道做什么，从而中断当前工作，在对自己的情绪做出反思后，能更好的理解他人的情绪。");
        suggestion11.put(5,"你的情绪理解能力非常强，可以花比较少的时间在情绪理解能力方面，如果想要进一步提升可以参考下面的方法：;" + "情绪追踪：每天都注意自己的情绪反应，无时无刻不在观察自己的情绪。当它发生变化时，就询问自己：刚才发生了什么？是什么让我产生刚才的情绪？对自己情绪的追踪能够让自己更明确情绪产生的情境。;" + "自我反思：思考自己产生的情绪、相应的情境及当时的行为反应，比如悲伤可能让自己四肢沉重，会让自己不知道做什么，从而中断当前工作，在对自己的情绪做出反思后，能更好的理解他人的情绪。");
        QtypeInfo qtypeInfo11 = new QtypeInfo("情绪理解能力","姜莉","情绪理解能力是指对情绪的理解、分析的能力，具体来说，就是认识词语与情绪本身关系的能力,如喜欢与爱之间的关系；理解情绪所传递的意义的能力；理解复杂情绪的能力，如爱恨同时存在的情感, 害怕和吃惊混合而成的畏惧情感；认识情绪转换可能性的能力，如将愤怒转化为愉快,或将愤怒转化为羞愧。;" +
                "情绪理解能力高的个体更能够根据他人所在情境或者他人的语言线索理解他人的情绪，从而很好的与其他个体进行交流和人际交往。情绪理解能力高的个体因为更能够理解他人情绪，同样能够很好地共情他人，表现出对他人的尊重，更好的与他人合作。","情绪理解能力是指对情绪的理解和分析能力。",explain11,suggestion11);

        Map<String,Object> qtypes5 = new HashMap<>();
        qtypes5.put("name","情绪智力");
        qtypes5.put("introduce","情绪智力是觉知和表达情绪 、情绪促进思维 ,理解和分析情绪、以及调控自;" + "己与他人情绪的能力。主要包括情绪识别、情绪理解、情绪使用、情绪管理四个维度，本系统主要测量前两个维度。");
        qtypes5.put("13",qtypeInfo10);
        qtypes5.put("14",qtypeInfo11);

        Map<Integer,String> explain12 = new HashMap<>();
        Map<Integer,String> suggestion12 = new HashMap<>();
        explain12.put(1,"你不擅于控制、管理和调节自身的冲动，行为比较冲动，不负责任；马虎，懒散；敷衍，缺乏自律。");
        explain12.put(2,"你不是非常擅于控制、管理和调节自身的冲动，行为比较冲动，不负责任；比较马虎，比较懒散；做事比较敷衍，自律能力不高。");
        explain12.put(3,"你在一定程度上能够控制、管理和调节自身的冲动，有时比较认真负责，但是也常常行为冲动，马虎懒散。");
        explain12.put(4,"你能够比较好的控制、管理和调节自身冲动，做事认真负责；理智谨慎，勤勉努力；可靠，值得信赖。");
        explain12.put(5,"你非常擅于控制、管理和调节自身冲动，做事非常认真负责；理智谨慎，非常勤勉努力；非常可靠，值得信赖。");
        suggestion12.put(1,"正确的认识自己和评价自己：接受现实的自我，选择适当的目标，寻求良好的方法，不作自不量力之事，才可创造理想的自我。欣然接受自己，可避免心理冲突和情绪焦虑。;" + "面对现实，适应环境：在力不能及的情况下，又能另择目标或重选方法以适应现实环境 。;" + "建立良好的人际关系：热情友好的与他人进行交往。");
        QtypeInfo qtypeInfo12 = new QtypeInfo("责任感","葛雷","责任感指我们控制、管理和调节自身冲动的方式，评估个体在目标导向行为上的组织、坚持和动机。它把可信赖的、讲究的个体和懒散的、马虎的个体作比较。;" +
                "这个维度得分较高的个体做事认真负责，理智谨慎，勤勉努力，可靠，值得信赖。","你的**非常高，**较高，**中等，**合格，**偏低。",explain12,suggestion12);


        Map<Integer,String> explain13 = new HashMap<>();
        Map<Integer,String> suggestion13 = new HashMap<>();
        explain13.put(1,"你很可能生活中保守固执，兴趣单一，灵活性不足，喜欢固定不变的事物，不喜欢求异图变。");
        explain13.put(2,"你可能比较保守固执、兴趣比较单一，灵活性不足，比较喜欢固定不变的事物，不喜欢求异图变。");
        explain13.put(3,"你在生活中可能表现为中规中矩，按部就班，比较现实，喜欢稳定的生活，不太喜欢求异图变。");
        explain13.put(4,"你兴趣广泛，经验丰富；具有强烈的好奇心，求知欲比较强；喜欢接受新事物，具有弹性。");
        explain13.put(5,"你兴趣非常广泛，经验非常丰富；具有强烈的好奇心，求知欲非常强；非常喜欢接受新事物，具有弹性。");
        QtypeInfo qtypeInfo13 = new QtypeInfo("开放性","葛雷","开放性描述一个人的认知风格。这个维度将那些好奇的、新颖的、非传统的以及有创造性的个体与那些传统的、无艺术兴趣的、无分析能力的个体做比较。;" +
                "这一维度得分较高的个体偏爱抽象思维，兴趣广泛，富有想象力，重视审美经历，愿意去尝试新鲜的事物，较强的求知欲。","你的**非常高，**较高，**中等，**合格，**偏低。",explain13,suggestion13);


        Map<Integer,String> explain14 = new HashMap<>();
        Map<Integer,String> suggestion14 = new HashMap<>();
        explain14.put(1,"你非常容易表现出情绪的波动，平时易焦虑，情绪不稳，忧心忡忡；经常感到敌对，压抑，寂寞，甚至会有罪恶感等。");
        explain14.put(2,"你易表现出情绪的波动。你比较容易焦虑，情绪不稳，忧心忡忡；偶尔会感到敌对，压抑，寂寞，甚至会有罪恶感等。");
        explain14.put(3,"你有时比较冲动，情绪不容易控制；偶尔遇到事情会焦虑不安。");
        explain14.put(4,"你比较少表现情绪的波动，情绪比较稳定，性情比较温和，比较善于自我控制。");
        explain14.put(5,"你几乎不会表现情绪的波动，情绪稳定，性情温和，善于自我控制。");
        QtypeInfo qtypeInfo14 = new QtypeInfo("情绪稳定","葛雷","情绪稳定是指个体的情绪波动状态。;" +
                "在这一维度上得分较高的个体情绪比较稳定，他们通常是性情温和，善于自我控制的。","你的**非常高，**较高，**中等，**合格，**偏低。",explain14,suggestion14);

        Map<Integer,String> explain15 = new HashMap<>();
        Map<Integer,String> suggestion15 = new HashMap<>();
        explain15.put(1,"你非常少表现出对于问题进行探索研究的人格特征，不喜欢深入思考问题，惰性较强，被动；满足于现状，墨守成规。");
        explain15.put(2,"你比较少表现出对于问题进行探索研究的人格特征，比较不喜欢深入思考问题，惰性较强，比较被动，比较满足于现状，墨守成规。");
        explain15.put(3,"你在一定程度表现出对于问题进行探索研究的人格特征，具有较好的思维能力，但是不太愿意深入思考，探索精神和创新意识一般。");
        explain15.put(4,"你会表现出对于问题进行探索研究的人格特征，喜欢和享受思考力，好奇心强，爱刨根问底，有探索和创新意识。");
        explain15.put(5,"你非常容易会表现出对于问题进行探索研究的人格特征，非常喜欢和享受思考力，好奇心非常强，爱刨根问底，非常有探索和创新意识。");
        QtypeInfo qtypeInfo15 = new QtypeInfo("探究性","葛雷","探究性描绘一个人对于问题进行探索研究的深度。;" +
                "在这一维度上得分较高的个体他们喜欢和享受思考，好奇心强，爱刨根问底，有探索和创新意识。","你的**非常高，**较高，**中等，**合格，**偏低。",explain15,suggestion15);

        Map<Integer,String> explain16 = new HashMap<>();
        Map<Integer,String> suggestion16 = new HashMap<>();
        explain16.put(1,"你的依赖性强，常常放弃个人的主见，附合众议，很多时候你需要别人的支持来维持自信。");
        explain16.put(2,"你的依赖性比较强，有时放弃个人的主见，附合众议；有时需要别人的支持来维持自信。");
        explain16.put(3,"你做事情不够果断，有依赖性；常常希望与别人一起完成工作，独立工作和解决问题的能力一般。");
        explain16.put(4,"你能够不依附他人独自完成事情。你自立自强，当机立断，能够自作主张，独自完成工作。");
        explain16.put(5,"你习惯于不依附他人独自完成事情。你自立自强，当机立断，能够自作主张，独自完成工作。");
        QtypeInfo qtypeInfo16 = new QtypeInfo("独立性","葛雷","独立性是用来描述个体不依附他人完成事情的可能。;" +
                "在这一维度上得分较高的个体往往自立自强，当机立断，能够自作主张，独自完成工作。","你的**非常高，**较高，**中等，**合格，**偏低。",explain16,suggestion16);

        Map<Integer,String> explain17 = new HashMap<>();
        Map<Integer,String> suggestion17 = new HashMap<>();
        explain17.put(1,"你几乎不太表现出亲近人的、有同情心的、信任他人的、宽大的、心软的人格特征，常常怀疑他人，比较刻薄，十分武断教条，带有偏见，喜欢支配别人。");
        explain17.put(2,"你比较少表现出亲近人的、有同情心的、信任他人的、宽大的、心软的人格特征，有时会怀疑他人，比较刻薄；平时比较武断，教条；常常带有偏见，比较喜欢支配别人。");
        explain17.put(3,"你偶尔表现出亲近人、宽大、心软的个人特征，有时有同情心，信任他人，但有时比较直率，不太谦虚，会让一些人感觉不舒服。");
        explain17.put(4,"你有时表现出亲近人的、有同情心的、信任他人的、宽大的、心软的人格特征，待人友好，乐于助人，善解人意，并且谦虚，直率，会同情他人。");
        explain17.put(5,"你表现出亲近人的、有同情心的、信任他人的、宽大的、心软的人格特征，待人非常友好，乐于助人，善解人意，平时非常谦虚，直率，同情他人。");
        QtypeInfo qtypeInfo17 = new QtypeInfo("宜人性","葛雷","宜人性是考察个体对其他人所持的态度，这些态度一方面包括亲近人的、有同情心的、信任他人的、宽大的、心软的，另一方面包括敌对的、愤世嫉俗的、爱摆布人的、复仇心重的、无情的。;" +
                "这一维度得分较高的个体他们往往表现出友好，乐于助人，善解人意，谦虚，直率，同情他人的特点。","你的**非常高，**较高，**中等，**合格，**偏低。",explain17,suggestion17);


        Map<Integer,String> explain18 = new HashMap<>();
        Map<Integer,String> suggestion18 = new HashMap<>();
        explain18.put(1,"你很难通过一定的语言、文字或肢体动作、表情等表达自己，对他人的言行、内心不敏感，难以了解他人的需求；并且难以建立良好的人际关系和合作关系。");
        explain18.put(2,"你通过一定的语言、文字或肢体动作、表情等表达手段将某种信息传递的能力比较弱，对他人的言行、内心比较不敏感，不容易了解他人的需求；不容易建立良好的人际关系和合作关系。");
        explain18.put(3,"你能够通过一定的语言、文字或肢体动作、表情等表达手段将某种信息传递，愿意与人交往，能够与他人的合作；沟通和人际交往能力不是很突出，表现一般。");
        explain18.put(4,"你擅于通过一定的语言、文字或肢体动作、表情等表达手段将某种信息传递，能够理解他人，沟通能力强，人际关系良好，可与他人进行有效合作。");
        explain18.put(5,"你非常擅于通过一定的语言、文字或肢体动作、表情等表达手段将某种信息传递，非常能够理解他人，沟通能力非常强，人际关系良好，很容易与他人进行有效合作。");
        QtypeInfo qtypeInfo18 = new QtypeInfo("人际交往","葛雷","人际交往是指个体通过一定的语言、文字或肢体动作、表情等表达手段将某种信息传递给其他个体的过程。\n" +
                "这一维度得分较高的个体他们往往能够理解他人，沟通能力强，人际关系良好，可与他人进行有效合作。","你的**非常高，**较高，**中等，**合格，**偏低。",explain18,suggestion18);

        Map<String,Object> qtypes6 = new HashMap<>();
        qtypes6.put("name","人格特征描述");
        qtypes6.put("introduce","人格特质指的是在不同的时间与不同的情境中保持相对一致的行为方式的一种倾向。人格测试问卷测试的是个体的人格特征，量表共70个题对，7个分量表。包括责任感、开放性、情绪稳定、探究性、独立性、宜人性、人际交往等7个维度。");
        qtypes6.put("1",qtypeInfo14);
        qtypes6.put("2",qtypeInfo12);
        qtypes6.put("3",qtypeInfo15);
        qtypes6.put("4",qtypeInfo13);
        qtypes6.put("5",qtypeInfo16);
        qtypes6.put("6",qtypeInfo17);
        qtypes6.put("7",qtypeInfo18);

        Map<Integer,String> explain19 = new HashMap<>();
        Map<Integer,String> suggestion19 = new HashMap<>();
        explain19.put(0,"你的父母对你的教养方式是\"权威型\"，他们能主动关爱你，能够耐心的倾听你的述说，而且能晓之以理、动之以情，激励你自我成长；但是，他们对你也有过多的要求和控制，会拿过高的标准要求你。");
        explain19.put(1,"你父亲对你的教养方式是\"权威型\"，他能主动关爱你，能够耐心的倾听你的述说，而且能晓之以理、动之以情，激励你自我成长；但是，他们对你也有过多的要求和控制，会拿过高的标准要求你。");
        explain19.put(2,"你母亲对你的教养方式是\"权威型\"，她能主动关爱你，能够耐心的倾听你的述说，而且能晓之以理、动之以情，激励你自我成长；但是，他们对你也有过多的要求和控制，会拿过高的标准要求你。");
        QtypeInfo qtypeInfo19 = new QtypeInfo("权威型","葛雷","关怀是指父母对于子女的关心以及情感温暖程度。;" +
                "这个维度得分较高的个体往往在成长过程中受到了父母较高的关心程度，获得了较强的家庭安全感。同时这些个体也表现出来情绪稳定、兴趣广泛、富有同情心的特征。","关怀是指父母对于子女的关心以及情感温暖程度。",explain19,suggestion19);

        Map<Integer,String> explain20 = new HashMap<>();
        Map<Integer,String> suggestion20 = new HashMap<>();
        explain20.put(0,"你的父母对你的教养方式是\"专制型\"，他们会拿自己的标准来要求你，而没有意识到过高的要求对你的个性是一种变相的扼杀；他们不能接受你的反馈，对你缺乏热情和关爱，要求你无条件服从，不能及时鼓励和表扬你。");
        explain20.put(1,"你父亲对你的教养方式是\"专制型\"，他会拿自己的标准来要求你，而没有意识到过高的要求对你的个性是一种变相的扼杀；他不能接受你的反馈，对你缺乏热情和关爱，要求你无条件服从，不能及时鼓励和表扬你。");
        explain20.put(2,"你母亲对你的教养方式是\"专制型\"，她会拿自己的标准来要求你，而没有意识到过高的要求对你的个性是一种变相的扼杀；她不能接受你的反馈，对你缺乏热情和关爱，要求你无条件服从，不能及时鼓励和表扬你。");
        QtypeInfo qtypeInfo20 = new QtypeInfo("专制型","葛雷","冷漠/拒绝是指父母在日常生活中对于子女的漠视程度。;" +
                "这个维度得分较高的个体往往具有较低的安全感、幸福感。表现出情绪不稳、冷漠、倔强而逆反的特征。","冷漠/拒绝是指父母在日常生活中对于子女的漠视程度。",explain20,suggestion20);

        Map<Integer,String> explain21 = new HashMap<>();
        Map<Integer,String> suggestion21 = new HashMap<>();
        explain21.put(0,"你的父母对你的教养方式是\"民主型\"，他们对充满了无尽的期望和爱，给予你无条件的爱，又较少的对你进行过度控制。");
        explain21.put(1,"你父亲对你的教养方式是\"民主型\"，他对充满了无尽的期望和爱，给予你无条件的爱，又较少的对你进行过度控制。");
        explain21.put(2,"你母亲对你的教养方式是\"民主型\"，她对充满了无尽的期望和爱，给予你无条件的爱，又较少的对你进行过度控制。");
        QtypeInfo qtypeInfo21 = new QtypeInfo("民主型","葛雷","过度保护是指有些家长不遗余力地将孩子的日常事情都由自己代替的现象。;" +
                "这一维度得分较高的个体往往会表现出依赖他人、胆小、缺乏自信的特征。","过度保护是指有些家长不遗余力地将孩子的日常事情都由自己代替的现象。",explain21,suggestion21);

        Map<Integer,String> explain22 = new HashMap<>();
        Map<Integer,String> suggestion22 = new HashMap<>();
        explain22.put(0,"你的父母对你的教养方式是\"忽视型\"，他们不关心你的成长，不会对你提出要求和行为标准，对你较为冷漠，缺少对你的教育和爱。");
        explain22.put(1,"你父亲对你的教养方式是\"忽视型\"，他不关心你的成长，不会对你提出要求和行为标准，对你较为冷漠，缺少对你的教育和爱。");
        explain22.put(2,"你母亲对你的教养方式是\"忽视型\"，她不关心你的成长，不会对你提出要求和行为标准，对你较为冷漠，缺少对你的教育和爱。");
        QtypeInfo qtypeInfo22 = new QtypeInfo("忽视型","葛雷","自主性是指孩子按自己意愿行事的动机、能力或特性。;" +
                "这一维度得分较高的个体往往会表现出独立、勇敢、有较强的攻击性的特征。","自主性是指孩子按自己意愿行事的动机、能力或特性。",explain22,suggestion22);


        Map<String,Object> qtypes7 = new HashMap<>();
        qtypes7.put("name","父母教养方式");
        qtypes7.put("suggestion","提升父母对子女的信心：父母要对自己的孩子有充分的信心\"赏\n" + "识孩子的一切努力\"赏识孩子所取得的点滴进步\"甚至要学会赏\n" + "识孩子的失败\"\n" + "让孩子感受到父母永远是他的后盾。;\n" +
                "给孩子适当的磨难与挫折承受力的训练：家长要学会放手，要让孩子自己去碰壁去成长。;\n" +
                "给孩子足够的爱：给予孩子足够的关怀，给还在创造一个具有安全感的家庭氛围。\n" + "重视和睦家庭的建立，营造良好的家庭气氛\n");
        qtypes7.put("introduce","父母教养方式是指在家庭生活中以亲子关系为中心的,父母在抚养子女的日常活动中所表现出来的一种对待孩子的固定的行为模式和行为倾向。它由父母的教养态度决定并受父母的个性、教养信念和行为习惯的影响,是其教养观念和教养行为的综合体。父母教养方式分为关爱和控制两个维度，根据高低可以分为权威型、专制型、溺爱型、忽视型四种。");
        qtypes7.put("quanwei",qtypeInfo19);
        qtypes7.put("zhuanzhi",qtypeInfo20);
        qtypes7.put("minzhu",qtypeInfo21);
        qtypes7.put("hushi",qtypeInfo22);

        Map<String,Object> nonIntelligence = new HashMap<>();
        nonIntelligence.put("name","非智力因素");
        nonIntelligence.put("1",qtypes5);
        nonIntelligence.put("2",qtypes6);
        nonIntelligence.put("family",qtypes7);

        Map<String,Map<String,Object>> data = new HashMap<>();
        data.put("1",intelligence);
        data.put("2",nonIntelligence);

        return data;
    }

    class QtypeInfo{
        private String name;
        private String author;
        private String introduce;
        private String explainFirst;
        private Map<Integer,String> explainSecond;
        private Map<Integer,String> suggestion;

        public QtypeInfo(String name, String author, String introduce, String explainFirst, Map<Integer, String> explainSecond, Map<Integer, String> suggestion) {
            this.name = name;
            this.author = author;
            this.introduce = introduce;
            this.explainFirst = explainFirst;
            this.explainSecond = explainSecond;
            this.suggestion = suggestion;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getExplainFirst() {
            return explainFirst;
        }

        public void setExplainFirst(String explainFirst) {
            this.explainFirst = explainFirst;
        }

        public Map<Integer, String> getExplainSecond() {
            return explainSecond;
        }

        public void setExplainSecond(Map<Integer, String> explainSecond) {
            this.explainSecond = explainSecond;
        }

        public Map<Integer, String> getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(Map<Integer, String> suggestion) {
            this.suggestion = suggestion;
        }
    }


}
