package com.saber.api.autotest;


import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.beust.jcommander.internal.Lists;
import com.saber.api.autotest.constants.TestCaseInfoConstant;
import com.saber.api.autotest.listener.AutoTestListener;
import com.saber.api.autotest.listener.RetryListener;
import com.saber.api.autotest.model.TestCaseModel;
import com.saber.api.autotest.service.TestCaseService;
import com.saber.api.autotest.utils.ExcelListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author DingXiaoLing
 * @version 1.0
 * @date 2019/9/2 10:37
 */
@SpringBootTest
@Slf4j
@Component
@Listeners({AutoTestListener.class, RetryListener.class})
public class RunTestCaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestCaseService testCaseService;

    @Value("${test.case.file}")
    private String TEST_CASE_FILE;

    @DataProvider(name = "testData")
    public Object[][] batchData() {
        Object[][] testCase = new Object[0][];
        try {
            List<Object> datalist = readExcle(TEST_CASE_FILE);

            testCase = new Object[datalist.size()][];
            for (int i = 0; i < datalist.size(); i++) {
                testCase[i] = new Object[]{datalist.get(i)};
            }
        } catch (Exception e) {
            log.error("异常信息，【{}】", e);
        }
        return testCase;

    }

    @Test(dataProvider = "testData")
    public void senceTest(TestCaseModel data) {

        try {
            log.info("---执行自动化测试开始---");
            if (StringUtils.isEmpty(data.getNo()) || TestCaseInfoConstant.IGNORE_FLAG.equals(data.getIgnoreFlag())) {
                return;
            }

            testCaseService.runTest(data);


            log.info("---执行自动化测试结束---");

        } catch (Exception e) {
            log.error("--执行自动化测试异常，异常信息：【{}】----", e);
        }

    }

    private static List<Object> readExcle(String readPath) {
        List<Object> allScenes = new ArrayList();

        try {
            ExcelListener excelListener = new ExcelListener();
            ExcelReader reader = getReader(readPath, excelListener);

            for (Sheet sheet1 : reader.getSheets()) {


                Sheet sheet = new Sheet(sheet1.getSheetNo(), 1, TestCaseModel.class);
                List<Object> readList = EasyExcelFactory.read(new FileInputStream(readPath), sheet);
                allScenes.addAll(readList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allScenes;


    }

    /**
     * 返回 ExcelReader
     *
     * @param readPath      需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(String readPath,
                                         ExcelListener excelListener) {

        try {
            InputStream inputStream = new FileInputStream(readPath);
            return new ExcelReader(inputStream, null, excelListener, false);
        } catch (IOException e) {
        }
        return null;
    }


}
