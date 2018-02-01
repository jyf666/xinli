package dao;

import models.TestpaperQuestiontype;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import java.util.List;

/**
 * Created by XIAODA on 2015/7/24.
 */
@Repository
public interface TestpaperQuestiontypeDao extends GenericRepository<TestpaperQuestiontype, Integer> {

    /**
     * 通过试卷id找到本次测试所有的考试题型
     * @param tpid
     * @return
     */
    public List<TestpaperQuestiontype> findByTpidOrderBySeqAsc(Integer tpid);

    /**
     * 通过试卷id找到本次测试所有的考试题型
     * @param tpid
     * @return
     */
    @Query("select tq.qtid from TestpaperQuestiontype tq where tq.tpid = :tpid order by tq.seq asc")
    public List<String> findQtidListByTpid(@Param("tpid") Integer tpid);

    public TestpaperQuestiontype findByTpidAndQtid(Integer tpid,Integer qtid);


    @Query("select tq from TestpaperQuestiontype tq,Test t,Testpaper tp where t.id=:tid and t.pid=tp.id and tp.id=tq.tpid order by tq.seq asc")
    public List<TestpaperQuestiontype> findByTid(@Param("tid") Integer tid);
}
