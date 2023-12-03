package com.zhouhao.controller;

import com.zhouhao.service.CommentService;
import com.zhouhao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("comments")
@RestController
public class CommentController {
    @Autowired
    CommentService commentsService;

    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String movementId) {
        PageResult pr = commentsService.findComments(movementId,page,pagesize);
        return ResponseEntity.ok(pr);
    }
    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map map) {
        String movementId = (String )map.get("movementId");
        String comment = (String)map.get("comment");
        commentsService.saveComments(movementId,comment);
        return ResponseEntity.ok(null);
    }
}
