package com.jollychic.utils;


import com.alibaba.fastjson.JSON;
import com.jollychic.bean.APITestCase;
import com.jollychic.bean.APITestDynamicParameters;
import com.jollychic.bean.APITestProject;
import com.jollychic.bean.APITestResult;
import com.jollychic.enums.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class JsonTestUtils {

    public static boolean isLocalDebug = false;
    public static String APITestServerAddr = "http://172.31.9.189:9980/autotestserver";
    public static final String DEBUG_RESULT_JSON = "debugResult.json";
    public static String notificationUsers = "1104";

    /**
     * 从指定用例的request中取值
     **/
    public static String GET_VALUE_FROM_TC_REQUEST = "##";
    /**
     * 从指定用例的response中取值
     **/
    public static String GET_VALUE_FROM_TC_RESULT = "$$";
    /**
     * 通过伪正则生成测试数据
     **/
    public static String GEN_VALUE = "%%";
    private static APITestProject apiTestProject;
    private static Map<Integer, APITestCase> allTestCaseMap = new LinkedHashMap<>();
    private static Map<Integer, APITestCase> allTestCaseRunMap = new LinkedHashMap<>();
    private static List<APITestResult> apiTestResultList = new ArrayList<>();
    private static Integer caseIndex = 0;

    private JsonTestUtils() {

    }

    public static String loadCases(String jsonFileName) {

        jsonFileName = APITestProject.class.getResource(jsonFileName).getPath();
        log.debug("jsonFileName:" + jsonFileName);

        String result = "";
        try {
            result = FileUtils.readFileToString(new File(jsonFileName), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void analyseProject(String apiTestProjectStr) {
        apiTestProject = JSON.parseObject(apiTestProjectStr, APITestProject.class);

        int caseNum = 0;
        for (APITestCase apiTestCase : apiTestProject.getTestCaseList()) {
            allTestCaseMap.put(caseNum++, apiTestCase);
        }
    }

    public static void debugOnline(String apiTestCaseStr, String globalParametersStr) {
        JSONObject globalParameters = new JSONObject(globalParametersStr);
        apiTestProject = new APITestProject();
        apiTestProject.setGlobalRequestParameters(globalParameters.toMap());
        caseIndex = 0;
        allTestCaseMap.put(caseIndex, JSON.parseObject(apiTestCaseStr, APITestCase.class));
        apiTestResultList.clear();
    }

    public static List<APITestResult> getApiTestResultList() {
        return apiTestResultList;
    }

    public static APITestResult getTestResult(String caseName) {
        for (APITestResult apiTestResult : apiTestResultList) {
            if (caseName.equals(apiTestResult.getName())) {
                return apiTestResult;
            }
        }
        return null;
    }

    public static Map<Integer, APITestCase> getAllTestCaseMap() {
        return allTestCaseMap;
    }

    public static Map<Integer, APITestCase> getAllTestCaseRunMap() {
        return allTestCaseRunMap;
    }

    public static APITestCase getTestCase() {
        return allTestCaseMap.get(caseIndex++);
    }

    public static Integer getCurrentCaseIndex() {
        return caseIndex - 1;
    }

    public static APITestCase getCurrentTestCase() {
        return allTestCaseMap.get(caseIndex - 1);
    }

    public static APITestResult getCurrentTestCaseResult() {
        return apiTestResultList.get(caseIndex - 1);
    }

    public static APITestProject getApiTestProject() {
        return apiTestProject;
    }

    public static APITestCase getTestCase(String testCaseName) {
        APITestCase apiTestCase = null;
        for (Map.Entry<Integer, APITestCase> oneCase : allTestCaseMap.entrySet()) {
            if (testCaseName.equals(oneCase.getValue().getName())) {
                apiTestCase = oneCase.getValue();
                break;
            }
        }
        return apiTestCase;
    }

    public static boolean isNeedRun() {
        boolean isRun = true;
        List<APITestDynamicParameters> apiTestDynamicParametersList = getCurrentTestCase ().getUpdateRequestParameters();
        if (apiTestDynamicParametersList != null) {
            for (APITestDynamicParameters apiTestDynamicParameters : apiTestDynamicParametersList) {
                APITestResult apiTestResult = getTestResult(apiTestDynamicParameters.getReferenceCaseName());
                if (apiTestResult == null || TestResult.FAIL.toString().equals(apiTestResult.getResult())) {
                    isRun = false;
                    break;
                }
            }
        }

        return isRun;
    }

//    public static void runOneCase() {
//
//    }

}
