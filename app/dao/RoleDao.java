package dao;

import models.Role;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by XIAODA on 2015/12/18.
 */
@Repository
public interface RoleDao extends GenericRepository<Role, Integer> {
}
