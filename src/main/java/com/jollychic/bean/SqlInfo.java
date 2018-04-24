package com.jollychic.bean;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class SqlInfo {

    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private String sqlStatement;
    /**
     * 'delete','update','insert','select'
     **/
    private String sqlType;
    @Nullable
    private String localJsonPath;
    private String columnName;

}
