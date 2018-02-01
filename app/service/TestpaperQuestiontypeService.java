package service;

import dao.TestpaperQuestiontypeDao;
import models.Testpaper;
import models.TestpaperQuestiontype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.SearchFilter;
import utils.json.JsonMapper;

import java.util.List;

/**
 * Created by XIAODA on 2015/7/24.
 */
@Service
public class TestpaperQuestiontypeService {

    @Autowired
    private TestpaperQuestiontypeDao testpaperQuestiontypeDao;

    /**
     * 根据当前测试题型获取下一测试题型
     * @param tpid
     * @return
     */
    public int findNextQtypeByQtype(Integer tpid, Integer qtype){

        List<TestpaperQuestiontype> list = testpaperQuestiontypeDao.findByTpidOrderBySeqAsc(tpid);

        int next_qtype = 0;
        for (int i = 0; i < list.size(); i++) {
            TestpaperQuestiontype testpaperQuestiontype = list.get(i);
            if(qtype.equals(testpaperQuestiontype.getQtid())){
                int index = i + 1;
                if(index<list.size()-1){
                    next_qtype = list.get(index).getQtid();
                }
                return  next_qtype;
            }
        }
        return next_qtype;
    }

    public String findQtidListByTpid(Integer tpid){
        List<String> list = testpaperQuestiontypeDao.findQtidListByTpid(tpid);
        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.toJson(list);
        return json;
    }


    public List<TestpaperQuestiontype> findByTid(Integer tid){
        return  testpaperQuestiontypeDao.findByTid(tid);
    }

    /** 根据考试查找该考试的题型
     * @param tid 考试id
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     */
    public List<TestpaperQuestiontype> getTestpaperQuestiontypeByTid(Integer tid){
        return testpaperQuestiontypeDao.findByTid(tid);
    }

    public void updateOrder(Integer tpid, List<Integer> seqList) {
        List<TestpaperQuestiontype> questiontypeList = testpaperQuestiontypeDao.findAll(SearchFilter.eq("tpid", tpid));
        for (TestpaperQuestiontype testpaperquestiontype: questiontypeList) {
            Integer qtid = testpaperquestiontype.getQtid();
            Integer seq = testpaperquestiontype.getSeq();
            Integer index = seqList.indexOf(qtid) + 1;
            testpaperquestiontype.setSeq(index);
        }
        testpaperQuestiontypeDao.save(questiontypeList);
    }

    public void updateLimitTime(Integer tpid, Integer qtid, Integer limitTime) {
        TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypeDao.findOne(SearchFilter.eq("tpid", tpid), SearchFilter.eq("qtid", qtid));
        testpaperQuestiontype.setLimitTime(limitTime);
        testpaperQuestiontypeDao.save(testpaperQuestiontype);
    }
}
