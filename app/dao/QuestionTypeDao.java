package dao;

import models.Questiontype;
import models.VQuestiontype;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by XIAODA on 2015/7/13.
 */
@Repository
public interface QuestionTypeDao extends GenericRepository<Questiontype, Integer> {

    @Query("select a from Questiontype a where a.useStatus='1'")
    public List<Questiontype>  findByUseStatus();

    @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Query("select a from Questiontype a, Testpaper b,TestpaperQuestiontype c where  a.id = c.qtid and c.tpid = b.id and b.id=:tPid order by seq")
    public List<Questiontype>  findAllByTpid(@Param("tPid") Integer tPid);

    @Query("select a from Questiontype a, Testpaper b,TestpaperQuestiontype c where  a.id = c.qtid and c.tpid = b.id and b.isReference='1'order by seq")
    public List<Questiontype> findByIsReference();

    @Query("select a from Questiontype a, Orders o,TestpaperQuestiontype tq where  o.orgCode=:orgCode and o.tpid=tq.tpid and tq.qtid=a.id and o.status='1' and o.dateCreated = (select max(d.dateCreated) from Orders d where d.orgCode=:orgCode and d.status='1')")
    public List<Questiontype>  findByOrgCode(@Param("orgCode") Integer orgCode);

    @QueryHints( { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Query("select new models.VQuestiontype(a.id, a.name, c.limitTime) from Questiontype a, Testpaper b, TestpaperQuestiontype c where a.id = c.qtid and c.tpid = b.id and b.id=:tPid and a.useStatus='1' order by seq")
    public List<VQuestiontype>  findAllVQuestionTypeByTpid(@Param("tPid") Integer tPid);
}
