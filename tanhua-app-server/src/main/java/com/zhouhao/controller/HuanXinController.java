package com.zhouhao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhouhao.dao.UserApi;
import com.zhouhao.entity.User;
import com.zhouhao.template.HuanXinTemplate;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.HuanXinUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("huanxin")
public class HuanXinController {
    @Autowired
    HuanXinTemplate huanXinTemplate;
    @Autowired
    UserApi userApi;

    @GetMapping("user")
    public ResponseEntity<HuanXinUserVo> user(){
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, String.valueOf(UserHolder.getUserId()));
        User user = userApi.selectOne(lambdaQueryWrapper);
        HuanXinUserVo huanXinUserVo = new HuanXinUserVo(user.getHxUser(), user.getPassword());
        return ResponseEntity.ok(huanXinUserVo);
    }
}
