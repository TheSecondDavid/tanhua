package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManagerService;
import com.zhouhao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management/manage")
public class ManageController {
    @Autowired
    ManagerService managerService;

    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        ResponseEntity result = managerService.findAllUsers(page,pagesize);
        return result;
    }

    /**
     * 根据id查询用户详情
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findById(@PathVariable("userId") Long userId) {
        return managerService.findById(userId);
    }

    /**
     * 查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid ) {
        PageResult result = managerService.findAllVideos(page,pagesize,uid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid,Integer state ) {
        ResponseEntity result = managerService.findAllMovements(page,pagesize,uid,state);
        return result;
    }

    //用户冻结
    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map params) {
        Map map =  managerService.userFreeze(params);
        return ResponseEntity.ok(map);
    }

    //用户解冻
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody  Map params) {
        Map map =  managerService.userUnfreeze(params);
        return ResponseEntity.ok(map);
    }
}
