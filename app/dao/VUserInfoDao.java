package dao;

import models.VUserInfo;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by XIAODA on 2015/12/28.
 */
@Repository
public interface VUserInfoDao extends GenericRepository<VUserInfo, Integer> {
}
