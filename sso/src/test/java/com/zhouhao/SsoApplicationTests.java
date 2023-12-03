package com.zhouhao;

import com.zhouhao.pojo.SMSConfig;
import com.zhouhao.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SsoApplicationTests {
    @Autowired
    SMSConfig aliyunSMSConfig;
    @Autowired
    SmsService smsService;

    @Test
    public void test1() {
        System.out.println(aliyunSMSConfig);
    }

    @Test
    public void test2(){
        smsService.sendCheckCode("18846178317");
    }
}
