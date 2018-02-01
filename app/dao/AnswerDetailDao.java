package dao;

import models.AnswerDetail;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by XIAODA on 2015/10/16.
 */
@Repository
public interface AnswerDetailDao extends GenericRepository<AnswerDetail, Integer> {
}
