package com.zhouhao.apiImpl;

import com.mongodb.client.result.UpdateResult;
import com.zhouhao.api.CommentApi;
import com.zhouhao.constants.CommentType;
import com.zhouhao.entity.Comment;
import com.zhouhao.entity.Movement;
import com.zhouhao.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.List;

@DubboService(version = "2.0")
public class CommentApiImpl implements CommentApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PageResult findComments(String movementId, Integer page, Integer pagesize, CommentType commentType) {
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType").is(commentType.getType()))
                .skip((page -1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //2、查询并返回
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        return new PageResult(page, pagesize, comments.size(), comments);
    }

    @Override
    public Integer saveComment(Comment comment) {
        //1、查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //2、向comment对象设置被评论人属性
        if(movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        mongoTemplate.save(comment);

        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if(comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount",1);
        }else if (comment.getCommentType() == CommentType.COMMENT.getType()){
            update.inc("commentCount",1);
        }else {
            update.inc("loveCount",1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        Movement movementres = mongoTemplate.findAndModify(query, update, options, Movement.class);
        return movementres.statisCount(comment.getCommentType());
    }

    @Override
    public Boolean hasComment(String movementId, Long userId, CommentType like) {
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(like.getType());
        Query query = Query.query(criteria);
        return mongoTemplate.exists(query,Comment.class); //判断数据是否存在
    }

    @Override
    public Integer delete(Comment comment) {
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query,Comment.class);

        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if(comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount",-1);
        }else if (comment.getCommentType() == CommentType.COMMENT.getType()){
            update.inc("commentCount",-1);
        }else {
            update.inc("loveCount",-1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true) ;//获取更新后的最新数据
        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);

        return modify.getLikeCount();
    }

    @Override
    public List<Comment> findCommentById(Long userId, ObjectId id) {
        Criteria criteria = Criteria.where("publishId").is(id).and("userId").is(userId);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Comment.class);
    }
}
