package com.jollychic.bean;

import lombok.Data;

@Data
public class APITestResult {

    private String name;
    private String errorInfo = "";
    private String result;
    private String resBody = "";
    private long startTime;
    private long endTime;
    private long runTime;

}
