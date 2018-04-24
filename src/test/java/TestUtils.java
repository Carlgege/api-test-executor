import com.jollychic.APITestRun;
import com.jollychic.utils.HttpUtils;
import org.json.HTTP;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    @Test
    public void test1() {
//        HttpUtils.post("http://localhost:9980/autotestserver/saveresults/apiTestTaskName/postTestTask", "");
        assertThat("2").as("校验字符串").isEqualTo("3");
    }

    @Test
    public void test2() {
        HttpUtils.post("http://172.31.9.189:9980/autotestserver/task/taskId/2",
                "[{\"endTime\":1522639544769,\"errorInfo\":\"\",\"name\":\"/user/emailExists.do--function test1\",\"resBody\":\"{\\\"isExists\\\":0,\\\"message\\\":\\\"Success\\\",\\\"messageCode\\\":\\\"0\\\",\\\"messageType\\\":0,\\\"result\\\":0}\",\"result\":\"pass\",\"runTime\":3617,\"startTime\":1522639541152}]");

    }

    @Test
    public void test3() {
        StringBuilder failMsg = new StringBuilder();
        failMsg.append("# [APP2接口运行结果](http://172.31.11.171:8180/)");
        failMsg.append("## 33 445 5");
        System.out.println(failMsg.toString());
        System.out.println(failMsg.toString().replaceAll("## |# ", ""));
        System.out.println(failMsg.toString().replaceAll("#{1,} ", ""));
    }

    @Test
    public void test4() {
        String s = "http://app.jollychic.com/edtion/getEdtionJson.do?edtionId=89948&terminalType=1&lang=0&appVersion=6.15.3&appTypeId=0&countryCode=HK&isYHHEdtion=0&cookieId=877e4880-1c3f-4adc-9157-a8eb2c120c18&timestamp=0&_=1521536697662&callback=topicCallback1521536697660";
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(s);
        System.out.println(matcher.find());
        System.out.println(matcher.group());
    }

    @Test
    public void test5() {
        System.out.println(HttpUtils.post("http://localhost:9105/login", "{\"account\":\"clark\", \"password\":\"1\"}"));

    }

}
