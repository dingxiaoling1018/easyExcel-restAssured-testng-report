package com.saber.api.autotest.service;

import com.saber.api.autotest.constants.PropertiesConstant;
import com.saber.api.autotest.constants.TestCaseInfoConstant;
import com.saber.api.autotest.model.TestCaseModel;
import com.saber.api.autotest.utils.RestAssurdGetRequestUtil;
import com.saber.api.autotest.utils.RestAssurdPostRequestUtil;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.testng.Reporter;

@Service
public class TestCaseService {

    @Autowired
    private Environment environment;
    @Autowired
    private InitTestCaseService initTestCaseService;
    @Autowired
    private VerifyResponseService verifyResponseService;
    @Autowired
    private ClearDataService clearDataService;

    // 执行每个测试用例
    public void runTest(TestCaseModel testCase) {
        try {
            // 1.拼接url
            testCase = packageApiUrl(testCase);

            // 2.准备工作，是否向数据库添加测试数据
            testCase = initTestCaseService.initTestCase(testCase);

            // 3.判断请求方式，拼接参数并发送请求
            Response response = sendRequest(testCase);

            // 4.校验请求返回数据
            verifyResponseService.verifyResponse(response, testCase);

            //5.校验数据库数据
            verifyResponseService.verifyMysql(testCase.getExpectationsMysql());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearDataService.clear(testCase);
        }
    }

    private TestCaseModel packageApiUrl(TestCaseModel testCase) {
        if (testCase.getApiUrl().startsWith(TestCaseInfoConstant.HTTP_START)) {
            return testCase;
        }

        if (testCase.getApiUrl().startsWith(TestCaseInfoConstant.CHAR_PARAMETER)) {
            testCase.setApiUrl(environment.getProperty(PropertiesConstant.API_URI) + testCase.getApiUrl());
        }
        return testCase;
    }

    private Response sendRequest(TestCaseModel data) {
        Response response = null;
        String method = data.getMethod().toLowerCase();
        switch (method) {
            case "post":
                response = RestAssurdPostRequestUtil.getResponse(data);
                break;
            case "get":
                response = RestAssurdGetRequestUtil.getResponse(data);
                break;
            default:
                Reporter.log("错误的请求方式");
                break;
        }
        return response;
    }
}
