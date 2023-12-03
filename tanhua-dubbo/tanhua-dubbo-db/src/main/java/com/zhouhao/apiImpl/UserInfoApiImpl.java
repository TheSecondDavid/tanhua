package com.zhouhao.apiImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.dao.UserInfoDao;
import com.zhouhao.entity.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {
    @Autowired
    UserInfoDao userInfoDao;

    @Override
    public void save(UserInfo userInfo) {
        userInfoDao.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoDao.updateById(userInfo);
    }

    @Override
    public UserInfo findById(String id) {
        return userInfoDao.selectById(id);
    }

    @Override
    public List<UserInfo> findByIds(List<Long> userIds) {
        List<UserInfo> res = new ArrayList<>();
        for(Long userId: userIds) {
            UserInfo byId = this.findById(String.valueOf(userId));
            res.add(byId);
        }
        return res;
    }

    @Override
    public IPage<UserInfo> findAll(Integer page, Integer pagesize) {
        return userInfoDao.selectPage(new Page<UserInfo>(page,pagesize),null);
    }
}
