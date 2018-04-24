package com.jollychic.listeners;

import com.alibaba.fastjson.JSON;
import com.jollychic.bean.APITestResult;
import com.jollychic.enums.TestResult;
import com.jollychic.utils.JsonTestUtils;
import com.jollychic.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.jollychic.utils.JsonTestUtils.DEBUG_RESULT_JSON;


/**
 * @author chenlg
 */
@Slf4j
public class InterfaceFailureHandle extends TestListenerAdapter {

    private APITestResult getAPITestResult(ITestResult iTestResult, String result) {
        APITestResult apiTestResult = new APITestResult();
        Throwable throwable = iTestResult.getThrowable();
        if (throwable != null) {
            log.debug(ExceptionUtils.getStackTrace(throwable));
            apiTestResult.setErrorInfo(throwable.getMessage());
        }
        apiTestResult.setResult(result);
        apiTestResult.setName(JsonTestUtils.getCurrentTestCase().getName());
        String resBody = JsonTestUtils.getCurrentTestCase().getResponseBody();
        apiTestResult.setResBody(resBody == null ? "" : resBody);
        apiTestResult.setStartTime(iTestResult.getStartMillis());
        apiTestResult.setEndTime(iTestResult.getEndMillis());
        apiTestResult.setRunTime(iTestResult.getEndMillis() - iTestResult.getStartMillis());

        return apiTestResult;
    }

    /**
     * 保存用例结果到文件
     */
    private void saveResult(ITestResult iTestResult, String result) {

        APITestResult apiTestResult = getAPITestResult(iTestResult, result);
        if (JsonTestUtils.isLocalDebug) {
            try {
                FileUtils.writeStringToFile(new File(DEBUG_RESULT_JSON), JSON.toJSONString(apiTestResult), "UTF-8", true);
                FileUtils.writeStringToFile(new File(DEBUG_RESULT_JSON), "\n", "UTF-8", true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RedisUtils.getRedis().set(JsonTestUtils.getCurrentTestCase().getName(), JSON.toJSONString(JsonTestUtils.getCurrentTestCase()));
        }
        JsonTestUtils.getApiTestResultList().add(apiTestResult);
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
//        log.debug("onTestStart");

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        log.debug("onTestSuccess");
        saveResult(iTestResult, TestResult.PASS.toString());
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        log.debug("onTestFailure");
        log.debug("ErrorInfo:\r\n" + ExceptionUtils.getStackTrace(iTestResult.getThrowable()));
        saveResult(iTestResult, TestResult.FAIL.toString());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        log.debug("onTestSkipped");
        saveResult(iTestResult, TestResult.SKIP.toString());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        log.debug("onTestFailedButWithinSuccessPercentage");
    }

    @Override
    public void onStart(ITestContext iTestContext) {
//        log.debug("onStart");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {

        super.onFinish(iTestContext);
        log.debug("onFinish");

        // List of test results which we will delete later
        ArrayList<ITestResult> testsToBeRemoved = new ArrayList<>();
        // collect all id's from passed test
        Set<Integer> passedTestIds = new HashSet<>();
        for (ITestResult passedTest : iTestContext.getPassedTests().getAllResults()) {
            log.debug("PassedTests = " + passedTest.getName());
            passedTestIds.add(getId(passedTest));
        }

        Set<Integer> failedTestIds = new HashSet<>();
        for (ITestResult failedTest : iTestContext.getFailedTests().getAllResults()) {
            log.debug("failedTest = " + failedTest.getName());
            int failedTestId = getId(failedTest);

            // if we saw this test as a failed test before we mark as to be
            // deleted
            // or delete this failed test if there is at least one passed
            // version
            if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestIds.add(failedTestId);
            }
        }

        // finally delete all tests that are marked
        for (Iterator<ITestResult> iterator = iTestContext.getFailedTests()
                .getAllResults().iterator(); iterator.hasNext(); ) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                log.debug("Remove repeat Fail Test: " + testResult.getName());
                iterator.remove();
            }
        }
    }

    private int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = id + result.getMethod().getMethodName().hashCode();
        id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
    }

}
