package com.zhouhao.controller;

import com.zhouhao.service.SettingsService;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.SettingsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class SettingsController {
    @Autowired
    SettingsService settingsService;

    @GetMapping("settings")
    public ResponseEntity settings(){
        SettingsVo settingsVo = settingsService.settings();
        return new ResponseEntity(settingsVo, HttpStatus.OK);
    }

    @PostMapping("questions")
    public ResponseEntity questions(@RequestBody Map map){
        String content = (String) map.get("content");
        settingsService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map) {
        //获取参数
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }
    @GetMapping("blacklist")
    public ResponseEntity blcaklist(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "10") int size){
        PageResult pageResult = settingsService.blcaklist(page, size);
        return ResponseEntity.ok(pageResult);
    }
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid") Long blackUserId){
        settingsService.deleteBlackList(blackUserId);
        return ResponseEntity.ok(null);
    }
}
