package com.zhouhao.api;

import com.zhouhao.entity.RecommendUser;
import com.zhouhao.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserId);

    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long userId);

    RecommendUser queryByUserId(Long userId, Long userId1);

    /**
     * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     */
    List<RecommendUser> queryCardsList(Long userId, int count);
}