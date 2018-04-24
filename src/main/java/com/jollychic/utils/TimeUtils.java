package com.jollychic.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {

    public static final int LINUX_TIME_LENGTH = 11;

    private TimeUtils() {

    }

    /**
     * 获取当前时间
     */
    public static String getDateTime() {
        LocalDate data = LocalDate.now();
        LocalTime time = LocalTime.now();
        return data.toString() + "-" + time.toString().replaceAll("\\.|:", "-");
    }

    public static String timeStamp2Date(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp));
    }

    /**
     * 处理手机端拿到的时间格式：Mon Nov  6 16:46:09 CST 2017
     *
     * @param dateTime
     * @return
     */
    @Deprecated
    public static String requestTimeConvert(String dateTime) {
        String[] result = dateTime.split(" ");

        StringBuilder sb = new StringBuilder();
        sb.append(result[result.length - 1]);
        sb.append("-");
        sb.append(Month.valueOf(result[1]).ordinal() + 1);
        sb.append("-");
        sb.append(result[3]);
        sb.append("%20");
        sb.append(result[4].replaceAll(":", "-"));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(timeStamp2Date(System.currentTimeMillis()));
    }

}
