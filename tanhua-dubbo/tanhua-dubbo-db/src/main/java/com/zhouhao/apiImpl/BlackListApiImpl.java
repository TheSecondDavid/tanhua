package com.zhouhao.apiImpl;

import com.zhouhao.api.BlackListApi;
import com.zhouhao.dao.BlackListMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;
}
