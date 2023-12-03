package com.zhouhao.controller;

import com.zhouhao.service.SmallVideosService;
import com.zhouhao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideoController {

    @Autowired
    private SmallVideosService videosService;

    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videosService.saveVideos(videoThumbnail,videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频列表
     */
    @GetMapping
    public ResponseEntity queryVideoList(@RequestParam(defaultValue = "1")  Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = videosService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(result);
    }
}