package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.admin.Admin;
import com.zhouhao.admin.AdminVo;
import com.zhouhao.constants.Constants;
import com.zhouhao.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //用户登录
    public ResponseEntity login(Map map) {
        //1、获取请求的参数（username,password,verificationCode（验证码）,uuid）
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");
        //2、判断用户名或者密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            //用户名或者密码为空
            //throw new BusinessException("用户名或者密码为空");
            Map map1 = new HashMap();
            map.put("message","用户名或者密码为空");
            return ResponseEntity.status(500).body(map1);
        }
        //3、判断验证码是否正确
        if (StringUtils.isEmpty(username)) {
            //验证码为空
            throw new BusinessException(new ErrorResult("400", "验证码为空"));
        }
        //从redis中获取验证码
        String key = Constants.CAP_CODE+uuid;
        String code = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(code) || !code.equals(verificationCode)) {
            //验证码错误
            throw new BusinessException(new ErrorResult("400", "验证码错误"));
        }
        redisTemplate.delete(key);
        //4、根据用户名查询用户
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        qw.eq("username", username);
        Admin admin = adminMapper.selectOne(qw);
        //5、判断用户是否存在，密码是否一致
        password = SecureUtil.md5(password); //md5加密
        if(admin == null || !admin.getPassword().equals(password)) {
            //用户名错误或者密码不一致
            throw new BusinessException(new ErrorResult("400", "用户名错误或者密码不一致"));
        }
        //6、通过JWT生成token
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("username", username);
        claims.put("id", admin.getId());
        String token = JwtUtils.getToken(claims);
        //8、构造返回值
        Map res = new HashMap();
        res.put("token", token);
        return ResponseEntity.ok(res);
    }

    //获取当前用户的用户资料
    public AdminVo profile() {
        Long id = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(id);
        return AdminVo.init(admin);
    }
}
