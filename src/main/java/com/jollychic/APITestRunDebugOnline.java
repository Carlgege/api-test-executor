package com.jollychic;


import com.jollychic.exec.JsonExecutor;
import com.jollychic.utils.JsonTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestNG;

/**
 * 在服务端调试用例的方法
 * arg0: 用例
 * arg1: 全局参数
 */
@Slf4j
public class APITestRunDebugOnline {

    private String apiTestCaseStr;
    private String globalParametersStr;

    public APITestRunDebugOnline(String apiTestCaseStr, String globalParametersStr) {
        this.apiTestCaseStr = apiTestCaseStr;
        this.globalParametersStr = globalParametersStr;
    }

    public void apiDebugOnline() {
        JsonTestUtils.debugOnline(apiTestCaseStr, globalParametersStr);
        JsonTestUtils.getTestCase();


        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[]{JsonExecutor.class});
        testNG.run();
    }
}
