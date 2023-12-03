package com.zhouhao.controller;

import com.zhouhao.service.SmsService;
import com.zhouhao.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class SmsController {
    @Autowired
    SmsService smsService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<ErrorResult> login(@RequestBody String phone){
        try{
            smsService.sendCheckCode(phone);
        }catch (Exception e){
            ErrorResult errorResult = ErrorResult.builder().errCode("500").errMessage("服务器内部错误").build();
            ResponseEntity<ErrorResult> errorResultResponseEntity = new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
            return errorResultResponseEntity;
        }
        return ResponseEntity.ok(null);
    }
}
