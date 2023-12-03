package com.zhouhao.api;

import com.zhouhao.entity.Video;
import com.zhouhao.vo.PageResult;

import java.util.List;

public interface VideoApi {

    void save(Video video);

    PageResult findAll(Integer page, Integer pagesize);

    List<Video> findByUserId(Integer page, Integer pagesize, Long userId);
}
