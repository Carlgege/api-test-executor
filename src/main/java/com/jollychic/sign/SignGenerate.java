package com.jollychic.sign;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 签名算法类
 *
 * @author xusheng
 */
public final class SignGenerate {

    private static final String appSecret = "123456";

    /**
     * 签名生成算法
     *
     * @return sign
     * @param请求的所有参数必须已转换为字符串类型
     */
    public static String getSign(JSONObject json) {

        return appSecret;
    }
}
