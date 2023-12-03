package com.zhouhao.controller;

import com.zhouhao.entity.UserInfo;
import com.zhouhao.service.UserInfoService;
import com.zhouhao.utils.JwtUtils;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.UserInfoVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequestMapping
@RestController
public class UserController {
    @Autowired
    UserInfoService userInfoService;

    @PostMapping("/user/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String token){
        Claims claims = JwtUtils.getClaims(token);
        String id = (String)claims.get("id");
        userInfo.setId(Long.valueOf(id));
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/users")
    public ResponseEntity updateUserInfo(@RequestBody UserInfo 	userInfo, @RequestHeader("Authorization") String token) {
        userInfo.setId(UserHolder.getUserId());
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }

    @PostMapping("user/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto, @RequestHeader("Authorization") String token) throws IOException {
        userInfoService.head(headPhoto, UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }

    @GetMapping("users")
    public ResponseEntity users(@RequestHeader("Authorization") String token){
        UserInfoVo userInfoVo = userInfoService.findById(String.valueOf(UserHolder.getUserId()));
        return ResponseEntity.ok(userInfoVo);
    }
}
