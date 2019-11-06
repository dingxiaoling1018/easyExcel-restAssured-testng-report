package com.saber.api.autotest.utils;

import com.saber.api.autotest.model.TestCaseModel;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

import static io.restassured.RestAssured.given;

public class RestAssurdGetRequestUtil {
    /**
     * 指定API接口URL,GET请求参数,获取Token
     *
     * @param ApiUrl
     * @return Token
     */
    public static String getJsonDataToken(String ApiUrl) {
        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .when()
                .get(ApiUrl);

        String Token = response.jsonPath().get("data.token");//获取单个值
        System.out.println(Token);

        return Token;
    }

    /**
     * 指定API接口URL,GET请求参数,获取JsonResult
     *
     * @param ApiUrl
     * @param Param
     * @return JsonResult
     */
    public static String getJsonResult(String ApiUrl, String Param) {

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .params("token", Param)
                .when()
                .get(ApiUrl);

        String JsonResult = response.asString();
        Reporter.log("返回的值JsonResult:" + JsonResult);
        System.out.println("返回的值JsonResult:" + response.asString());
        return JsonResult;
    }

    public static Response getResponse(String ApiUrl) {

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .when()
                .get(ApiUrl);

        String JsonResult = response.asString();
        Reporter.log("返回的值JsonResult:" + JsonResult);
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

    public static String getJsonResult(String ApiUrl) {

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .when()
                .get(ApiUrl);

        String JsonResult = response.asString();
        Reporter.log("返回的值JsonResult:" + JsonResult);
        System.out.println("返回的值JsonResult:" + response.asString());
        return JsonResult;
    }


    /**
     * 获取返回结果
     * @param data
     * @return
     */
    public static Response getResponse(TestCaseModel data){
        if (StringUtils.isNotBlank(data.getHeaders())) {
            return getResponseOfHeader(getRequestUrl(data), data.getHeaders());
        }

        return getResponse(getRequestUrl(data));

    }

    private static String getRequestUrl(TestCaseModel data) {
        if(data.getApiUrl() != null && data.getBody() != null){
            return data.getApiUrl() + "?" + data.getBody();
        }
        return data.getApiUrl();
    }

    public static Response getResponseOfHeader(String ApiUrl, String header) {
        String headerName = org.apache.commons.lang3.StringUtils.substringBefore(header,"=");
        String headerValue = org.apache.commons.lang3.StringUtils.substringAfter(header,"=");
        Response response =   given()
                    .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                    .contentType("application/json")
                    .log().all()
                    .headers(headerName, headerValue)
                    .request()
                    .when()
                    .get(ApiUrl);

        Reporter.log("返回的值JsonResult:" + response.asString());
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

}