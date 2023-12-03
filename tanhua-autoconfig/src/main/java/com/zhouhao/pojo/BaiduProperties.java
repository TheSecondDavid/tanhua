package com.zhouhao.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.baidu")
public class BaiduProperties {
    String AppID;
    String API_Key;
    String Secret_Key;
}
