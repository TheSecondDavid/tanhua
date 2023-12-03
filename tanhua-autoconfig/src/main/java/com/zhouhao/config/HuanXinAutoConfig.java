package com.zhouhao.config;

import com.zhouhao.pojo.HuanXinProperties;
import com.zhouhao.template.HuanXinTemplate;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("com.zhouhao.pojo")
public class HuanXinAutoConfig {
    @Bean
    public HuanXinTemplate EMService(HuanXinProperties properties){
        return new HuanXinTemplate(properties);
    }
}