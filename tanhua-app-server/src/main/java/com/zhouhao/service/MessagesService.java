package com.zhouhao.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.api.FriendApi;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.constants.Constants;
import com.zhouhao.dao.UserApi;
import com.zhouhao.entity.Friend;
import com.zhouhao.entity.User;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.template.HuanXinTemplate;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.ContactVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessagesService {
    @Autowired
    UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference(version = "2.0")
    private FriendApi friendApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;

    /**
     * 根据环信id查询用户详情
     */
    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        //1、根据环信id查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getHxUser, huanxinId);
        User user = userApi.selectOne(queryWrapper);

        //2、根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId());
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo); //copy同名同类型的属性
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    public void contacts(Long friendId) {
        Boolean aBoolean = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + UserHolder.getUserId(),
                Constants.HX_USER_PREFIX + friendId);
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
        //2、如果注册成功，记录好友关系到mongodb
        friendApi.save(UserHolder.getUserId(), friendId);
    }

    public PageResult findFriends(Integer page, Integer pagesize, String keyword) {
        //1、调用API查询当前用户的好友数据 -- List<Friend>
        List<Friend> list = friendApi.findByUserId(UserHolder.getUserId(), page, pagesize);
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //2、提取数据列表中的好友id
        List<Long> userIds = CollUtil.getFieldValues(list, "friendId", Long.class);
        //3、调用UserInfoAPI查询好友的用户详情
        UserInfo info = new UserInfo();
        info.setNickname(keyword);
        List<UserInfo> userInfoList = userInfoApi.findByIds(userIds);
        //4、构造VO对象
        List<ContactVo> vos = new ArrayList<>();
        for (Friend friend : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userInfo.getId().equals(friend.getFriendId())) {
                    ContactVo vo = ContactVo.init(userInfo);
                    vos.add(vo);
                }
            }
        }
        return new PageResult(page, pagesize, vos.size(), vos);
    }
}
