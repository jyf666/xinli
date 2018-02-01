package controllers;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import models.dto.UserDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;
import views.html.system.info;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIAODA on 2015/10/20.
 */
@org.springframework.stereotype.Controller
public class SystemController extends Controller {

    @Autowired
    private BoneCPDataSource dataSource;

    public Result info(){
        BoneCPConfig boneCPConfig = dataSource.getConfig();
        int dbActive = dataSource.getPool().getTotalLeased();
        int dbIdle = dataSource.getPool().getTotalCreatedConnections();
        int dbUnuse = dataSource.getPool().getTotalFree();

        Map<String, String> dsMap = new HashMap();
        dsMap.put("initSQL",dataSource.getInitSQL());
        dsMap.put("totalLeased",String.valueOf(dataSource.getTotalLeased()));
        dsMap.put("acquireIncrement",String.valueOf(dataSource.getAcquireIncrement()));
        dsMap.put("acquireRetryAttempts",String.valueOf(dataSource.getAcquireRetryAttempts()));
        dsMap.put("acquireRetryDelay",String.valueOf(dataSource.getAcquireRetryDelay()));
        dsMap.put("acquireRetryDelayInMs",String.valueOf(dataSource.getAcquireRetryDelayInMs()));
        dsMap.put("closeConnectionWatchTimeoutInMs",String.valueOf(dataSource.getCloseConnectionWatchTimeoutInMs()));
        dsMap.put("closeConnectionWatchTimeout",String.valueOf(dataSource.getCloseConnectionWatchTimeout()));
        dsMap.put("connectionTimeoutInMs",String.valueOf(dataSource.getConnectionTimeoutInMs()));
        dsMap.put("connectionTimeout",String.valueOf(dataSource.getConnectionTimeout()));
        dsMap.put("idleConnectionTestPeriod",String.valueOf(dataSource.getIdleConnectionTestPeriod()));
        dsMap.put("idleConnectionTestPeriodInMinutes",String.valueOf(dataSource.getIdleConnectionTestPeriodInMinutes()));
        dsMap.put("idleMaxAgeInMinutes",String.valueOf(dataSource.getIdleMaxAgeInMinutes()));
        dsMap.put("idleMaxAge",String.valueOf(dataSource.getIdleMaxAge()));
        dsMap.put("maxConnectionAgeInSeconds",String.valueOf(dataSource.getMaxConnectionAgeInSeconds()));
        dsMap.put("maxConnectionAge",String.valueOf(dataSource.getMaxConnectionAge()));
        dsMap.put("maxConnectionsPerPartition",String.valueOf(dataSource.getMaxConnectionsPerPartition()));
        dsMap.put("minConnectionsPerPartition",String.valueOf(dataSource.getMinConnectionsPerPartition()));
        dsMap.put("partitionCount",String.valueOf(dataSource.getPartitionCount()));
        dsMap.put("poolAvailabilityThreshold",String.valueOf(dataSource.getPoolAvailabilityThreshold()));
        dsMap.put("queryExecuteTimeLimit",String.valueOf(dataSource.getQueryExecuteTimeLimit()));
        dsMap.put("queryExecuteTimeLimitInMs",String.valueOf(dataSource.getQueryExecuteTimeLimitInMs()));
        dsMap.put("statementsCacheSize",String.valueOf(dataSource.getStatementsCacheSize()));
        dsMap.put("preparedStatementCacheSize",String.valueOf(dataSource.getPreparedStatementCacheSize()));
        dsMap.put("preparedStatementsCacheSize",String.valueOf(dataSource.getPreparedStatementsCacheSize()));
        dsMap.put("releaseHelperThreads",String.valueOf(dataSource.getReleaseHelperThreads()));
        dsMap.put("statementCacheSize",String.valueOf(dataSource.getStatementCacheSize()));
        dsMap.put("statementReleaseHelperThreads",String.valueOf(dataSource.getStatementReleaseHelperThreads()));
        dsMap.put("statementsCachedPerConnection",String.valueOf(dataSource.getStatementsCachedPerConnection()));

        Runtime runtime = Runtime.getRuntime();
        long mInuse = runtime.totalMemory() - runtime.freeMemory();
        long mFree = runtime.freeMemory();
        long mUnuse = runtime.maxMemory() - runtime.totalMemory();

        return ok(info.render(boneCPConfig, dbActive, dbIdle, dbUnuse, mInuse, mFree, mUnuse, dsMap));
    }

    public Result exportErrorLog(){
        String date = DateUtils.formatDate(new Date());
        File root = Play.application().path();
        File file = new File(root, "/logs/xinli/error" + date + ".log");
        response().setContentType("multipart/form-data");// 这样设置，会自动判断下载文件类型
        response().setHeader("Content-Disposition", "attachment;fileName=" + file.getName());
        return ok(file);
    }
}

