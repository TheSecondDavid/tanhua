package com.zhouhao.service;

import cn.hutool.core.codec.Base64Encoder;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.template.BaiduTemplate;
import com.zhouhao.template.QiniuTemplate;
import com.zhouhao.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class UserInfoService{
    @Autowired
    private MessagesService messagesService;
    @DubboReference
    UserInfoApi userInfoApi;
    @Autowired
    QiniuTemplate qiniuTemplate;
    @Autowired
    BaiduTemplate baiduTemplate;

    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }

    public UserInfoVo findById(String id) {
        UserInfo userInfo = userInfoApi.findById(id);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        userInfoVo.setAge(String.valueOf(userInfo.getAge()));
        return userInfoVo;
    }

    public void head(MultipartFile headPhoto, Long id) throws IOException {
//        String upload = qiniuTemplate.upload(headPhoto);
//        System.out.println(upload);
//        String upload = "s3l5k05fh.hd-bkt.clouddn.com/093af6c4-ef9e-4fbb-84bd-927d58ed4b77.jpg";
//        byte[] bytes = headPhoto.getBytes();
        File file = new File("C:\\Users\\91192\\Desktop\\人脸\\f55897c1b5fdcee3222bd9b3f97975ac.jpg");
        byte[] bytesArray = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytesArray);

        boolean detect = baiduTemplate.detectBase64(Base64Encoder.encode(bytesArray));

        if (!detect) {
            throw new RuntimeException();
        } else {
            //2.2 包含人脸，调用API更新
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAvatar("s3l5k05fh.hd-bkt.clouddn.com/093af6c4-ef9e-4fbb-84bd-927d58ed4b77.jpg");
            userInfoApi.update(userInfo);
        }
    }
}
