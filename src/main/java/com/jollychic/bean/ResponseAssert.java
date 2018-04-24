package com.jollychic.bean;

import lombok.Data;

@Data
public class ResponseAssert {

    private String assertCondition;
    private String jsonPath;
    private Object value;

}
