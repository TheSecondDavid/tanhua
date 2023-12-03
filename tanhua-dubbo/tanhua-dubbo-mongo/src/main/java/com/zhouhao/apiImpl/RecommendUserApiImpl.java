package com.zhouhao.apiImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.zhouhao.api.RecommendUserApi;
import com.zhouhao.entity.RecommendUser;
import com.zhouhao.entity.UserLike;
import com.zhouhao.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DubboService(version = "2.0.0")
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        Query query = new Query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long userId) {
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(pagesize).skip((page - 1) * pagesize);
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        int count = (int) mongoTemplate.count(query, RecommendUser.class);
        return new PageResult(page, pagesize, count, recommendUsers);
    }

    @Override
    public RecommendUser queryByUserId(Long userId, Long ownerUserId) {
        Criteria criteria = Criteria.where("userId").is(String.valueOf(userId)).and("toUserId").is(String.valueOf(ownerUserId));
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        /**
         * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
         * 1、排除喜欢，不喜欢的用户
         * 2、随机展示
         * 3、指定数量
         */
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score")));
        TypedAggregation<RecommendUser> recommendUserTypedAggregation = TypedAggregation.newAggregation(RecommendUser.class, Aggregation.match(criteria), Aggregation.sample(count));
        AggregationResults<RecommendUser> aggregate = mongoTemplate.aggregate(recommendUserTypedAggregation, RecommendUser.class);
        List<RecommendUser> recommendUsers = aggregate.getMappedResults();

        criteria = Criteria.where("userId").is(userId);
        List<UserLike> userLikes = mongoTemplate.find(Query.query(criteria), UserLike.class);
        Set<Long> likeIds = new HashSet<>(CollUtil.getFieldValues(userLikes, "likeUserId", Long.class));

        if (likeIds.size() != 0) {
            for (RecommendUser recommendUser : recommendUsers) {
                if (likeIds.contains(recommendUser.getUserId())){
                    recommendUsers.remove(recommendUser);
                }
            }
        }
        return recommendUsers;
    }
}
