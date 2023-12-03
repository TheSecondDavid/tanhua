package com.zhouhao.controller;

import com.alibaba.fastjson.JSON;
import com.zhouhao.service.UserService;
import com.zhouhao.utils.UserHolder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 获取登录验证码
     *   请求参数：phone （Map）
     *   响应：void
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map){
        String phone =(String) map.get("phone");
        userService.sendMsg(phone);

        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", UserHolder.getUserId().toString());
        msg.put("date", System.currentTimeMillis());
                msg.put("type", "0101");
        String message = JSON.toJSONString(msg);
        //发送消息
        try {
            amqpTemplate.convertSendAndReceive("tanhua.log.exchange",
                    "log.user",message);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(null); //正常返回状态码200
    }
    /**
     * 检验登录
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        //1、调用map集合获取请求参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        //2、调用userService完成用户登录
        Map retMap = userService.loginVerification(phone,code);
        //3、构造返回
        return ResponseEntity.ok(retMap);
    }
}
