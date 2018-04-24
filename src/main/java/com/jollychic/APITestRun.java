package com.jollychic;


import com.alibaba.fastjson.JSON;
import com.jollychic.bean.APITestCase;
import com.jollychic.bean.APITestResult;
import com.jollychic.enums.TestResult;
import com.jollychic.exec.JsonExecutor;
import com.jollychic.utils.DingTalkUtils;
import com.jollychic.utils.HttpUtils;
import com.jollychic.utils.JsonTestUtils;
import com.jollychic.utils.WeiXinUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestNG;

import java.util.HashMap;
import java.util.Map;

import static com.jollychic.enums.MainParameters.*;
import static com.jollychic.utils.JsonTestUtils.analyseProject;
import static com.jollychic.utils.TimeUtils.timeStamp2Date;

/**
 * 读取配置文件并执行所有测试用例
 */
@Slf4j
public class APITestRun {

    private static Integer taskId = 0;

    public static void main(String[] args) {

        String apiTestProjectStr;
        Map<String, String> parameters = new HashMap<>();

        for (String param : args) {
            String[] paramKeyValue = param.split("=");
            parameters.put(paramKeyValue[0], paramKeyValue[1]);
        }

        /**
         * 有LOCAL_JSON_FILE_PATH，则读取本地json文件
         * 有TASK_ID，则从服务端获取用例
         * 都没有，则加载默认文件
         */
        if (parameters.containsKey(LOCAL_JSON_FILE_PATH.toString())) {
            apiTestProjectStr = JsonTestUtils.loadCases(parameters.get(LOCAL_JSON_FILE_PATH.toString()));
        } else if (parameters.containsKey(TASK_ID.toString())) {
            taskId = Integer.parseInt(parameters.get(TASK_ID.toString()));
            JsonTestUtils.APITestServerAddr = parameters.get(SERVER_ADDR.toString());
            JsonTestUtils.notificationUsers = parameters.get(NOTIFICATION_USERS.toString());
            apiTestProjectStr = HttpUtils.get(JsonTestUtils.APITestServerAddr + "/task/taskId/" + parameters.get(TASK_ID.toString()));
        } else {
            apiTestProjectStr = JsonTestUtils.loadCases("/App2APITestProject_release.json");
        }

        analyseProject(apiTestProjectStr);

        for (Map.Entry<Integer, APITestCase> oneTC : JsonTestUtils.getAllTestCaseMap().entrySet()) {
            log.debug(oneTC.getValue().getName() + " : " + oneTC.getValue().getDescription());
            TestNG testNG = new TestNG(false);
            testNG.setSuiteThreadPoolSize(1);
            testNG.setThreadCount(1);
            testNG.setTestClasses(new Class[]{JsonExecutor.class});
            JsonTestUtils.getTestCase();
//            if (isNeedRun()) {
                testNG.run();
//            }
        }

        int failTotal = 0;
        int skipTotal = 0;
        StringBuilder failMsg = new StringBuilder();
        failMsg.append("# ["+JsonTestUtils.getApiTestProject().getProjectName()+"接口运行结果](http://ip:port/)");
        for (APITestResult apiTestResult : JsonTestUtils.getApiTestResultList()) {
            if (TestResult.FAIL.toString().equals(apiTestResult.getResult())) {
                failTotal++;
                failMsg.append("\n## Name: \n");
                failMsg.append(apiTestResult.getName());
                failMsg.append("\n## Started on: \n");
                failMsg.append(timeStamp2Date(apiTestResult.getStartTime()));
                failMsg.append("\n## Request body: \n");
                failMsg.append(JSON.toJSONString(JsonTestUtils.getTestCase(apiTestResult.getName()).getRequestParameters()));
                failMsg.append("\n## Response: \n");
                failMsg.append(apiTestResult.getResBody());
                failMsg.append("\n## Error info: \n");
                failMsg.append(apiTestResult.getErrorInfo());
                failMsg.append("\n## Used time: \n");
                failMsg.append(apiTestResult.getEndTime() - apiTestResult.getStartTime());
                failMsg.append("ms\n\n");
            } else if (TestResult.SKIP.toString().equals(apiTestResult.getResult())) {
                skipTotal++;
            }
        }
        failMsg.append("\n## Total: ");
        failMsg.append(JsonTestUtils.getApiTestResultList().size());
        failMsg.append("\n## Fail: ");
        failMsg.append(failTotal);
        failMsg.append("\n## Skip: ");
        failMsg.append(skipTotal);
        failMsg.append("\n");

        if (failTotal > 0) {
            DingTalkUtils.sendDingMessage("{\"msgtype\": \"markdown\", \"markdown\": {\"title\": \"接口测试运行结果\",  \"text\":\"" + failMsg.toString().replaceAll("\"", "\\\\\"") + "\"}}");
            WeiXinUtils.sendMessage(JsonTestUtils.notificationUsers, failMsg.toString().replaceAll("\"", "\\\\\"").replaceAll("## |# ", ""));
        }

        if (taskId > 0) {
            System.out.println("upload result to server");
            HttpUtils.post(JsonTestUtils.APITestServerAddr + "/task/taskId/" + parameters.get(TASK_ID.toString()), JSON.toJSONString(JsonTestUtils.getApiTestResultList()));
        }

    }
}
