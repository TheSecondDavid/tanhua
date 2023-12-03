package com.zhouhao.service;

import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.api.UserLocationApi;
import com.zhouhao.utils.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference(version = "2.0")
    private UserLocationApi userLocationApi;

    //更新地理位置
    public void updateLocation(Double longitude, Double latitude, String address) {
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(),longitude,latitude,address);
        if(!flag) {
            throw  new BusinessException(ErrorResult.error());
        }
    }
}
