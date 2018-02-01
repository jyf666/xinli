package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.vo.ScoreVo;
import models.vo.TestVo;
import models.vo.TestpaperVo;
import models.vo.UserVo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import persistence.SearchFilter;
import play.libs.Json;
import scala.collection.parallel.mutable.ParArray;
import scala.util.parsing.json.JSONObject;
import utils.FileUtils;
import utils.SystemConstant;
import utils.enums.DimensionEnum;
import utils.enums.FamilyDimensionEnum;

import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wangbin on 2015/11/26.
 */
@Service
public class AdmissionsOrgReportService {

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
    @Autowired
    private EduExperienceDao eduExperienceDao;
    @Autowired
    private TestpaperNormDao testpaperNormDao;

    private JsonNode descriptionData;

    /**
     * 获取招生机构的pdf数据
     * @param orgCode
     * @return
     */
    public  Map<String,Object> getOrgPdfData(Integer orgCode) throws MalformedURLException {
        this.getDescriptionData();
        Map<String,Object> pdfData = new HashMap<>();

        List<Questiontype> questiontypes = questionTypeDao.findByOrgCode(orgCode);
        List<UserVo> userVos = userDao.findUserVoByOrgCodeHaveScore(orgCode);
        Map<Integer, List<ScoreVo>> scoreVosMap = new HashMap<>();
        Map<Integer, List<Score>> scoresMap =  new HashMap<>();
        for (UserVo userVo: userVos) {
            scoreVosMap.put(userVo.getUid(), scoreDao.findScoreVoByUidAndOrgCode(userVo.getUid(), orgCode));
            scoresMap.put(userVo.getUid(),  scoreDao.findAll(SearchFilter.eq("orgCode", orgCode), SearchFilter.eq("uid", userVo.getUid())));
        }

        List<Map> userScoreList = getUserInfos(orgCode, userVos, scoreVosMap, questiontypes, scoresMap);
        List userCards = new ArrayList<>();
        pdfData.put("scoreList", userScoreList);

        for (Map<String, List> userScores: userScoreList){
            List<List> userScoresList = userScores.get("scores");
            for(List userScore: userScoresList){
                for (UserVo userVo: userVos) {
                    if (userVo.getAccount().equals(userScore.get(0)))
                        userCards.add(getUserCard(orgCode, userScore, userVo, scoreVosMap, questiontypes, userVos, scoresMap));
                }
            }
        }
        pdfData.put("userCards", userCards);

        List absentUsers = getAbsentUsers(orgCode);
        pdfData.put("absentUsers", absentUsers);
        return pdfData;
    }

    /**
     * 获取考生能力卡
     * @param orgCode
     * @param userScore
     * @param userVo
     * @return
     */
    private HashMap getUserCard(Integer orgCode, List userScore, UserVo userVo, Map<Integer, List<ScoreVo>> scoreVosMap, List<Questiontype> questiontypes, List<UserVo> userVos, Map<Integer, List<Score>> scoresMap){
        List<Integer> intelligence = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION);
            add(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
            add(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
            add(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
            add(SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
            add(SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
            add(SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
            add(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
            add(SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
        }};
        List<Integer> creativity = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
        }};
        List<Integer> emotion = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
            add(SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
        }};
        List<Integer> personality = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_PERSONALITY);
        }};
        boolean intelliIndicator = false;
        boolean creativityIndicator = false;
        boolean emotionIndicator = false;
        boolean personalityIndicator = false;
        for (Questiontype questiontype:questiontypes){
            if (intelligence.contains(questiontype.getId()) && intelliIndicator == false) {
                intelliIndicator = true;
            }
            if (creativity.contains(questiontype.getId()) && creativityIndicator == false) {
                creativityIndicator = true;
            }
            if (emotion.contains(questiontype.getId()) && emotionIndicator == false) {
                emotionIndicator = true;
            }
            if (personality.contains(questiontype.getId()) && personalityIndicator == false) {
                personalityIndicator = true;
            }
        }
        List<String> title = new ArrayList(){{
            add("account");
            add("class");
            add("name");
            add("sex");
            add("age");
        }};
        if (intelliIndicator){
            title.add("intelli");
            title.add("intelliRank");
        }
        if (creativityIndicator){
            title.add("creativity");
            title.add("creativityRank");
        }
        if (emotionIndicator){
            title.add("emotion");
            title.add("emotionRank");
        }
        if (personalityIndicator){
            title.add("personality");
        }
        Map useInfo = new HashMap(){{
            put("id", userVo.getUid());
            for (int i = 0; i < userScore.size(); i++)
                put(title.get(i), userScore.get(i));
        }};
        List<ScoreVo> scoreVos = scoreVosMap.get(userVo.getUid());

        return new HashMap(){{
            put("userInfo", useInfo);
            put("intelligence", getIntelligence(scoreVos, orgCode, questiontypes, userVos, scoresMap));
            put("personality", getPersonality(scoreVos));
        }};
    }

    /**
     * 获取人格数据，包括描述和雷达图
     * @param scoreVos
     * @return
     */
    private Map getPersonality(List<ScoreVo> scoreVos){
        List<ScoreVo> scores = new ArrayList<>();
        for (ScoreVo scoreVo: scoreVos) {
            if(scoreVo.getQtype().equals(SystemConstant.QUESTION_TYPE_PERSONALITY))
                scores.add(scoreVo);
        }

        StringBuilder starExplain = new StringBuilder();
        StringBuilder lightningExplain = new StringBuilder();
        String[] dimensions = {"neuroticism", "responsibility" , "exploratory", "openness", "independent",
                "agreeableness", "socialing"};
        String[] dimnames = {"情绪稳定", "责任感", "探究性", "开放性", "独立性", "宜人性", "人际交往"};
        String[] personailtyScore = new String[7];//存放人格的分数
        List<Integer> personalityInfo = new ArrayList(){};
        for (ScoreVo scoreVo: scoreVos){
            if (scoreVo.getQtype().equals(SystemConstant.QUESTION_TYPE_PERSONALITY)){
                personalityInfo = getDimensionlevel(scoreVo, personalityInfo);
                int i = Integer.parseInt(scoreVo.getDimension()) - 1;
                personailtyScore[i] = scoreVo.getStandardScore();
                JsonNode secondSection = this.descriptionData.get("personality").get(dimensions[i]);
                if (Double.parseDouble(scoreVo.getStandardScore()) < 55) {
                    lightningExplain.append(secondSection.findPath("low").asText() + ";");
                }
                else if (Double.parseDouble(scoreVo.getStandardScore()) > 145) {
                    starExplain.append(secondSection.findPath("high").asText() + ";");
                }
            }
        }
        String peraonalityInfo = String.join(", ", personalityInfo.toString());
        Map personailty = new HashMap<>();
        personailty.put("radarGragh", new HashMap(){{
            put("score", String.join(",", personailtyScore));
            put("dimname", String.join(",",dimnames));
        }});
        personailty.put("explain", new HashMap(){{
            put("abstract", peraonalityInfo);
            put("star", starExplain.toString());
            put("lightning", lightningExplain.toString());
        }});
        return personailty;
    }

    /**
     * 获取去机构报告智力部分的内容（玫瑰图和表格及解释）
     * @param scoreVos
     * @return
     */
    private Map getIntelligence( List<ScoreVo> scoreVos, Integer orgCode, List<Questiontype> questiontypes, List<UserVo> userVos, Map<Integer, List<Score>> scoresMap){
        List<Integer> attention = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
            add(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
        }};
        List<Integer> memory = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
            add(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
        }};
        List<Integer> criticism = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
        }};
        List<Integer> reasoning = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
            add(SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
            add(SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
        }};
        List<Integer> space = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
        }};
        List<Integer> intelligence = new ArrayList(){{
            addAll(attention);
            addAll(memory);
            addAll(criticism);
            addAll(reasoning);
            addAll(space);
        }};
        List<Integer> creativity = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
        }};
        List<Integer> emotion = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
            add(SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
        }};
        boolean intelliIndicator = false;
        boolean creativityIndicator = false;
        boolean emotionIndicator = false;
        boolean personalityIndicator = false;
        for (Questiontype questiontype:questiontypes){
            if (intelligence.contains(questiontype.getId()) && intelliIndicator == false) {
                intelliIndicator = true;
            }
            if (creativity.contains(questiontype.getId()) && creativityIndicator == false) {
                creativityIndicator = true;
            }
            if (emotion.contains(questiontype.getId()) && emotionIndicator == false) {
                emotionIndicator = true;
            }
        }
        List<Double> intelliZscore = new ArrayList<>();
        List<Double> emotionZscore = new ArrayList<>();
        List<Integer> intelliStandardScore = new ArrayList<>();
        List<Integer> emotionStandardScore = new ArrayList<>();
        int creativityScore = 0;
        Map<String, String> attentionContent = new HashMap<>();
        Map<String, String> memoryContent = new HashMap<>();
        Map<String, String> criticismContent = new HashMap<>();
        Map<String, String> reasoningContent = new HashMap<>();
        Map<String, String> spaceContent = new HashMap<>();
        Map<String, Map> attentionTable = new HashMap<>();
        Map<String, Map> memoryTable = new HashMap<>();
        Map<String, Map> criticismTable = new HashMap<>();
        Map<String, Map> reasoningTable = new HashMap<>();
        Map<String, Map> RATTable = new HashMap<>();
        Map<String, Map> spaceTable = new HashMap<>();
        Map rose = new HashMap<>();
        Map<String, String> emotionContent = new HashMap<>();
        for (ScoreVo scoreVo: scoreVos){
            if (attention.contains(scoreVo.getQtype())){
                attentionContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                attentionTable.put(scoreVo.getIntroduce(), new HashMap() {{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                intelliZscore.add(Double.parseDouble(scoreVo.getScore()));
                intelliStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
            if (memory.contains(scoreVo.getQtype())){
                memoryContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                memoryTable.put(scoreVo.getIntroduce(), new HashMap(){{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                intelliZscore.add(Double.parseDouble(scoreVo.getScore()));
                intelliStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
            if (criticism.contains(scoreVo.getQtype())){
                criticismContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                criticismTable.put(scoreVo.getIntroduce(), new HashMap(){{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                intelliZscore.add(Double.parseDouble(scoreVo.getScore()));
                intelliStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
            if (reasoning.contains(scoreVo.getQtype())){
                reasoningContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                reasoningTable.put(scoreVo.getIntroduce(), new HashMap(){{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                intelliZscore.add(Double.parseDouble(scoreVo.getScore()));
                intelliStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
            if (space.contains(scoreVo.getQtype())){
                spaceContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                spaceTable.put(scoreVo.getIntroduce(), new HashMap() {{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                intelliZscore.add(Double.parseDouble(scoreVo.getScore()));
                intelliStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
            if (creativity.contains(scoreVo.getQtype())){
                rose.put("创造力", new HashedMap(){{
                    put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                }});
                RATTable.put(scoreVo.getIntroduce(), new HashMap(){{
                    put("score", scoreVo.getStandardScore());
                    put("rank", scoreVo.getRank());
                }});
                creativityScore = Integer.parseInt(scoreVo.getStandardScore());
            }
            if (emotion.contains(scoreVo.getQtype())){
                emotionContent.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                emotionZscore.add(Double.parseDouble(scoreVo.getScore()));
                emotionStandardScore.add(Integer.parseInt(scoreVo.getStandardScore()));
            }
        }
        rose.put("智力", new HashMap(){{
            putAll(attentionContent);
            putAll(memoryContent);
            putAll(criticismContent);
            putAll(reasoningContent);
            putAll(spaceContent);
        }});
        if (emotionContent.size() != 0)
            rose.put("情绪智力", emotionContent);
        Map intelligenceContents = new HashMap<>();
        intelligenceContents.put("roseGraph", rose);
        StringBuilder classify = new StringBuilder();
        StringBuilder ability = new StringBuilder();
        StringBuilder score = new StringBuilder();
        StringBuilder rank = new StringBuilder();
        if (!attentionTable.isEmpty()){
            classify.append("注意力,");
            classify.append(Integer.toString(attentionTable.size()) + ";");
            for (Map.Entry<String, Map> entry: attentionTable.entrySet()){
                ability.append(entry.getKey() + ",");
                score.append(entry.getValue().get("score") + ",");
                rank.append(entry.getValue().get("rank") + ",");
            }
        }
        if (!memoryTable.isEmpty()){
            classify.append("记忆力,");
            classify.append(Integer.toString(memoryTable.size()) + ";");
            for (Map.Entry<String, Map> entry: memoryTable.entrySet()){
                ability.append(entry.getKey() + ",");
                score.append(entry.getValue().get("score") + ",");
                rank.append(entry.getValue().get("rank") + ",");
            }
        }
        if (!criticismTable.isEmpty()){
            classify.append("批判性思维,");
            classify.append(Integer.toString(criticismTable.size()) + ";");
            for (Map.Entry<String, Map> entry: criticismTable.entrySet()){
                ability.append(entry.getKey() + ",");
                score.append(entry.getValue().get("score") + ",");
                rank.append(entry.getValue().get("rank") + ",");
            }
        }
        if (!reasoningTable.isEmpty()){
            classify.append("推理能力,");
            classify.append(Integer.toString(reasoningTable.size()) + ";");
            for (Map.Entry<String, Map> entry: reasoningTable.entrySet()){
                ability.append(entry.getKey() + ",");
                score.append(entry.getValue().get("score") + ",");
                rank.append(entry.getValue().get("rank") + ",");
            }
        }
        if (!spaceTable.isEmpty()){
            classify.append("空间能力,");
            classify.append(Integer.toString(spaceTable.size()) + ";");
            for (Map.Entry<String, Map> entry: spaceTable.entrySet()){
                ability.append(entry.getKey() + ",");
                score.append(entry.getValue().get("score") + ",");
                rank.append(entry.getValue().get("rank") + ",");
            }
        }
        intelligenceContents.put("table", new HashMap(){{
            put("classify", classify.toString());
            put("ability", ability.toString());
            put("score", score.toString());
            put("rank", rank.toString());
        }});
        List<String> explain = new ArrayList<>();
        if (intelliIndicator) {
            if (intelliStandardScore.size()==1)
                explain.add(getExplain(intelliStandardScore.get(0), "intelligence"));
            else
                explain.add(getExplain(calScore(intelliZscore, intelligence, orgCode, userVos, scoresMap), "intelligence"));
        }
        if (creativityIndicator)
            explain.add(getExplain(creativityScore, "creativity"));
        if (emotionIndicator) {
            if (emotionStandardScore.size()==1)
                explain.add(getExplain(emotionStandardScore.get(0), "emotion"));
            else
                explain.add(getExplain(calScore(emotionZscore, emotion, orgCode, userVos, scoresMap), "emotion"));
        }
        intelligenceContents.put("explain", explain);
        return intelligenceContents;
    }

    private String getExplain(Integer score, String ability){
        JsonNode explain = this.descriptionData.get(ability);
        if (score < 70)
            return explain.get("low").asText();
        else if (score < 90)
            return explain.get("qualified").asText();
        else if (score < 110)
            return explain.get("middle").asText();
        else if (score < 130)
            return explain.get("good").asText();
        else
        return explain.get("excellent").asText();
    }

    /**
     * 获取列表学生信息和得分
     * @param orgCode
     * @return
     */
    private List getAbsentUsers(Integer orgCode) {
        //该机构提交过答案的考生
        int pagesize = 40;
        List<UserVo> userVos = userDao.findAbsentUserVoByOrgCode(orgCode);
        List<List<String>> absentUsers = new ArrayList<>();
        for (UserVo userVo: userVos){
            List<String> absentUser = new ArrayList<>();
            absentUser.add(userVo.getAccount());
            absentUser.add(userVo.getClass_());
            absentUser.add(userVo.getName());
            absentUser.add(userVo.getSex());
            absentUser.add(String.valueOf(userVo.getAge()));
            absentUsers.add(absentUser);
        }
        return page(absentUsers, pagesize);
    }

    /**
     * 获取列表学生信息和得分
     * @param orgCode
     * @return
     */
    private List getUserInfos(Integer orgCode, List<UserVo> userVos, Map<Integer, List<ScoreVo>> scoreVosMap, List<Questiontype> questiontypes, Map<Integer, List<Score>> scoresMap) {
        //该机构提交过答案的考生
        List<List> userScoreList = new ArrayList<>();
        int pagesize = 40;
        List<Integer> intelligence = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_SYMBOLIC_OPERATION);
            add(SystemConstant.QUESTION_TYPE_SPATIAL_MEMORY);
            add(SystemConstant.QUESTION_TYPE_GRAPF_SEARCH);
            add(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);
            add(SystemConstant.QUESTION_TYPE_PARAGRAPH_REASONING);
            add(SystemConstant.QUESTION_TYPE_ANALOGIC_REASONING);
            add(SystemConstant.QUESTION_TYPE_SPACE_ROTATION);
            add(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT);
            add(SystemConstant.QUESTION_TYPE_MATRIX_REASONING);
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_ABILITY);
            add(SystemConstant.QUESTION_TYPE_CRITICALTHINKING_TENDENCY);
        }};
        List<Integer> creativity = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_REMOTE_ASSOCIATION);
        }};
        List<Integer> emotion = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_EMOTION_RECOGNITION);
            add(SystemConstant.QUESTION_TYPE_EMOTION_UNDERSTANDING);
        }};
        List<Integer> personality = new ArrayList(){{
            add(SystemConstant.QUESTION_TYPE_PERSONALITY);
        }};
        boolean intelliIndicator = false;
        boolean creativityIndicator = false;
        boolean emotionIndicator = false;
        boolean personalityIndicator = false;
        Integer intelliIndex = 5;
        Integer index = 3;
        Integer creativityIndex = 0;
        Integer emotionIndex = 0;
        List<String> header = new ArrayList<>();
        for (Questiontype questiontype:questiontypes){
            if (intelligence.contains(questiontype.getId()) && intelliIndicator == false) {
                intelliIndicator = true;
            }
            if (creativity.contains(questiontype.getId()) && creativityIndicator == false) {
                creativityIndicator = true;
            }
            if (emotion.contains(questiontype.getId()) && emotionIndicator == false) {
                emotionIndicator = true;
            }
            if (personality.contains(questiontype.getId()) && personalityIndicator == false) {
                personalityIndicator = true;
            }
        }
        if (creativityIndicator) {
            creativityIndex = intelliIndex + 2;
            header.add("创造性");
            header.add("创造性排名");
        }
        if (emotionIndicator){
            header.add("情绪智力成绩");
            header.add("情绪智力排名");
            if (creativityIndex != 0)
                emotionIndex = creativityIndex + 2;
            if (creativityIndex == 0)
                emotionIndex = intelliIndex + 2;
        }
        if (personalityIndicator)
            header.add("人格特征");
        Map<String,Object> intelliMap = new HashedMap();
        Map<String,Object> creativityMap = new HashedMap();
        Map<String,Object> emotionMap = new HashedMap();
        for(UserVo userVo: userVos){
            List<String> userInfo = new ArrayList<>();
            userInfo.add(userVo.getAccount());
            userInfo.add(userVo.getClass_());
            userInfo.add(userVo.getName());
            userInfo.add(userVo.getSex());
            userInfo.add(String.valueOf(userVo.getAge()));
            List<Double> intelliScore = new ArrayList<>();
            List<Double> emotionScore = new ArrayList<>();
            double creativityScore = 0;
            List<Integer> personalityInfo = new ArrayList(){};
            List<ScoreVo> scoreVos = scoreVosMap.get(userVo.getUid());
            for (ScoreVo scoreVo : scoreVos) {
                if (intelligence.contains(scoreVo.getQtype().intValue()) && intelliIndicator == true) {
                    intelliScore.add(Double.parseDouble(scoreVo.getScore()));
                    intelliMap.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                }
                if (creativity.contains(scoreVo.getQtype().intValue()) && creativityIndicator == true) {
                    creativityScore = Double.parseDouble(scoreVo.getStandardScore());
                    creativityMap.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                }
                if (emotion.contains(scoreVo.getQtype().intValue()) && emotionIndicator == true) {
                    emotionScore.add(Double.parseDouble(scoreVo.getScore()));
                    emotionMap.put(scoreVo.getIntroduce(), scoreVo.getStandardScore());
                }
                if (personality.contains(scoreVo.getQtype().intValue()) && personalityIndicator == true) {
                    personalityInfo = getDimensionlevel(scoreVo, personalityInfo);
                }
            }
            if (intelliIndicator == true) {
                if (intelliScore.size()==1)
                    userInfo.add(intelliMap.values().iterator().next().toString());
                else
                    userInfo.add(String.valueOf(calScore(intelliScore, intelligence, orgCode, userVos, scoresMap)));
                userInfo.add("0");
            }
            if (creativityIndicator == true) {
                userInfo.add(String.valueOf(creativityScore));
                userInfo.add("0");
            }
            if (emotionIndicator == true) {
                if (emotionScore.size()==1)
                    userInfo.add(emotionMap.values().iterator().next().toString());
                else
                    userInfo.add(String.valueOf(calScore(emotionScore, emotion, orgCode, userVos, scoresMap)));
                userInfo.add("0");
            }
            if (personalityIndicator == true) {
                userInfo.add(String.join(", ", personalityInfo.toString()));
            }
            userScoreList.add(userInfo);
        }
        if (creativityIndicator)
            userScoreList = userRankByAbility(userScoreList, creativityIndex, false);
        if (emotionIndicator)
            userScoreList = userRankByAbility(userScoreList, emotionIndex, false);
        userScoreList = userRankByAbility(userScoreList, intelliIndex, true);
        List<Map> pages = new ArrayList<>();
        for (Object useScore: page(userScoreList, pagesize)){
            pages.add(new HashMap(){{
                put("header", header);
                put("scores", useScore);
            }});
        }
        return pages;
    }

    /**
     * 计算能力的总分
     * @param zscores z分数列表
     * @return
     */
    private Integer calScore(List<Double> zscores, List<Integer> questiontypes, Integer orgCode, List<UserVo> userVos, Map<Integer, List<Score>> scoresMap){
        List<Double> normZScore = new ArrayList<>();
        Double average = 0.0;
        Double std = 0.0;
        for (int i = 0; i < userVos.size(); i++){
            Integer uid = userVos.get(i).getUid();

            Double userZscore = 0.0;
            Double num = 0.0;
            List<Score> scores = scoresMap.get(uid);
            for (Score score: scores){
                if (questiontypes.contains(score.getQtype())) {
                    userZscore += Double.parseDouble(score.getzScore());
                    num++;
                }
            }
            normZScore.add(userZscore);
        }
        Map<String, Double> norm = calcAverageAndSD(normZScore);

        Double totalZScore = 0.0;
        for (Double score: zscores){
            totalZScore +=score;
        }
        return (int) (100 + 15 * (totalZScore - norm.get("average")) / norm.get("SD") + 0.5);
    }

    /**
     * 计算均值和方差
     * @param zScores
     * @return
     */
    private Map<String, Double> calcAverageAndSD(List<Double> zScores){
        Double average = 0.0;
        Double SD = 0.0;
        for (Double zScore: zScores){
            average += zScore;
            SD += zScore*zScore;
        }
        final Double Average = average/zScores.size();
        final Double Sd = Math.sqrt(SD/zScores.size() - Average*Average);
        Map<String, Double> norm = new HashMap(){{
            put("average", Average);
            put("SD", Sd);
        }};
        return norm;
    }
//    private Integer calScore(List<Double> zscores, List<Integer> questiontypes, Integer orgCode, List<UserVo> userVos, Map<Integer, List<Score>> scoresMap){
//        Double average = 0.0;
//        Double std = 0.0;
//        for (int i = 0; i < userVos.size(); i++){
//            Integer uid = userVos.get(i).getUid();
//
//            Double userZscore = 0.0;
//            Double num = 0.0;
//            List<Score> scores = scoresMap.get(uid);
//            for (Score score: scores){
//                if (questiontypes.contains(score.getQtype())) {
//                    userZscore += Double.parseDouble(score.getzScore());
//                    num++;
//                }
//            }
//            std += userZscore * userZscore;
//            average += userZscore;
//        }
//        average /= userVos.size();
//        std = Math.sqrt(std / userVos.size() - average * average);
//        Double totalZScore = 0.0;
//        for (Double score: zscores){
//            totalZScore +=score;
//        }
//        totalZScore /= zscores.size();
//        return (int) (100 + 15 * (totalZScore - average) / std + 0.5);
//    }
    /**
     * 排序
     * @param userInfos
     * @param column
     * @return
     */
    private List userRankByAbility(List<List> userInfos, int column, boolean rankable){
        List<Object> userInfoList = new ArrayList<>(userInfos.size());
        for (int i = 0; i < userInfos.size(); i++){
            userInfoList.add(i, new ArrayList<>(1));
        }
        List<Double> abilityScore = new ArrayList<>();
        for(List<String> userInfo: userInfos){
            abilityScore.add(Double.parseDouble(userInfo.get(column)));
        }
        Collections.sort(abilityScore);
        for(List<String> userInfo: userInfos){
            int rank;
            rank = abilityScore.size() - abilityScore.lastIndexOf(Double.parseDouble(userInfo.get(column)));
            userInfo.set(column + 1, Integer.toString(rank));
        }
        //rank
        if (rankable) {
            for (List<String> userInfo : userInfos) {
                int index = Integer.parseInt(userInfo.get(column + 1)) - 1;
                ArrayList temp = (ArrayList) userInfoList.get(index);
                while(temp.size() != 0){
                    index++;
                    temp = (ArrayList) userInfoList.get(index);
                }
                userInfoList.set(index, userInfo);
            }
            return userInfoList;
        }
        return userInfos;
    }

    /**
     * 分页
     * @param userInfos
     * @param pagesize
     * @return
     */
    private List page(List userInfos, int pagesize){
        List<List> userInfoPages = new ArrayList<>(userInfos.size() / pagesize + 1);
        List userInfoPage = new ArrayList<>();
        for (Object userInfo: userInfos){
            if (userInfoPage.size() == pagesize){
                userInfoPages.add(userInfoPage);
                userInfoPage = new ArrayList<>();
            }
            userInfoPage.add(userInfo);
        }
        userInfoPages.add(userInfoPage);
        return userInfoPages;
    }

    /**
     * 获取人格特征的等级
     * @param scoreVo
     * @param levels
     * @return
     */
    private List getDimensionlevel(ScoreVo scoreVo, List levels){
        if(Integer.parseInt(scoreVo.getStandardScore()) < 55){
            levels.add(1);
        }
        else if(Integer.parseInt(scoreVo.getStandardScore()) > 145){
            levels.add(0);
        }
        Collections.sort(levels);
        return levels;
    }

    /**
     * 从用户分数中构建出该用户的非智力因素题目的数据结构
     * @param userScores 用户分数
     * @return
     */
    private Map<String,Object> getNonIntelligenceData(List<Score> userScores,Integer intelligenceSize){

        List<Score> scores = new ArrayList<>();
        for (Score score: userScores) {
            if(score.getQtype().equals(SystemConstant.QUESTION_TYPE_PERSONALITY))
                scores.add(score);
        }

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
        Map personailty = new HashMap<>();
        personailty.put("personailty", new HashMap(){{
            put("score",personailtyScore.toString().substring(0, personailtyScore.toString().length() - 1));
            put("dimname",personailtyDimName.toString().substring(0, personailtyDimName.toString().length() - 1));
        }});
        personailty.put("explain",personailtyExplainSecond.toString().substring(0, personailtyExplainSecond.toString().length() - 1));
        return personailty;
    }

    /**
     * 获取Json数据
     * @param
     * @return
     */
    private void getDescriptionData() throws MalformedURLException {
        StringBuilder descriptionData = new StringBuilder();
        String encoding = "UTF-8";
        File file = FileUtils.getPublicFile("/assets/json/", "orgReport.json");
        try{
            BufferedReader filecontent = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
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


}
