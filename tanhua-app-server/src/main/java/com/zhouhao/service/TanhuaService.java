package com.zhouhao.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.api.*;
import com.zhouhao.constants.Constants;
import com.zhouhao.dto.RecommendUserDto;
import com.zhouhao.entity.Question;
import com.zhouhao.entity.RecommendUser;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.template.HuanXinTemplate;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.NearUserVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.TodayBest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TanhuaService {
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;
    @DubboReference(version = "2.0.0")
    RecommendUserApi recommendUserApi;
    @DubboReference
    UserInfoApi userInfoApi;
    @DubboReference
    QuestionApi questionApi;
    @Autowired
    HuanXinTemplate huanXinTemplate;
    @DubboReference(version = "2.0")
    UserLikeApi userLikeApi;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    MessagesService messagesService;
    @DubboReference(version = "2.0")
    UserLocationApi userLocationApi;

    //查询今日佳人数据
    public TodayBest todayBest() {
        //1、获取用户id
        Long userId = UserHolder.getUserId();
        //2、调用API查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if(recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1l);
            recommendUser.setScore(99d);
        }
        //3、将RecommendUser转化为TodayBest对象
        UserInfo userInfo = userInfoApi.findById(String.valueOf(recommendUser.getUserId()));
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        //4、返回
        return vo;
    }

    public PageResult recommendation(RecommendUserDto recommendUserDto) {
        Long userId = UserHolder.getUserId();
        PageResult page = recommendUserApi.queryRecommendUserList(recommendUserDto.getPage(), recommendUserDto.getPagesize(), userId);
        List<RecommendUser> items = (List<RecommendUser>) page.getItems();
        if(items.size() == 0)
            return page;

        List<Long> userIds = new ArrayList<>();
        for(RecommendUser recommendUser: items){
            Long userIdTemp = recommendUser.getUserId();
            userIds.add(userIdTemp);
        }

        List<UserInfo> userInfos = userInfoApi.findByIds(userIds);
        List<TodayBest> res = new ArrayList<>();
        for(UserInfo userInfo: userInfos){
            for(RecommendUser recommendUser: items)
                if(recommendUser.getUserId().equals(userInfo.getId())){
                    TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
                    res.add(todayBest);
                }
        }

        page.setItems(res);
        return page;
    }

    public TodayBest personalInfo(Long userId) {
        //1、根据用户id查询，用户详情
        UserInfo userInfo = userInfoApi.findById(String.valueOf(userId));
        //2、根据操作人id和查看的用户id，查询两者的推荐数据
        RecommendUser user = recommendUserApi.queryByUserId(userId,UserHolder.getUserId());
        //3、构造返回值
        if(user == null) {
            user = new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(UserHolder.getUserId());
            //构建缘分值
            user.setScore(95d);
        }
        return TodayBest.init(userInfo,user);
    }

    public String strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        return question == null ? "你喜欢java编程吗？" : question.getTxt();
    }

    public void replyQuestions(Long userId, String reply) {
        Long currentUserId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(String.valueOf(currentUserId));
        Map map = new HashMap();
        map.put("userId",currentUserId);
        map.put("huanXinId", Constants.HX_USER_PREFIX+currentUserId);
        map.put("nickname",userInfo.getNickname());
        map.put("strangerQuestion",strangerQuestions(userId));
        map.put("reply",reply);
        String message = JSON.toJSONString(map);
        Boolean aBoolean = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId,  message);
        if(!aBoolean) {
            throw  new BusinessException(ErrorResult.error());
        }
    }

    public List<TodayBest> queryCardsList() {
        List<RecommendUser> recommendUsers = recommendUserApi.queryCardsList(UserHolder.getUserId(), 5);
        List<Long> ids = CollUtil.getFieldValues(recommendUsers, "userId", Long.class);
        List<UserInfo> byIds = userInfoApi.findByIds(ids);
        Map<Long, UserInfo> map = byIds.stream().collect(Collectors.toMap(UserInfo::getId, byId -> byId));

        List<TodayBest> vos = new ArrayList<>();
        for (RecommendUser user : recommendUsers) {
            UserInfo userInfo = map.get(user.getUserId());
            if(userInfo != null) {
                TodayBest vo = TodayBest.init(userInfo, user);
                vos.add(vo);
            }
        }
        return vos;
    }


    //探花喜欢 106 -  2
    public void likeUser(Long likeUserId) {
        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(),likeUserId,true);
        if(!save) {
            //失败
            throw new BusinessException(ErrorResult.error());
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        //3、判断是否双向喜欢
        if(isLike(likeUserId,UserHolder.getUserId())) {
            //4、添加好友
            messagesService.contacts(likeUserId);
        }
    }

    public Boolean isLike(Long userId,Long likeUserId) {
        String key = Constants.USER_LIKE_KEY+userId;
        return redisTemplate.opsForSet().isMember(key,likeUserId.toString());
    }
    public void notLikeUser(Long likeUserId) {
        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(),likeUserId,false);
        if(!save) {
            //失败
            throw new BusinessException(ErrorResult.error());
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        //3、判断是否双向喜欢，删除好友(各位自行实现)
    }

    public List<NearUserVo> queryNearUser(String gender, String distance) {
        //1、调用API查询附近的用户（返回的是附近的人的所有用户id，包含当前用户的id）
        List<Long> userIds = userLocationApi.queryNearUser(UserHolder.getUserId(),Double.valueOf(distance));
        //2、判断集合是否为空
        if(CollUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        //3、调用UserInfoApi根据用户id查询用户详情
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        List<UserInfo> byIds = userInfoApi.findByIds(userIds);
        Map<Long, UserInfo> map = byIds.stream().collect(Collectors.toMap(UserInfo::getId, byId -> byId));
        //4、构造返回值。
        List<NearUserVo> vos = new ArrayList<>();
        for (Long userId : userIds) {
            //排除当前用户
            if(userId == UserHolder.getUserId()) {
                continue;
            }
            UserInfo info = map.get(userId);
            if(info != null) {
                NearUserVo vo = NearUserVo.init(info);
                vos.add(vo);
            }
        }
        return vos;
    }
}