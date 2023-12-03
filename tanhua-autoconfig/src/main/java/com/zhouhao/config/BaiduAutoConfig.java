package com.zhouhao.config;

import com.zhouhao.pojo.BaiduProperties;
import com.zhouhao.template.BaiduTemplate;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("com.zhouhao.pojo")
public class BaiduAutoConfig {
    @Bean
    public BaiduTemplate baiduTemplate(BaiduProperties baiduProperties){
        return new BaiduTemplate(baiduProperties);
    }
}
