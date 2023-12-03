package com.zhouhao;

import com.zhouhao.template.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {
    @Autowired
    SmsTemplate smsTemplate;

    @Test
    public void test1(){
        smsTemplate.sendSms("18846178317", String.valueOf(getRandom()));
    }

    private int getRandom(){
        Random random = new Random();
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int c = random.nextInt(10);
        int d = random.nextInt(10);

        String r = String.valueOf(a) + String.valueOf(b) + String.valueOf(c) + String.valueOf(d);
        return Integer.parseInt(r);
    }
}
