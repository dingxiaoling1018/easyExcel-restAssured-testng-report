package com.saber.api.autotest.utils;

import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

public class ReportUtil {
    private static String reportName = "自动化测试报告";

    private static String splitTimeAndMsg = "===";

    public static void log(String msg) {
        Reporter.log(msg, true);
    }

    public static String getReportName() {
        return reportName;
    }

    public static String getSpiltTimeAndMsg() {
        return splitTimeAndMsg;
    }

    public static void setReportName(String reportName) {
        if(StringUtils.isNotEmpty(reportName)){
            ReportUtil.reportName = reportName;
        }
    }
}

