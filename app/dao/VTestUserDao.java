package dao;

import models.VTestUser;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by XIAODA on 2016/2/22.
 */
@Repository
public interface VTestUserDao extends GenericRepository<VTestUser, Integer> {
}
