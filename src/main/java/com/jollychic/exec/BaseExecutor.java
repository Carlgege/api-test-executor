package com.jollychic.exec;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.jollychic.bean.APITestCase;
import com.jollychic.bean.APITestDynamicParameters;
import com.jollychic.bean.ResponseAssert;
import com.jollychic.enums.Har;
import com.jollychic.listeners.InterfaceFailureHandle;
import com.jollychic.utils.DatabaseUtils;
import com.jollychic.utils.JsonTestUtils;
import com.jollychic.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.TestCaseEvent;
import ru.yandex.qatools.allure.model.Parameter;
import ru.yandex.qatools.allure.model.ParameterKind;
import ru.yandex.qatools.allure.model.TestCaseResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.jollychic.enums.ResAssertCondition.IS;
import static com.jollychic.enums.ResAssertCondition.NOT;
import static com.jollychic.utils.JsonTestUtils.GET_VALUE_FROM_TC_RESULT;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public abstract class BaseExecutor implements IAPITestExec{

    APITestCase testCase;
    List<String> sqlSplitList = new ArrayList<>();
    final static int LINUX_TIME_LENGTH = 11;

    @BeforeClass
    void init() {
        testCase = JsonTestUtils.getCurrentTestCase();
    }

    @Override
    public void updateRequestWithSql() {
        if (testCase.getBeforeSql() != null) {
            testCase.getBeforeSql().forEach((v) -> {
                String sql = v.getSqlStatement();
                if (sql.contains(GET_VALUE_FROM_TC_RESULT)) {
                    sqlSplitList = Arrays.asList(sql.split("\\$\\$"));
                    for (String sqlSplit : sqlSplitList) {
                        if (sqlSplit.startsWith("{")) {
                            String[] testCaseInfo = sqlSplit.substring(1, sqlSplit.length() - 1).split("\\:");
                            String testCaseParameter;
                            JSONObject tempJ = new JSONObject();
                            if ("this".equals(testCaseInfo[0])) {
                                tempJ.putAll(testCase.getRequestParameters());
                            } else {
                                tempJ.putAll(JsonTestUtils.getTestCase(testCaseInfo[0]).getRequestParameters());
                            }
                            testCaseParameter = JsonPath.parse(tempJ.toJSONString()).read(testCaseInfo[1]).toString();
                            sql = sql.replace(GET_VALUE_FROM_TC_RESULT + sqlSplit + GET_VALUE_FROM_TC_RESULT, testCaseParameter);
                        }
                    }
                }
                /** 0表示查询语句，1表示非查询语句 **/
                if ("select".equals(v.getSqlType())) {
                    JSONObject reqJSONObject = new JSONObject();
                    reqJSONObject.putAll(testCase.getRequestParameters());
                    String reqJSON = reqJSONObject.toJSONString();
                    String result = JsonPath.parse(reqJSON).set(v.getLocalJsonPath(), DatabaseUtils.executeQueryResult(v.getDbURL(), v.getDbUser(), v.getDbPassword(), sql, v.getColumnName())).jsonString();

                    org.json.JSONObject resultJ = new org.json.JSONObject(result);
                    testCase.getRequestParameters().putAll(resultJ.toMap());
                } else {
                    DatabaseUtils.executeSql(v.getDbURL(), v.getDbUser(), v.getDbPassword(), sql);
                }
            });
        }
    }

    @Override
    public void updateRequestWithRegular() {

        testCase.getRequestParameters().forEach((k, v) -> {
            String value = v.toString();

            if (value.startsWith(JsonTestUtils.GEN_VALUE)) {
                String content;
                StringBuilder result = new StringBuilder();
                String temp;
                content = value.substring(3, value.indexOf("}"));
                result.append(content.substring(0, content.indexOf("<")));
                if (value.contains("linuxTime")) {
                    int bit = Integer.parseInt(content.substring(content.indexOf(">") + 1));
                    temp = "" + System.currentTimeMillis() / 1000;

                    if (bit < LINUX_TIME_LENGTH) {
                        temp = temp.substring(temp.length() - bit);
                    } else {
                        for (int i = LINUX_TIME_LENGTH; i < bit; i++) {
                            result.append("1");
                        }
                    }
                    result.append(temp);
                    result.append(value.substring(value.indexOf("}") + 1));
                    testCase.getRequestParameters().put(k, result.toString());
                }
            }
        });
    }

    @Override
    public void updateRequestWithReferenceCase() {
        if (testCase.getUpdateRequestParameters() != null && testCase.getUpdateRequestParameters().size() > 0) {
            JSONObject reqJSONObject = new JSONObject();
            reqJSONObject.putAll(testCase.getRequestParameters());
            String reqJSON = reqJSONObject.toJSONString();
            for (APITestDynamicParameters dynamicParameters : testCase.getUpdateRequestParameters()) {
                if (Har.REQUEST.toString().equals(dynamicParameters.getReferenceBodyType())) {
                    /** 在debug用例时，判断一下被依赖的用例是否有response，以判断它是否执行过，如果没执行过，可以尝试从redis取结果 **/
                    Map<String, Object> remoteReqParameters;
                    if (JsonTestUtils.isLocalDebug) {
                        String cachedCaseStr = RedisUtils.getRedis().get(dynamicParameters.getReferenceCaseName());
                        APITestCase cachedCase = JSON.parseObject(cachedCaseStr, APITestCase.class);
                        remoteReqParameters = cachedCase.getRequestParameters();
                    } else {
                        remoteReqParameters = JsonTestUtils.getTestCase(dynamicParameters.getReferenceCaseName()).getRequestParameters();
                    }
                    JSONObject reqJ = new JSONObject();
                    reqJ.putAll(remoteReqParameters);
                    Object remoteObj = JsonPath.parse(reqJ.toJSONString()).read(dynamicParameters.getRemoteJsonPath());

                    String result = JsonPath.parse(reqJSON).set(dynamicParameters.getLocalJsonPath(), remoteObj).jsonString();
                    org.json.JSONObject resultJ = new org.json.JSONObject(result);
                    testCase.getRequestParameters().putAll(resultJ.toMap());
                } else if (Har.RESPONSE.toString().equals(dynamicParameters.getReferenceBodyType())) {
                    String remoteBody;
                    if (JsonTestUtils.isLocalDebug) {
                        String cachedCaseStr = RedisUtils.getRedis().get(dynamicParameters.getReferenceCaseName());
                        APITestCase cachedCase = JSON.parseObject(cachedCaseStr, APITestCase.class);
                        remoteBody = cachedCase.getResponseBody();
                    } else {
                        remoteBody = JsonTestUtils.getTestCase(dynamicParameters.getReferenceCaseName()).getResponseBody();
                    }
                    remoteBody = remoteBody.substring(remoteBody.indexOf("{"), remoteBody.lastIndexOf("}") + 1);
                    Object remoteObj = JsonPath.parse(remoteBody).read(dynamicParameters.getRemoteJsonPath());

                    String result = JsonPath.parse(reqJSON).set(dynamicParameters.getLocalJsonPath(), remoteObj).jsonString();
                    reqJSON = result;
                    org.json.JSONObject resultJ = new org.json.JSONObject(result);
                    testCase.getRequestParameters().putAll(resultJ.toMap());
                }
            }
        }
    }

    @Override
    public abstract void updateRequestUrl();

    private String updateTestCaseName(int caseIndex) {
        String caseIn = String.valueOf(caseIndex);
        int length = caseIn.length();
        for (int i = 0; i < 3 - length; i++) {
            caseIn = "0" + caseIn;
        }
        return caseIn;
    }

    /**
     * 更新测试用例的名字和描述信息
     */
    void updateTestCaseInfoBefore() {

        Allure.LIFECYCLE.fire(new TestCaseEvent() {
            @Override
            public void process(TestCaseResult testCaseResult) {

                testCaseResult.setName(updateTestCaseName(JsonTestUtils.getCurrentCaseIndex()) + "--" + testCase.getName());
                ru.yandex.qatools.allure.model.Description description = new ru.yandex.qatools.allure.model.Description();
                description.setValue(testCase.getDescription());
                testCaseResult.setDescription(description);

                log.debug(testCase.getName());
                log.debug(testCase.getDescription());

                testCaseResult.getLabels().forEach((k) -> {
                    if ("testClass".equals(k.getName())) {
                        k.setValue(testCase.getName());
                    } else if ("testMethod".equals(k.getName())) {
                        k.setValue(testCase.getDescription());
                    }
                });
            }
        });
    }

    void updateTestCaseInfoAfter() {
        Allure.LIFECYCLE.fire(new TestCaseEvent() {
            @Override
            public void process(TestCaseResult testCaseResult) {
                List<Parameter> parameterList = new ArrayList<>();
                Parameter request = new Parameter();
                request.setName("Request");
                request.setValue(JSON.toJSONString(JsonTestUtils.getCurrentTestCase().getRequestParameters()));
                request.setKind(ParameterKind.ARGUMENT);

                Parameter response = new Parameter();
                response.setName("Response");
                response.setValue(JsonTestUtils.getCurrentTestCase().getResponseBody());
                response.setKind(ParameterKind.ARGUMENT);

                Parameter url = new Parameter();
                url.setName("URL");
                url.setValue(testCase.getApiUrl());
                url.setKind(ParameterKind.ARGUMENT);

                parameterList.add(url);
                parameterList.add(request);
                parameterList.add(response);

                testCaseResult.setParameters(parameterList);
            }
        });
    }

    @Override
    public void responseCodeAssert(Response response) {
        assertThat(response.code()).as("verify response code.").isEqualTo(testCase.getResponseCode()).descriptionText();
    }

    @Override
    public void responseValueAssert(List<ResponseAssert> responseAssertList, String resBody) {
        responseAssertList.forEach((v) -> {
            String temp = "The jsonpath does not exist";
            try {
                temp = JsonPath.parse(resBody).read(v.getJsonPath()).toString();
            } catch (Exception e) {
                e.printStackTrace();
                //skip
            }
            if (IS.toString().equalsIgnoreCase(v.getAssertCondition())) {
                assertThat(temp).as("verify " + v.getJsonPath()).isEqualTo(v.getValue().toString());
            } else if (NOT.toString().equalsIgnoreCase(v.getAssertCondition())) {
                assertThat(temp).as("verify " + v.getJsonPath()).doesNotContain(v.getValue().toString());
            }
        });
    }
}
