package com.jollychic.utils;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DatabaseUtils {

    public static Connection connection = null;
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * 需要对不同的库操作，所以不使用单例模式
     *
     * @param dbURL
     * @param dbUser
     * @param dbPassword
     * @return
     */
    private static Connection getConnection(String dbURL, String dbUser, String dbPassword) {

        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static ResultSet executeQuery(String dbURL, String dbUser, String dbPassword, String sql) {
        Connection connection = getConnection(dbURL, dbUser, dbPassword);
        ResultSet resultSet = null;

        try {
            resultSet = connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public static String executeQueryResult(String dbURL, String dbUser, String dbPassword, String sql, String col) {
        ResultSet resultSet = executeQuery(dbURL, dbUser, dbPassword, sql);
        String result = "";
        int i = 0;
        do {
            ThreadUtils.sleep(1000);
            try {
                if (resultSet.next()) {
                    result = resultSet.getString(col);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            log.debug("executeQuery: " + sql);
            log.debug("Result: " + result);
            i++;
        } while (i < 3 && "".equals(result));

        return result;
    }

    public static boolean executeSql(String dbURL, String dbUser, String dbPassword, String sql) {
        Connection connection = getConnection(dbURL, dbUser, dbPassword);
        boolean b = false;

        try {
            b = connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.debug("executeSql: " + sql + " , success");

        return b;
    }

    public static void main(String[] args) {
        //beta
//        ResultSet resultSet = executeQuery("jdbc:mysql://169.44.62.140:3306/jolly_split_order_user?useUnicode=true&characterEncoding=UTF-8",
//                "test", "l8ka65##1", "SELECT * from who_sms_send_log WHERE mobile=\"971553528528\" ORDER BY log_id DESC LIMIT 1;");

        //weekly
        ResultSet resultSet = executeQuery("jdbc:mysql://172.31.0.68:3306/jolly_split_order_user?useUnicode=true&characterEncoding=UTF-8",
                "test_update", "l8ka65", "SELECT * from who_sms_send_log ORDER BY log_id DESC LIMIT 1;");

        try {
            if (resultSet.next()) {
                log.debug(resultSet.getString("task_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
