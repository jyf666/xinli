/***********************************************************************
 * Module:  TestpaperNormDao.java
 * Author:  XIAODA
 * Purpose: Defines the Class TestpaperNormDao
 ***********************************************************************/

package dao;

import java.util.*;
import models.TestpaperNorm;
import org.springframework.stereotype.Repository;
import persistence.GenericRepository;

/** 试卷常模Dao
 * 
 * @pdOid 0ef42f7b-39d9-4bb2-8875-44c3e3f92c7e */
@Repository
public interface TestpaperNormDao extends GenericRepository<TestpaperNorm, Integer> {
   /** 保存试卷常模
    * 
    * @param testpaperNorm
    * @pdOid d3f6354e-dde2-4127-ab90-afd84c8336c7 */
   public TestpaperNorm save(TestpaperNorm testpaperNorm);

   
   /** 根据招生机构编码查询试卷常模
    * 
    * @param orgCode
    * @pdOid 731c163c-255f-41d9-a8c6-815dbc33c5ed */
   public List<TestpaperNorm> findByOrgCode(String orgCode);

}