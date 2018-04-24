package com.jollychic.bean;


import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class APITestProject {

    private String projectName;
    private Map<String, Object> globalRequestParameters = new LinkedHashMap<>();
    private List<APITestCase> testCaseList = new ArrayList<>();

}
