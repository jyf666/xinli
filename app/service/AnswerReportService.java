package service;

import dao.AnswerReportDao;
import models.AnswerReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by XIAODA on 2015/7/27.
 */
@Service
public class AnswerReportService {

    @Autowired
    private AnswerReportDao answerReportDao;

    /**
     * 保存或更新答题记录表
     * @param uid
     * @param tid
     * @param qType
     * @param complete
     */
    @Transactional
    public void saveOrUpdate(Integer uid, Integer tid, Integer qType, Integer complete){

        AnswerReport answerReport;
        List<AnswerReport> answerReportList = answerReportDao.findByUidAndTidAndQtypeAndCommitTimeIsNullOrderByStartTimeDesc(uid, tid, qType);
        if(answerReportList != null && answerReportList.size() > 0){
            answerReport = answerReportList.get(0);
            answerReport.setCommitTime(new Date());
            answerReport.setComplete(complete);
        } else {
            answerReport = new AnswerReport();
            answerReport.setUid(uid);
            answerReport.setTid(tid);
            answerReport.setQtype(qType);
            answerReport.setStartTime(new Date());
        }
        answerReportDao.save(answerReport);
    }

    @Transactional
    public List<AnswerReport> save(List<AnswerReport> list){
        return answerReportDao.save(list);
    }

    /**
     * 获取莫考生最后一次提交的题型
     * @param uid
     * @param tid
     * @return
     */
    public Integer findQtypeLastCommit(Integer uid, Integer tid){
        List<Integer> list = answerReportDao.findQtypeByUidAndTidAndCommitTimeIsNotNullOrderByCommitTimeDesc(uid, tid);
        return list.get(0);
    }
}
