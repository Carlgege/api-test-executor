package com.jollychic.bean;


import lombok.Data;

import javax.annotation.Nullable;
import java.util.*;

@Data
public class APITestCase {

    //用例名
    private String name;
    @Nullable
    private String description;
    //接口地址不带host
    private String apiUrl;
    //请求类型，get, post
    private String method;
    @Nullable
    private String requestContentType;
    @Nullable
    private String responseContentType;
    @Nullable
    private Map<String, Object> requestParameters = new HashMap<>();
    @Nullable
    private List<APITestDynamicParameters> updateRequestParameters = new LinkedList<>();
    /**
     * is, messageType : 0
     **/
    @Nullable
    private String responseBody;
    private List<ResponseAssert> responseAsserts = new ArrayList<>();
    @Nullable
    private List<SqlInfo> beforeSql = new LinkedList<>();
    @Nullable
    private List<SqlInfo> afterSql = new LinkedList<>();
    @Nullable
    private String caseComment = "";
    @Nullable
    private int responseCode = 200;


}
