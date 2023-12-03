package com.zhouhao.config;

import com.zhouhao.pojo.QiniuProperties;
import com.zhouhao.template.QiniuTemplate;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("com.zhouhao.pojo")
public class QiniuAutoConfig {
    @Bean
    public QiniuTemplate qiniuTemplate(QiniuProperties qiniuProperties){
        return new QiniuTemplate(qiniuProperties);
    }
}
