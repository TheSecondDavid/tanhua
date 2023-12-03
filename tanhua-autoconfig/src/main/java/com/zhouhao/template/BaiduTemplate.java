package com.zhouhao.template;

import com.baidu.aip.face.AipFace;
import com.zhouhao.pojo.BaiduProperties;
import org.json.JSONObject;

import java.util.HashMap;

public class BaiduTemplate {
    BaiduProperties baiduProperties;

    public BaiduTemplate(BaiduProperties baiduProperties) {
        this.baiduProperties = baiduProperties;
    }

    public boolean detect(String url) {
        AipFace client = new AipFace(baiduProperties.getAppID(), baiduProperties.getAPI_Key(), baiduProperties.getSecret_Key());
        String imageType = "URL";

        JSONObject res = client.detect(url, imageType, new HashMap<>());
        System.out.println(res);
        if (res.get("result") == null || res.get("result").toString().equals("null") || res.get("result").toString().equals(""))
            return false;
        else
            return (int) res.getJSONObject("result").get("face_num") > 0;
    }

    public boolean detectBase64(String imageBase64) {
        AipFace client = new AipFace(baiduProperties.getAppID(), baiduProperties.getAPI_Key(), baiduProperties.getSecret_Key());
        String imageType = "BASE64";

        JSONObject res = client.detect(imageBase64, imageType, new HashMap<>());
        System.out.println(res);
        if (res.get("result") == null || res.get("result").toString().equals("null") || res.get("result").toString().equals(""))
            return false;
        else
            return (int) res.getJSONObject("result").get("face_num") > 0;
    }
}
