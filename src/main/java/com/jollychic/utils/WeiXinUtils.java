package com.jollychic.utils;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.jollychic.bean.WeiXinUser;
import org.json.HTTP;
import org.json.JSONObject;

import java.util.List;

public class WeiXinUtils {

    private static String corpId = "yourCorpId";
    private static String secret = "yourSecret";
    private static String sendMessageUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
    private static String userListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=";
    private static String getTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpId+"&corpsecret="+secret;

    private static String getToken() {
        String response = HttpUtils.get(getTokenUrl);
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.get("access_token").toString();
    }

    public static void sendMessage(String user, String messageBody) {
        String message = "{\"touser\": \"" + user + "\", \"toparty\": \"\", \"totag\": \"\", \"msgtype\": \"text\", \"agentid\": 1000005, \"text\": {\"content\": \"" + messageBody + "\"}, \"safe\":\"0\"}";
        HttpUtils.post(sendMessageUrl + getToken(), message);
    }

    public static String getDepartment(int departmentId) {
        StringBuilder url = new StringBuilder();
        url.append(userListUrl);
        url.append(getToken());
        url.append("&department_id=");
        url.append(departmentId);
        url.append("&fetch_child=1");

        String userList = HttpUtils.get(url.toString());
        userList = JsonPath.parse(userList).read("$.userlist").toString();

        return userList;
    }

    public static void main(String[] args) {

    }


}
