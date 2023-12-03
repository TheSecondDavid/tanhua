package com.zhouhao.api;

import com.zhouhao.constants.CommentType;
import com.zhouhao.entity.Comment;
import com.zhouhao.vo.PageResult;
import org.bson.types.ObjectId;

import java.util.List;

public interface CommentApi {
    PageResult findComments(String movementId, Integer page, Integer pagesize, CommentType comment);

    Integer saveComment(Comment comment);

    Boolean hasComment(String movementId, Long userId, CommentType like);

    Integer delete(Comment comment);

    List<Comment> findCommentById(Long userId, ObjectId id);
}
