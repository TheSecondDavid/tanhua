package com.zhouhao.config;

import com.zhouhao.pojo.SmsProperties;
import com.zhouhao.template.SmsTemplate;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("com.zhouhao.pojo")
public class SmsAutoConfig {
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        return new SmsTemplate(smsProperties);
    }
}