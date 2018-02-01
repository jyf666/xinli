package dao;

/**
 * Created by ballma on 16/3/17.
 */

import models.TestpaperUser;
import persistence.GenericRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestpaperUserDao extends GenericRepository<TestpaperUser, String> {
    /** 保存每个用户考试试卷
     *
     * @param testpaperUser
     * @pdOid 7ad5e31e-026e-4b6b-ac79-b768a5fa8c65 */
    public TestpaperUser save(TestpaperUser testpaperUser);

    public TestpaperUser findByUidAndTpid(Integer uid,Integer tpid);

    public List<TestpaperUser> findByTpid(Integer tpid);
}
