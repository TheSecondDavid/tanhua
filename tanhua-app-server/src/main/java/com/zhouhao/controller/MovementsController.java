package com.zhouhao.controller;

import com.zhouhao.entity.Movement;
import com.zhouhao.service.CommentService;
import com.zhouhao.service.MovementsService;
import com.zhouhao.vo.MovementsVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.VisitorsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementsController {
    @Autowired
    private MovementsService movementsService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MovementsService movementService;

    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile imageContent[]) throws IOException {
        movementsService.publishMovement(movement, imageContent);
        return ResponseEntity.ok(null);
    }
    
    @GetMapping("all")
    public ResponseEntity findByUserId(Long userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize){
        PageResult pageResult = movementsService.findByUserId(userId, page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping
    public ResponseEntity movements(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findFriendMovements(page,pagesize);
        return ResponseEntity.ok(pr);
    }

    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findRecommendMovements(page,pagesize);
        return ResponseEntity.ok(pr);
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        MovementsVo vo = movementsService.findMovementById(movementId);
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.likeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.dislikeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 喜欢
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.loveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.disloveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }
    /**
     * 谁看过我
     */
    @GetMapping("visitors")
    public ResponseEntity queryVisitorsList(){
        List<VisitorsVo> list = movementService.queryVisitorsList();
        return ResponseEntity.ok(list);
    }
}
