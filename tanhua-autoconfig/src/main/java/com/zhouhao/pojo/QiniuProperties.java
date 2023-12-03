package com.zhouhao.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.qiniu")
public class QiniuProperties {
    String accessKey;
    String secretKey;
    String url;
}
