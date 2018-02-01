package dao;

/**
 * Created by ballma on 16/3/21.
 */

import models.TestpaperUserQuestion;
import persistence.GenericRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TestpaperUserQuestionDao  extends GenericRepository<TestpaperUserQuestion, String> {

}
