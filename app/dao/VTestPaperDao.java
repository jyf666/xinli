package dao;

import models.VTestPaper;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by lintongyu on 2016/1/19.
 */
@Repository
public interface VTestPaperDao extends GenericRepository<VTestPaper, Integer> {
}
