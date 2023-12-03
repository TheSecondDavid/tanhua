package com.zhouhao.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.huanxin")
public class HuanXinProperties {
    private String appKey;
    private String clientId;
    private String clientSecret;
}
