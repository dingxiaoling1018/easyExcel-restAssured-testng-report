package com.saber.api.autotest.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/8/27 18:01
 */
@Data
@Getter
@Setter
public class TestCaseModel extends BaseRowModel {

    @ExcelProperty(value = {"用例编号", "no"} ,index = 0)
    public String no;

    @ExcelProperty(value = {"用例描述","Desc"},index = 1)
    public String desc;

    @ExcelProperty(value = {"API地址","apiUrl"},index = 2)
    public String apiUrl;

    @ExcelProperty(value = {"请求方式","method"},index = 3)
    public String method;

    @ExcelProperty(value = {"请求格式","ParamsType"},index = 4)
    public String paramsType;

    @ExcelProperty(value = {"请求参数","body"},index = 5)
    public String body;

    @ExcelProperty(value = {"Headers","Headers"},index = 6)
    public String headers;

    @ExcelProperty(value = {"数据库","Mysql"},index = 7)
    public String mysql;

    @ExcelProperty(value = {"MongoDB", "MongoDB"},index = 8)
    public String mongoDB;

    @ExcelProperty(value = {"Redis", "Redis"},index = 9)
    public String redis;

    @ExcelProperty(value = {"期望的mysql验证", "ExpectationsMysql"},index = 10)
    public String expectationsMysql;

    @ExcelProperty(value = {"期望的返回验证", "ExpectationsResponse"},index = 11)
    public String expectationsResponse;

    @ExcelProperty(value = {"清理mysql", "ClearMysql"},index = 12)
    public String clearMysql;

    @ExcelProperty(value = {"清理redis", "ClearRedis"},index = 13)
    public String clearRedis;

    @ExcelProperty(value = {"是否忽略", "ignoreFlag"},index = 14)
    public String ignoreFlag;

}
