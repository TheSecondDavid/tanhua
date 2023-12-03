package com.zhouhao.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhouhao.aop.LogConfig;
import com.zhouhao.api.CommentApi;
import com.zhouhao.api.MovementApi;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.api.VisitorsApi;
import com.zhouhao.constants.CommentType;
import com.zhouhao.constants.Constants;
import com.zhouhao.entity.Comment;
import com.zhouhao.entity.Movement;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.entity.Visitors;
import com.zhouhao.template.QiniuTemplate;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.MovementsVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.VisitorsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementsService {
    @Autowired
    private QiniuTemplate qiniuTemplate;
    @DubboReference(version = "2.0")
    private MovementApi movementApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference(version = "2.0")
    private CommentApi commentApi;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @DubboReference(version = "2.0", timeout = 10000)
    VisitorsApi visitorsApi;
    @Autowired
    UserInfoService userinfoService;
    //根据id查询
    @LogConfig(type = "0202",key = "movement",objId = "#movementId")
    public MovementsVo findById(String movementId) {
        //1、调用api根据id查询动态详情
        Movement movement = movementApi.findMovementById(movementId);
        //2、转化vo对象
        if(movement != null) {
            UserInfo userInfo = userInfoApi.findById(String.valueOf(movement.getUserId()));
            return MovementsVo.init(userInfo,movement);
        }else {
            return null;
        }
    }
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        Long userId = UserHolder.getUserId();
        ArrayList<String> list = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String upload = qiniuTemplate.upload(multipartFile);
            list.add(upload);
        }

        movement.setMedias(list);
        movement.setId(new ObjectId(String.valueOf(userId)));
        movementApi.publish(movement);
    }

    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        PageResult pageResult = movementApi.findByUserId(userId, page, pagesize);
        List<Movement> items = (List<Movement>) pageResult.getItems();

        List<MovementsVo> list = new ArrayList<>();
        for (Movement movement : items) {
            MovementsVo temp = MovementsVo.init(userInfoApi.findById(userId.toString()), movement);
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId();
            String hashKeyLike = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
            String hashKeyLove = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
            Object like = redisTemplate.opsForHash().get(key, hashKeyLike);
            Object love = redisTemplate.opsForHash().get(key, hashKeyLove);
            if (like != null || love != null) {
                if(like != null)
                    temp.setHasLiked(1);
                if(love != null)
                    temp.setHasLoved(1);
            } else {
                List<Comment> comments = commentApi.findCommentById(userId, movement.getId());
                for (Comment comment : comments) {
                    if (comment.getCommentType().equals(CommentType.LIKE.getType()))
                        temp.setHasLiked(1);
                    if (comment.getCommentType().equals(CommentType.LOVE.getType()))
                        temp.setHasLoved(1);
                }
            }
            list.add(temp);
        }
        pageResult.setItems(list);
        return pageResult;
    }

    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        Long userId = UserHolder.getUserId();
        PageResult pageResult = movementApi.findFriendsMovementsByUserId(userId, page, pagesize);

        List<Movement> items = (List<Movement>) pageResult.getItems();
        List<MovementsVo> list = new ArrayList<>();
        for (Movement movement : items) {
            MovementsVo temp = MovementsVo.init(userInfoApi.findById(String.valueOf(movement.getUserId())), movement);
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId();
            String hashKeyLike = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
            String hashKeyLove = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
            Object like = redisTemplate.opsForHash().get(key, hashKeyLike);
            Object love = redisTemplate.opsForHash().get(key, hashKeyLove);
            if (like != null || love != null) {
                if(like != null)
                    temp.setHasLiked(1);
                if(love != null)
                    temp.setHasLoved(1);
            } else {
                List<Comment> comments = commentApi.findCommentById(userId, movement.getId());
                for (Comment comment : comments) {
                    if (comment.getCommentType().equals(CommentType.LIKE.getType()))
                        temp.setHasLiked(1);
                    if (comment.getCommentType().equals(CommentType.LOVE.getType()))
                        temp.setHasLoved(1);
                }
            }

            list.add(temp);
        }
        pageResult.setItems(list);

        return pageResult;
    }

    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        Long userId = UserHolder.getUserId();
        String redisKey = Constants.MOVEMENTS_RECOMMEND + userId;
        String redisData = redisTemplate.opsForValue().get(redisKey);

        String[] split = redisData.split(",");
        PageResult pageResult = movementApi.findByPids(split, page, pagesize);
        List<Movement> items = (List<Movement>) pageResult.getItems();
        List<MovementsVo> list = new ArrayList<>();
        for (Movement movement : items) {
            MovementsVo temp = MovementsVo.init(userInfoApi.findById(String.valueOf(movement.getUserId())), movement);
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId();
            String hashKeyLike = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
            String hashKeyLove = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
            Object like = redisTemplate.opsForHash().get(key, hashKeyLike);
            Object love = redisTemplate.opsForHash().get(key, hashKeyLove);
            if (like != null || love != null) {
                if(like != null)
                    temp.setHasLiked(1);
                if(love != null)
                    temp.setHasLoved(1);
            } else {
                List<Comment> comments = commentApi.findCommentById(userId, movement.getId());
                for (Comment comment : comments) {
                    if (comment.getCommentType().equals(CommentType.LIKE.getType()))
                        temp.setHasLiked(1);
                    if (comment.getCommentType().equals(CommentType.LOVE.getType()))
                        temp.setHasLoved(1);
                }
            }

            list.add(temp);
        }
        pageResult.setItems(list);

        return pageResult;
    }

    public MovementsVo findMovementById(String movementId) {
        Movement movement = movementApi.findMovementById(movementId);
        if (movement != null)
            return MovementsVo.init(userInfoApi.findById(String.valueOf(movement.getUserId())), movement);
        else
            return null;
    }

    public List<VisitorsVo> queryVisitorsList() {
        List<Visitors> visitorsList = visitorsApi.queryMyVisitor(UserHolder.getUserId());

        if (CollUtil.isEmpty(visitorsList)) {
            return Collections.emptyList();
        }

        List<Object> userIds = CollUtil.getFieldValues(visitorsList, "visitorUserId");
        List<Long> collect = userIds.stream().map(userId -> (Long) userId).collect(Collectors.toList());
        List<UserInfo> userInfoList = this.userinfoService.userInfoApi.findByIds(collect);

        List<VisitorsVo> visitorsVoList = new ArrayList<>();

        for (Visitors visitor : visitorsList) {
            for (UserInfo userInfo : userInfoList) {
                if (ObjectUtil.equals(visitor.getVisitorUserId(), userInfo.getId())) {
                    VisitorsVo visitorsVo = BeanUtil.toBeanIgnoreError(userInfo, VisitorsVo.class);
                    visitorsVo.setGender(userInfo.getGender().toLowerCase());
                    visitorsVo.setFateValue(visitor.getScore().longValue());
                    visitorsVoList.add(visitorsVo);
                    break;
                }
            }
        }

        return visitorsVoList;
    }
}
