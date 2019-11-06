package com.saber.api.autotest.utils;

import com.saber.api.autotest.model.TestCaseModel;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

import static io.restassured.RestAssured.given;

/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/8/27 18:01
 */
public class RestAssurdPostRequestUtil {

    /**
     * 获取Message
     * @param ApiUrl
     * @param Param
     * @return Message
     */
    public static String getMessag(String ApiUrl, String Param){

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .body(Param)
                .when()
                .post(ApiUrl);

        String Message = response.jsonPath().get("message");
        Reporter.log("message: " + Message);

        return Message;
    }

    /**
     * 获取StatusCode
     * @param ApiUrl
     * @param Param
     * @return StatusCode
     */
    public static int getStatusCode(String ApiUrl, String Param){

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .body(Param)
                .when()
                .post(ApiUrl);

        int StatusCode = response.jsonPath().get("status_code");
        Reporter.log("status_code:" + StatusCode);

        return StatusCode;
    }

    /**
     * 获取返回body
     * @param ApiUrl
     * @param Param
     * @return JsonResult
     */
    public static Response getJsonResult(String ApiUrl, String Param){

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .request()
                .body(Param)
                .when()
                .post(ApiUrl);

        String JsonResult = response.asString();
        Reporter.log("返回的值JsonResult:" + JsonResult);
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

    public static Response getResponse(TestCaseModel data){
        if (StringUtils.isNotBlank(data.getHeaders()) ) {
            return getResponseOfHeader(getRequestUrl(data), data.getBody(), data.getHeaders());
        }
        return getJsonResult(getRequestUrl(data), data.getBody());

    }

    private static String getRequestUrl(TestCaseModel data) {
        if(data.getApiUrl() != null && data.getBody() != null){
            return data.getApiUrl() + "?" + data.getBody();
        }
        return data.getApiUrl();
    }

    /**
     * 获取返回response
     * @param ApiUrl
     * @param body
     * @return JsonResult
     */
    public static Response getResponseOfCookie(String ApiUrl, String body, String cookie){

        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .cookie(cookie)
                .request()
                .body(body)
                .when()
                .post(ApiUrl);

        Reporter.log("返回的值response:" + response.asString());
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

    public static Response getResponseOfCookieHeader(String ApiUrl, String body, String cookie, String header){

        String headerName = org.apache.commons.lang3.StringUtils.substringBefore(header,"=");
        String headerValue = org.apache.commons.lang3.StringUtils.substringAfter(header,"=");
        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .header(headerName, headerValue)
                .cookie(cookie)
                .request()
                .body(body)
                .when()
                .post(ApiUrl);

        Reporter.log("返回的值response:" + response.asString());
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

    public static Response getResponseOfHeader(String ApiUrl, String body, String header){

        String headerName = org.apache.commons.lang3.StringUtils.substringBefore(header,"=");
        String headerValue = org.apache.commons.lang3.StringUtils.substringAfter(header,"=");
        Response response = given()
                .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))
                .contentType("application/json")
                .log().all()
                .header(headerName, headerValue)
                .request()
                .body(body)
                .when()
                .post(ApiUrl);

        Reporter.log("返回的值response:" + response.asString());
        System.out.println("返回的值JsonResult:" + response.asString());
        return response;
    }

}
