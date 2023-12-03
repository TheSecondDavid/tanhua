package com.zhouhao;

import org.junit.Test;
import java.io.*;
import java.util.Properties;

public class DemoTest {
    @Test
    public void Test1() throws IOException {
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream("src\\main\\resources\\application.properties");
        InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
        properties.load(reader);
        reader.close();
        System.out.println(properties);

        System.out.println(properties.get("aliyun.sms.signName"));
        properties.setProperty("address", "Beijing, China");

        FileOutputStream fos = new FileOutputStream("src\\main\\resources\\application-dev.properties");
        OutputStreamWriter os = new OutputStreamWriter(fos, "utf-8");
        properties.store(os, "changeProperties");
        os.close();
    }
}
