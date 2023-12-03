package com.zhouhao.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhouhao.entity.UserInfo;

import java.util.List;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(String id);

    List<UserInfo> findByIds(List<Long> userIds);

    IPage<UserInfo> findAll(Integer page, Integer pagesize);
}
