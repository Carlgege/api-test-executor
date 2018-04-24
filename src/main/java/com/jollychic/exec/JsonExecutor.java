package com.jollychic.exec;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jollychic.enums.Har;
import com.jollychic.enums.ResAssertKey;
import com.jollychic.listeners.InterfaceFailureHandle;
import com.jollychic.listeners.impl.Retry;
import com.jollychic.sign.SignGenerate;
import com.jollychic.utils.HttpUtils;
import com.jollychic.utils.JsonTestUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author chenlg
 */
@Slf4j
@Listeners({InterfaceFailureHandle.class, Retry.class})
public class JsonExecutor extends BaseExecutor {

    public static final String REQ_TIME_STAMP = "appTimestamp";

    /**
     * get请求接口需要拼接参数到url
     */
    @Override
    public void updateRequestUrl() {
        if (testCase.getRequestParameters() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(testCase.getApiUrl());
            sb.append("?");

            testCase.getRequestParameters().forEach((k, v) -> {
                sb.append(k);
                sb.append("=");
                sb.append(v);
                sb.append("&");
            });
            sb.setLength(sb.length() - 1);
            testCase.setApiUrl(sb.toString());
        }
    }

    @Test
    public void executor() {

        updateTestCaseInfoBefore();

        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(JsonTestUtils.getApiTestProject().getGlobalRequestParameters());

        if (null != testCase.getRequestParameters()) {
            try {
                updateRequestWithRegular();

                updateRequestWithReferenceCase();

                updateRequestWithSql();
            } catch (Exception e) {
                throw new SkipException("Skip This test because update the request parameters failed", e);
            }

            if (jsonObject.containsKey(REQ_TIME_STAMP)) {
                jsonObject.put(REQ_TIME_STAMP, System.currentTimeMillis());
            }
            JsonTestUtils.getCurrentTestCase().getRequestParameters().putAll(testCase.getRequestParameters());
            jsonObject.putAll(testCase.getRequestParameters());
        }

        if (Har.POST.toString().equals(testCase.getMethod())) {
            String sign = SignGenerate.getSign(jsonObject);
            jsonObject.put("sign", sign);
        } else {
            updateRequestUrl();
        }

//        log.debug(jsonObject.toJSONString());

        /** 是否有delete请求？**/
        Response response;

        /** 如果发生连接超时异常，捕获它，并再试一次 **/
        try {
            if (Har.POST.toString().equals(testCase.getMethod())) {
                response = HttpUtils.getPostResponse(testCase.getApiUrl(), jsonObject.toJSONString());
            } else {
                response = HttpUtils.getGetResponse(testCase.getApiUrl());
            }
        } catch (RuntimeException e) {
            if (Har.POST.toString().equals(testCase.getMethod())) {
                response = HttpUtils.getPostResponse(testCase.getApiUrl(), jsonObject.toJSONString());
            } else {
                response = HttpUtils.getGetResponse(testCase.getApiUrl());
            }
        }

        String resBody = "";
        try {
            resBody = response.body().string();
            if (resBody == null || resBody.length() == 0) {
                org.json.JSONObject json = new org.json.JSONObject();
                String message = response.header(ResAssertKey.MESSAGE.toString());
                String messageCode = response.header("messageCode");
                if (message != null) {
                    json.put(ResAssertKey.MESSAGE.toString(), message);
                }
                if (messageCode != null) {
                    json.put("messageCode", messageCode);
                }
                resBody = json.toString();
                log.debug("response headers: " + resBody);
            }
            JsonTestUtils.getCurrentTestCase().setResponseBody(resBody);
            log.debug("Response: " + resBody);
            log.debug("Response code: " + response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateTestCaseInfoAfter();

        responseCodeAssert(response);

        if (resBody.contains("{") && resBody.contains("}")) {
            resBody = resBody.substring(resBody.indexOf("{"), resBody.lastIndexOf("}") + 1);
        }

        String tempResBody = resBody;

        responseValueAssert(testCase.getResponseAsserts(), tempResBody);
    }

}
