package com.zhouhao.apiImpl;

import com.zhouhao.api.UserLikeApi;
import com.zhouhao.entity.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.List;

@DubboService(version = "2.0")
public class UserLikeApiImpl implements UserLikeApi {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        Criteria criteria = Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId);
        Query query = Query.query(criteria);
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);
        if(userLikes.size() == 0){
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setLikeUserId(likeUserId);
            userLike.setCreated(System.currentTimeMillis());
            userLike.setUpdated(System.currentTimeMillis());
            userLike.setIsLike(isLike);
            mongoTemplate.save(userLike);
            return true;
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        Update update = Update.update("isLike", true).set("created", System.currentTimeMillis());
        options.upsert(true);
        mongoTemplate.findAndModify(query, update, options, UserLike.class);

        return true;
    }
}
