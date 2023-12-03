package com.zhouhao.service;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.zhouhao.pojo.SMSConfig;
import com.zhouhao.template.SmsTemplate;
import com.zhouhao.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Random;

@Service
@SuppressWarnings("all")
public class SmsService {
    @Autowired
    SMSConfig smsConfig;
    @Autowired
    SmsTemplate smsTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                // 访问的域名
                .setEndpoint("dysmsapi.aliyuncs.com");
        // 访问的域名
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public SendSmsResponseBody sendSms(String mobile) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = SmsService
                .createClient(smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile) //目标手机号
                .setSignName(smsConfig.getSignName()) //签名名称
                .setTemplateCode(smsConfig.getTemplateCode()) //短信模板code
                .setTemplateParam("{\"code\":\""+ getRandom() +"\"}"); //模板中变量替换
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

        SendSmsResponseBody body = sendSmsResponse.getBody();

        return body;
    }

    public ErrorResult sendCheckCode(String phone) {
        String redisKey = "CHECK_CODE_" + phone;
        String random = String.valueOf(getRandom());

        //先判断该手机号发送的验证码是否还未失效
        if(this.redisTemplate.hasKey(redisKey)){
            String msg = "上一次发送的验证码还未失效！";
            return ErrorResult.builder().errCode("000001").errMessage(msg).build();
        }

        String code = smsTemplate.sendSms(phone, random);
        if(code.equals(500)) {
            String msg = "发送短信验证码失败！";
            return ErrorResult.builder().errCode("000000").errMessage(msg).build();
        }

        //短信发送成功，将验证码保存到redis中，有效期为5分钟
        this.redisTemplate.opsForValue().set(redisKey, random, Duration.ofMinutes(5));
        return null;
    }

    private int getRandom(){
        Random random = new Random();
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int c = random.nextInt(10);
        int d = random.nextInt(10);
        int e = random.nextInt(10);
        int f = random.nextInt(10);

        String r = String.valueOf(a) + String.valueOf(b) + String.valueOf(c) + String.valueOf(d) + String.valueOf(e) + String.valueOf(f);
        return Integer.parseInt(r);
    }
}
