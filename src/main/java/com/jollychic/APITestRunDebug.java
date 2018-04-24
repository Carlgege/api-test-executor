package com.jollychic;

import com.jollychic.bean.APITestCase;
import com.jollychic.exec.JsonExecutor;
import com.jollychic.utils.JsonTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestNG;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.jollychic.utils.JsonTestUtils.analyseProject;

/**
 * 读取配置文件并执行最后一条测试用例
 */
@Slf4j
public class APITestRunDebug {

    private static String[] debugTC = new String[]{"sG natsu-test003"};

    public static void main(String[] args) {
        JsonTestUtils.isLocalDebug = true;

        File debugFile = new File(JsonTestUtils.DEBUG_RESULT_JSON);
        if (!debugFile.exists()) {
            try {
                debugFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String apiTestProjectStr = JsonTestUtils.loadCases("/App2APITestProject_release_debug.json");
        analyseProject(apiTestProjectStr);

        Iterator<Map.Entry<Integer, APITestCase>> it = JsonTestUtils.getAllTestCaseMap().entrySet().iterator();
        do {
            JsonTestUtils.getTestCase();

            String nextTCName = it.next().getValue().getName();
            System.out.println("nextTCName: " + nextTCName);
            for (String tcName : debugTC) {
                if (nextTCName.equals(tcName)) {
                    TestNG testNG = new TestNG();
                    testNG.setTestClasses(new Class[]{JsonExecutor.class});
                    testNG.run();
                }
            }
        } while (it.hasNext());
    }
}
