package com.jollychic.enums;

/**
 * Har 文件内容固定字符串
 */
public enum Har {

    LOG("log"),
    ENTRIES("entries"),
    REQUEST("request"),
    HEADERS("headers"),
    NAME("name"),
    VALUE("value"),
    CONTENT_TYPE("Content-Type"),
    JSON("json"),
    ENCODING("encoding"),
    HOST("Host"),
    METHOD("method"),
    POST("POST"),
    PARAMS("params"),
    GET("GET"),
    POSTDATA("postData"),
    TEXT("text"),
    URL("url"),
    RESPONSE("response"),
    CONTENT("content"),
    STATUS("status"),
    SERVERIPADDRESS("serverIPAddress");

    private final String name;

    Har(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
