package com.jollychic.utils;

/**
 * 使用钉钉发送消息通知
 */
public class DingTalkUtils {

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=yourToken";

    public static void sendDingMessage(String message) {
        HttpUtils.post(WEBHOOK_TOKEN, message);
    }

    public static void main(String args[]) throws Exception {

//        HttpClient httpclient = HttpClients.createDefault();
//
//        HttpPost httppost = new HttpPost(WEBHOOK_TOKEN);
//        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
//

        int failTotal = 0;
        StringBuilder failMsg = new StringBuilder();
        failMsg.append("# [APP2接口线上运行结果](http://baidu.com)");
        failMsg.append("\n");
        for (int i = 0; i < 3; i++) {

            failTotal++;
            failMsg.append("## name: ");
            failMsg.append("tc" + i);
            failMsg.append("\n### error info: ");
            failMsg.append("\ndetail" + i);
            failMsg.append("\n");

        }
        failMsg.append("\n");


        String textMsg = "{\"msgtype\": \"markdown\", \"markdown\": {\"title\": \"Auto Test\", \"text\": \"" + failMsg.toString() + "\"}}";
//        StringEntity se = new StringEntity(textMsg, "utf-8");
//        httppost.setEntity(se);
//
//        HttpResponse response = httpclient.execute(httppost);
//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            String result = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println(result);
//        }

        sendDingMessage(textMsg);
    }
}