package com.saber.api.autotest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.databind.JsonNode;
import com.saber.api.autotest.db.jdbc.JdbcManager;
import com.saber.api.autotest.model.TestCaseModel;
import com.saber.api.autotest.utils.AssertUtil;
import com.saber.api.autotest.utils.JsonSchemaValidator;
import com.saber.api.autotest.utils.ReportUtil;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/8/27 18:01
 */
@Service
@Slf4j
public class VerifyResponseService {

    @Autowired
    JdbcManager jdbcManager;

    /**
     * 校验接口返回值
     *
     * @param response
     * @param testCase
     * @return
     */
    public void verifyResponse(Response response, TestCaseModel testCase) {
        AssertUtil.flag = true;
        AssertUtil.errors.clear();

        if ("error".equals(response)) {
            AssertUtil.flag = false;
        }

        //验证请求状态是否是200
        AssertUtil.equals(String.valueOf(response.getStatusCode()), String.valueOf(200));

        //期望和实际返回值完全一样
        if (isJson(response.getBody().asString()) && isJson(testCase.getExpectationsResponse())) {
            AssertUtil.equals(stringToJson(testCase.expectationsResponse).toString(), response.getBody().asString());
        }

        //jsonpath解析:返回结果中的部分字段
        if (isJson(response.getBody().asString()) && !isJson(testCase.getExpectationsResponse())) {
            try {
                JSONObject jsonObject = JSON.parseObject(response.getBody().asString());
                Map<String, String> map = str2map(testCase.expectationsResponse, ";", ":");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String expectKey = entry.getKey().trim();
                    String expectValue = entry.getValue().trim();

                    // 检查整个返回是否包含目标字段
                    if (!expectKey.startsWith("$")) {
                        ReportUtil.log("用例参数有误: 请检查参数拼写是否有误");
                    }

                    if (expectKey.startsWith("$")) {
                        String actualValue = JSONPath.eval(jsonObject, expectKey).toString();
                        AssertUtil.equals(expectKey, expectValue, actualValue);
                    }

                }
            } catch (Exception e) {
                AssertUtil.flag = false;
                ReportUtil.log("返回消息异常");
            }


        }

        if (AssertUtil.flag) {
            ReportUtil.log("--------测试用例成功--------: Pass");
        } else {
            ReportUtil.log("--------测试用例失败--------: Fail");
        }

        Assert.assertTrue(AssertUtil.flag, "仅做判断所有检查是否通过。如果出现该信息，请看前面输出的校验信息");
    }

    /**
     * string转json
     * author: DingXiaoling
     *
     * @param content
     * @return
     */
    private JsonNode stringToJson(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JsonSchemaValidator.getJsonNodeFromString(content);
        }
        return null;
    }

    public void log_verify(String input, Object actual_result, Object expect_result) {
        this.log_Message(input, actual_result, expect_result);
        assertEquals(expect_result, actual_result);
    }

    public void log_Message(String input, Object actual_result, Object expect_result) {
        if (input != null && !input.isEmpty()) {
            log.info("Test parameters: " + input);
        }
        log.info("actual_result:" + JSON.toJSONString(actual_result));
        log.info("expect_result:" + JSON.toJSONString(expect_result));
    }

    /**
     * 校验数据库
     *
     * @param expectations
     */
    public void verifyMysql(String expectations) throws Exception {

        if (StringUtils.isEmpty(expectations)) {
            return;
        }
        AssertUtil.flag = true;
        AssertUtil.errors.clear();
        String sql;

        // JSON数组走这个逻辑
        if (isJsonArray(expectations)) {
            JSONArray dataArr = JSONArray.parseArray(expectations);

            // data部分是json数组，直接按照json数组进行解析
            for (Object obj : dataArr) {
                JSONObject jsonObj = (JSONObject) obj;
                sql = jsonObj.getString("sql");
                Map actualMap = jdbcManager.queryToMap(sql);

                // 如果sql查询返回为空，则继续执行下一组数据
                if (actualMap == null) {
                    AssertUtil.flag = false;
                    ReportUtil.log("无法连接库: sql: \" + sql + \" 查不到数据! 请检查sql跟数据库!");
                    continue;
                }

                for (Iterator<String> iterator = jsonObj.keySet().iterator(); iterator.hasNext(); ) {
                    String expectKey = iterator.next().trim();
                    if (("sql").equals(expectKey)) {
                        continue;
                    }
                    String expectValue = jsonObj.getString(expectKey).trim();
                    String actualValue = actualMap.get(expectKey).toString();

                    AssertUtil.equals(expectKey, expectValue, actualValue);
                }
            }
        }
        // 非JSON数组走这个逻辑
        else {
            Map<String, String> sqlMap = str2map(expectations, ";", ":");
            if (sqlMap.containsKey("isMultipleCheck")) {
                sql = sqlMap.get("isMultipleCheck");
                Map actualMap = jdbcManager.queryToMap(sql);

                for (Map.Entry<String, String> entry : sqlMap.entrySet()) {

                    if (("isMultipleCheck").equals(entry.getKey())) {
                        continue;
                    }

                    String expectKey = entry.getKey().trim();
                    String expectValue = entry.getValue().trim();
                    String actualValue = actualMap.get(expectKey).toString().trim();

                    AssertUtil.equals(expectKey, expectValue, actualValue);
                }
            } else {
                for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
                    sql = entry.getKey();
                    String expectValue = entry.getValue().trim();
                    String actualValue = jdbcManager.query(sql).trim();

                    AssertUtil.equals(sql, expectValue, actualValue);
                }
            }
        }

        if (AssertUtil.flag) {
            ReportUtil.log("--------测试用例成功--------: Pass");
        } else {
            ReportUtil.log("--------测试用例失败--------: Fail");
        }

        Assert.assertTrue(AssertUtil.flag, "仅做判断所有检查是否通过。如果出现该信息，请看前面输出的校验信息");
    }

    /**
     * 判断字符串是否为Json
     *
     * @param content
     * @return boolen
     */
    private boolean isJson(String content) {
        if (stringToJson(content) == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为JsonArray
     *
     * @param content
     * @return boolen
     */
    private boolean isJsonArray(String content) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 字符串键值对转成map
     *
     * @param paramters
     * @param splitChar1 一级分隔符，将字符串分成多个键值对key-value
     * @param splitChar2 二级分隔符，分割key和value
     * @return
     */
    private Map<String, String> str2map(String paramters, String splitChar1, String splitChar2) {
        if (paramters.isEmpty()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] paramArray = paramters.split(splitChar1);
        for (String param : paramArray) {
            String[] array = param.split(splitChar2);
            map.put(array[0], array[1]);
        }
        return map;
    }

}
