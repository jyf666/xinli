/***********************************************************************
 * Module:  AdminDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class AdminDao
 ***********************************************************************/

package dao;

import models.Admin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import persistence.GenericRepository;

/** 管理员Dao
 * 
 * @pdOid 423f0014-1067-4d33-b8b6-547ab08d699f */
@Repository
public interface AdminDao extends GenericRepository<Admin, Integer> {

    @Query("select a from Admin a where a.loginName=:logName and a.useStatus='1'")
    Admin findByLoginName(@Param("logName") String logName);

    Admin findByOrgCode(Integer orgCode);

}