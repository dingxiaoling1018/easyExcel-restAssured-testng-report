package com.saber.api.autotest.service;

import com.saber.api.autotest.db.jdbc.JdbcManager;
import com.saber.api.autotest.db.redis.RedisManager;
import com.saber.api.autotest.model.TestCaseModel;
import com.saber.api.autotest.utils.SqlFileExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/8/27 18:01
 */
@Service
@Slf4j
public class InitTestCaseService {

    @Autowired
    public Environment environment;
    @Autowired
    public SqlFileExecutor sqlFileExecutor;
    @Autowired
    public JdbcManager jdbcManager;
    @Autowired
    public RedisManager redisManager;

    /**
     * 准备工作(cookie,token,sql等)
     *
     * @param testCase
     * @return
     */
    public TestCaseModel initTestCase(TestCaseModel testCase) throws Exception {
        // 是SQL文件就执行文件；如果是SQL语句，就执行SQL语句
        if (StringUtils.isNotEmpty(testCase.getMysql())) {
            if (testCase.getMysql().endsWith(".sql")) {
                String dataDir = environment.getProperty("test.data.dir");
                String sqlFile = dataDir + File.separator + testCase.getMysql();
                try {
                    sqlFileExecutor.executeSqlFile(sqlFile);
                } catch (NullPointerException e) {
                    log.error("初始化数据库异常，异常信息：【{}】", e);
                }

            }

            if (!testCase.getMysql().endsWith(".sql")) {
                operationMysql(testCase.getMysql());
            }
        }

        if (StringUtils.isNotEmpty(testCase.getMongoDB())) {//判断是否要执行mongo文件
            //暂时未处理
        }

        if (StringUtils.isNotEmpty(testCase.getRedis())) {//判断是否要执行redis文件
            String[] redisArray = testCase.getRedis().split(";");
            clearRedis(redisArray);
        }
        return testCase;
    }

    /**
     * 执行case前执行sql语句
     * PS: Excel配置的为sql语句，以分号隔开
     *
     * @param sqlStr SQL语句
     */
    public void operationMysql(String sqlStr) {
        try {
            String[] sqlArray = sqlStr.split(";");
            for (String sql : sqlArray) {
                jdbcManager.execute(sql);
            }
            log.info("--执行SQL语句成功--");
        } catch (Exception e) {
            log.error("--执行SQL语句报错, 异常信息：【{}】--", e);
        }
    }


    /**
     * 清理redis 支持set和del
     * $yesterday  2017-12-04
     * $today      2017-12-05
     * $datefortable 20171204
     *
     * @return
     */
    public void clearRedis(String[] redisArray) {
        for (String str : redisArray) {
            if (str.trim().startsWith("set")) {//修改key
                String key = str.trim().split(" ")[1];
                String value = str.trim().split(" ")[2];
                redisManager.setRedisKeyValue(key, value);
            } else if (str.trim().startsWith("del")) {//删除key
                String key = str.trim().split(" ")[1];
                redisManager.removeRedisValueByKey(key);
            }
        }
    }

}
