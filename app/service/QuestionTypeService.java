package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.QuestionDao;
import dao.QuestionTypeDao;
import dao.TestpaperQuestiontypeDao;
import dao.VQuestiontypeDao;
import models.Question;
import models.Questiontype;
import models.TestpaperQuestiontype;
import models.VQuestiontype;
import models.vo.QuestiontypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;
import play.cache.Cache;
import utils.SystemConstant;


import org.springframework.data.jpa.domain.Specification;
import persistence.DynamicSpecifications;
import play.mvc.Http;
import utils.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/6/30.
 */
@Service
public class QuestionTypeService {

    @Autowired
    private QuestionTypeDao questionTypeDao;
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private VQuestiontypeDao vQuestiontypeDao;
    @Autowired
    private TestpaperQuestiontypeDao testpaperQuestiontypeDao;

    /** 添加考试题型
     * @param objectNode 题型数据json对象
     * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
    @Transactional
    public Questiontype save(ObjectNode objectNode){
        Questiontype questiontype = new Questiontype();
        questiontype.setName(objectNode.findPath("qTypeName").asText());
        questiontype.setIntroduce(objectNode.findPath("introduce").asText());
        questiontype.setLimitTime(objectNode.findPath("answerTime").asInt());
        Questiontype qt = questionTypeDao.save(questiontype);
//        updateQtypeTimeMapCache(qt);
        return qt;
    }
    /** 修改考试题型
     * @param objectNode 题型数据json对象
     * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
    @Transactional
    public Questiontype update(ObjectNode objectNode){
        Questiontype questiontype = questionTypeDao.findOne(objectNode.findPath("qtId").asInt());
        questiontype.setName(objectNode.findPath("qTypeName").asText());
        questiontype.setIntroduce(objectNode.findPath("introduce").asText());
        questiontype.setLimitTime(objectNode.findPath("answerTime").asInt());
        questiontype.setScoringFormula(objectNode.findPath("scoring").asText());
//        updateQtypeTimeMapCache(questiontype);
        return questionTypeDao.save(questiontype);
    }

    /** 获取所有题型
     *
     * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
    public List<Questiontype> getAllQuestionType(){

        return questionTypeDao.findByUseStatus();
    }
    /** 获取所有题型
     *
     * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
    public Page<QuestiontypeVo> getAllQuestionType(Integer pageNum){
        Pageable pageable = new PageRequest(pageNum-1,SystemConstant.PAGESIZE);
        String sql = "SELECT QT.ID, NAME, LIMIT_TIME, INTRODUCE, SCORING_FORMULA, QT.USE_STATUS,TYPE,COUNT(QUST.ID) CON FROM QUESTIONTYPE QT\n" +
                "LEFT JOIN QUESTION QUST ON QUST.Q_TYPE = QT.ID WHERE QT.USE_STATUS='1'\n" +
                "GROUP BY QT.ID, NAME, LIMIT_TIME, INTRODUCE, SCORING_FORMULA, QT.USE_STATUS";
        Page<Object> page = questionTypeDao.findAllBySql(pageable, sql);

        List<QuestiontypeVo> questiontypeVos = new ArrayList<>();
        List<Object> objects = page.getContent();
        for (int i = 0; i < objects.size(); i++) {
            Object[] o = (Object[])objects.get(i);
            QuestiontypeVo  questiontypeVo = new QuestiontypeVo();
            questiontypeVo.setId((Integer) o[0]);
            questiontypeVo.setName((String) o[1]);
            questiontypeVo.setLimitTime((Integer) o[2]);
            questiontypeVo.setIntroduce((String) o[3]);
            questiontypeVo.setScoringFormula((String) o[4]);
            questiontypeVo.setType(String.valueOf(o[6]));
            questiontypeVo.setQuestionNumber(((BigInteger) o[7]).intValue());
            questiontypeVos.add(questiontypeVo);
        }
        PageImpl<QuestiontypeVo> pageImpl = new PageImpl<QuestiontypeVo>(questiontypeVos,pageable,page.getTotalElements());

        return pageImpl;
    }

    /**
     * 分页查找题目类型
     * @param request
     * @param cols
     * @return
     */
    public Page<VQuestiontype> findAll(Http.Request request, String[] cols){
        Pageable pageable = PageUtils.getPageRequest(request, cols);
        Specification<VQuestiontype> spec = DynamicSpecifications.fromRequest(request, VQuestiontype.class);
        return vQuestiontypeDao.findAll(spec, pageable);
    }


    /** 获取模版题型
     *
     * @pdOid c252707a-5eef-4175-919d-aeff07ef0859 */
    public List<Questiontype> getByIsReference(){
        return questionTypeDao.findByIsReference();
    }


    /**
     * 根据试卷id查找题型
     * @param tpid
     * @return
     */
    public List<Questiontype> findAllByTpid(Integer tpid){
        return questionTypeDao.findAllByTpid(tpid);
    }

    public Map getQuestionTypeMapTpid(Integer tpid, Integer qtype){
        List<Questiontype> questiontypes = questionTypeDao.findAllByTpid(tpid);
        Map map = new HashMap();
        map.put("questiontypes", questiontypes);
        for (int i = 0; i < questiontypes.size(); i++) {
            Questiontype questiontype = questiontypes.get(i);
            if(qtype.equals(questiontype.getId())){
                map.put("index", i);
                break;
            }
        }
        return map;
    }

    /**
     * 根据ID查找题目类型
     * @param id
     * @return
     */
    public Questiontype findById(Integer id){
        return questionTypeDao.findOne(id);
    }

    /**
     * 停用题型
     * @param qtype 题型id
     * @return
     */
    public void delete(Integer qtype){
        Questiontype questiontype = questionTypeDao.findOne(qtype);
        questiontype.setUseStatus("0");
        List<Question> questions = questionDao.findByQType(qtype);
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            question.setUseStatus("0");
        }
        questionDao.save(questions);
        questionTypeDao.save(questiontype);

    }

    /**
     * 更新缓存
     * @param questiontype
     */
    public void updateQtypeTimeMapCache(Questiontype questiontype){
        Map qtypeTimeMap = (Map)Cache.get("qtypeTimeMap");
        Integer qtype = questiontype.getId();
//        Integer time = questiontype.getLimitTime();
//        qtypeTimeMap.put(qtype, time);
//        Cache.set("qtypeTimeMap", qtypeTimeMap);
        /** 设置每套试卷题型时间 */
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);
        Map qtypeTimeMapEach = (Map)Cache.get(tpid.toString());
        if (qtypeTimeMapEach==null){
            qtypeTimeMapEach = new HashMap();
        }
        Integer timeEach = testpaperQuestiontypeDao.findOne(SearchFilter.eq("tpid", tpid), SearchFilter.eq("qtid", qtype)).getLimitTime();
        qtypeTimeMapEach.put(qtype, timeEach);
        Cache.set("qtypeTimeMap", qtypeTimeMapEach);
    }

    public void updateQtypeTimeEachMapCache() {
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);
//        Map qtypeTimeMapEach = (Map)Cache.get(tpid.toString());
        Map qtypeTimeMapEach = (Map)Cache.get("qtypeTimeMap");
        if (qtypeTimeMapEach==null){
            qtypeTimeMapEach = new HashMap();
        }
        List<VQuestiontype> vQuestiontypes = questionTypeDao.findAllVQuestionTypeByTpid(tpid);
        for (VQuestiontype vQuestiontype: vQuestiontypes){
            Integer qtype = vQuestiontype.getId();
            Integer timeEach = vQuestiontype.getLimitTime();
            qtypeTimeMapEach.put(qtype, timeEach);
        }
        Cache.set("qtypeTimeMap", qtypeTimeMapEach);
        System.out.println(qtypeTimeMapEach);
    }

    public List<VQuestiontype> findAllVQuestionTypeByTpid(Integer tpid) {
        return questionTypeDao.findAllVQuestionTypeByTpid(tpid);
    }

    public List<Questiontype> findAll(Http.Request request){
        Specification<Questiontype> spec = DynamicSpecifications.fromRequest(request, Questiontype.class);
        return questionTypeDao.findAll(spec);
    }

    public String getTimeMsg(Integer qtype){
        Map<Integer, Integer> qtypeTimeMap = (Map<Integer, Integer>) Cache.get("qtypeTimeMap");
        int time = qtypeTimeMap.get(qtype);
        int minutes = time / 60;
        int seconds = time % 60;
        String timeMsg = "";
        if(seconds == 0){
            timeMsg = minutes+"分钟";
        } else {
            timeMsg = minutes+"分"+seconds+"秒";
        }
        return timeMsg;
    }
}
