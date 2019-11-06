package com.saber.api.autotest.service;

import com.saber.api.autotest.db.jdbc.JdbcManager;
import com.saber.api.autotest.db.redis.RedisManager;
import com.saber.api.autotest.model.TestCaseModel;
import com.saber.api.autotest.utils.ReportUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClearDataService {

    @Autowired
    private JdbcManager jdbcManager;
    @Autowired
    private RedisManager redisManager;

    /**
     * 清理测试产生的垃圾数据
     * PS: Excel配置的为sql语句，以分号隔开
     *
     * @param testCase
     */
    public void clear(TestCaseModel testCase) {
        try {
            // 清理MySQL数据库
            if (StringUtils.isNotEmpty(testCase.getClearMysql())) {
                String clears = testCase.getClearMysql();
                String[] sqlArray = clears.split(";");
                for (String sql : sqlArray) {
                    jdbcManager.execute(sql);
                }
            }
            // 清理Redis数据库
            if (StringUtils.isNotEmpty(testCase.getClearRedis())) {
                String[] redisArray = testCase.getClearRedis().split(";");
                clearRedis(redisArray);
            }
        } catch (Exception e) {
            ReportUtil.log("清理测试数据报错，请检查报错信息");
            e.printStackTrace();
        }
    }

    /**
     * 清理redis 支持set和del
     * $yesterday  2019-12-04
     * $today      2019-12-05
     * $datefortable 20191204
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
