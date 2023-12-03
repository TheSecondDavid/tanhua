package com.zhouhao.controller;

import com.zhouhao.dto.RecommendUserDto;
import com.zhouhao.service.TanhuaService;
import com.zhouhao.vo.NearUserVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    //今日佳人
    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        TodayBest vo = tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }

    @GetMapping("recommendation")
    private ResponseEntity recommendation(RecommendUserDto recommendUserDto){
        PageResult result = tanhuaService.recommendation(recommendUserDto);
        return new ResponseEntity(result,   HttpStatus.OK);
    }

    /**
     * 查看佳人详情
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId) {
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }

    /**
     * 查看陌生人问题
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        String questions = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 回复陌生人问题
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {
        //前端传递的userId:是Integer类型的
        String obj = map.get("userId").toString();
        Long userId = Long.valueOf(obj);
        String reply = map.get("reply").toString();
        tanhuaService.replyQuestions(userId,reply);
        return ResponseEntity.ok(null);
    }

    /**
     * 探花-推荐用户列表
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBest> list = this.tanhuaService.queryCardsList();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        this.tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> notLikeUser(@PathVariable("id") Long likeUserId) {
        this.tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }
    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender,
                                                          @RequestParam(defaultValue = "2000") String distance) {
        List<NearUserVo> list = this.tanhuaService.queryNearUser(gender, distance);
        return ResponseEntity.ok(list);
    }
}
