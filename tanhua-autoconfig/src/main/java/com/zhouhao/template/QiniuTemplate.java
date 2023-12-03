package com.zhouhao.template;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zhouhao.pojo.QiniuProperties;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class QiniuTemplate {
    QiniuProperties qiniuProperties;

    public QiniuTemplate(QiniuProperties qiniuProperties){
        this.qiniuProperties = qiniuProperties;
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        String accessKey = qiniuProperties.getAccessKey();
        String secretKey = qiniuProperties.getSecretKey();
        String bucket = "tanhua-zhouhao";
        byte[] bytes = multipartFile.getBytes();
        String key = UUID.randomUUID().toString() + ".jpg";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(bytes, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            ex.printStackTrace();
            if (ex.response != null) {
                System.err.println(ex.response);
                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        }

        return qiniuProperties.getUrl() + "/" + key;
    }
}