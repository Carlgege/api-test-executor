package com.jollychic.bean;

import lombok.Data;

/**
 * 依赖用例
 */
@Data
public class APITestDynamicParameters {

    private String referenceCaseName;
    /**
     * 依赖的用例的请求还是响应
     **/
    private String referenceBodyType;
    private String remoteJsonPath;
    private String localJsonPath;
}
