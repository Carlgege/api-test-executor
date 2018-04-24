package com.jollychic.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;


import java.io.IOException;
import java.net.ConnectException;


import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author chenlg
 */
@Slf4j
public class HttpUtils {
    private static final MediaType JSON = MediaType.parse("application/json; " + "charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    static {
        final int timeout = 120;
        client.newBuilder()
                .connectTimeout(timeout, SECONDS)
                .readTimeout(timeout, SECONDS)
                .writeTimeout(timeout, SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static Response getGetResponse(final String path) {
        log.debug("GET, path: " + path);
        Request request = new Request.Builder().url(path).addHeader("Connection", "close").build();
        return getExecuteResponse(request);
    }

    public static String get(final String path) {
        log.debug("GET, path: " + path);
        Request request = new Request.Builder().url(path).addHeader("Connection", "close").build();
        return execute(request);
    }

    public static Response getPostResponse(final String path, String body) {
        Request request = new Request.Builder().url(path).post(RequestBody.create(JSON, body)).addHeader("Connection", "close").build();
        log.debug("POST, path: " + path + ", body: " + body);
        return getExecuteResponse(request);
    }

    public static String post(final String path, String body) {
        Request request = new Request.Builder().url(path).post(RequestBody.create(JSON, body)).addHeader("Connection", "close").build();
        log.debug("POST, path: " + path + ", body: " + body);
        return execute(request);
    }

    private static Response getExecuteResponse(Request request) {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (ConnectException e) {
            log.debug(e.getMessage());
        } catch (IOException e) {
            log.debug("exception:" + e.getMessage());
            throw new RuntimeException(request.method() + " \"" + request.url() + "\" failed. ", e);
        }

        return response;
    }


    private static String execute(Request request) {
        String result = "";
        Response response;
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
        } catch (ConnectException e) {
            // check the command: adb forward --list
            // check the uiautomator service is running
//            log.debug("connectException: " + e.getLocalizedMessage());
//
//            ui2ServerCheck("yes");
//
//            try {
//                response = client.newCall(request).execute();
//                result = response.body().string();
//            } catch (IOException ex) {
//                log.debug("connectException happened again: " + ex.getLocalizedMessage());
//            }
        } catch (IOException e) {
            log.debug("exception:" + e.getMessage());
            throw new RuntimeException(request.method() + " \"" + request.url() + "\" failed. ", e);
        }
        if (!request.url().toString().endsWith("screenshot") && !request.url().toString().endsWith("source")) {
            log.debug("execute result:" + result);
        }

        return result;
    }


    public static void main(String[] args) {

    }

}
