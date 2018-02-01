package dao;

import models.Permission;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/**
 * Created by XIAODA on 2015/12/23.
 */
@Repository
public interface PermissionDao extends GenericRepository<Permission, Integer> {
}
