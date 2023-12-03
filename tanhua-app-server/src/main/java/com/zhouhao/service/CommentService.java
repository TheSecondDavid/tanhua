package com.zhouhao.service;

import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.api.CommentApi;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.constants.CommentType;
import com.zhouhao.constants.Constants;
import com.zhouhao.entity.Comment;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.CommentVo;
import com.zhouhao.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @DubboReference(version = "2.0")
    CommentApi commentApi;
    @DubboReference
    UserInfoApi userInfoApi;
    @Autowired
    RedisTemplate redisTemplate;

    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        PageResult pageResult = commentApi.findComments(movementId, page, pagesize, CommentType.COMMENT);
        List<Comment> comments = (List<Comment>) pageResult.getItems();
//        List<CommentVo> list = Collections.emptyList();
        List<CommentVo> list = new ArrayList<>();
        for(Comment comment: comments){
            CommentVo temp = CommentVo.init(userInfoApi.findById(String.valueOf(comment.getUserId())), comment);
            list.add(temp);
        }
        pageResult.setItems(list);

        return pageResult;
    }

    public void saveComments(String movementId, String comment) {
        Long userId = UserHolder.getUserId();
        //2、构造Comment
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setUserId(userId);
        comment1.setCreated(System.currentTimeMillis());
        //3、调用API保存评论
        commentApi.saveComment(comment1);
    }

    public Integer likeComment(String movementId) {
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LIKE);
        if(hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }

        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.saveComment(comment);

        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().put(key, hashKey, 1);
        return count;
    }

    public Integer dislikeComment(String movementId) {
        //1、调用API查询用户是否已点赞
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LIKE);
        //2、如果未点赞，抛出异常
        if(!hasComment) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey);

        return count;
    }

    //喜欢
    public Integer loveComment(String movementId) {
        //1、调用API查询用户是否已点赞
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2、如果已经喜欢，抛出异常
        if(hasComment) {
            throw  new BusinessException(ErrorResult.loveError());
        }
        //3、调用API保存数据到Mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.saveComment(comment);
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }

    //取消喜欢
    public Integer disloveComment(String movementId) {
        //1、调用API查询用户是否已点赞
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2、如果未点赞，抛出异常
        if(!hasComment) {
            throw new BusinessException(ErrorResult.disloveError());
        }
        //3、调用API，删除数据，返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        //4、拼接redis的key，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;
    }
}
