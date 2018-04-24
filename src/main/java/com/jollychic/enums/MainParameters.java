package com.jollychic.enums;

/**
 * @author chenlg
 */
public enum MainParameters {

    /** 指定本地Json文件路径 **/
    LOCAL_JSON_FILE_PATH("localJsonFilePath"),
    TASK_ID("taskId"),
    SERVER_ADDR("serverAddr"),
    NOTIFICATION_USERS("notificationUsers");

    private final String name;

    MainParameters(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }


}
