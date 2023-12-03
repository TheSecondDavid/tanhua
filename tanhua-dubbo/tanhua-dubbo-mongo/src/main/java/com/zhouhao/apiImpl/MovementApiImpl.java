package com.zhouhao.apiImpl;

import com.zhouhao.api.MovementApi;
import com.zhouhao.entity.Friend;
import com.zhouhao.entity.Movement;
import com.zhouhao.entity.MovementTimeLine;
import com.zhouhao.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DubboService(version = "2.0")
public class MovementApiImpl implements MovementApi {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    IdWorker idWorker;
    @Autowired
    private TimeLineService timeLineService;

    @Override
    public void publish(Movement movement) {
        movement.setPid(idWorker.getNextId("movement"));
        movement.setCreated(System.currentTimeMillis());
        mongoTemplate.save(movement);
        timeLineService.saveTimeLine(movement.getUserId(),movement.getId());
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, movements.size(), movements);
    }

    @Override
    public PageResult findFriendsMovementsByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        List<Long> list = new ArrayList<>();
        for(Friend friend: friends){
            list.add(friend.getFriendId());
        }

        criteria = Criteria.where("userId").in(list);
        query = Query.query(criteria).with(Sort.by(Sort.Order.desc("created"))).skip((page - 1) * pagesize).limit(pagesize);
//        List<MovementTimeLine> movementTimeLines = mongoTemplate.find(query, MovementTimeLine.class);
//        List<ObjectId> list1 = new ArrayList<>();
//        for(MovementTimeLine movementTimeLine: movementTimeLines){
//            list1.add(movementTimeLine.getMovementId());
//        }
//
//        criteria = Criteria.where("id").in(list1);
//        query = Query.query(criteria);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);

        return new PageResult(page, pagesize, movements.size(), movements);
    }

    @Override
    public PageResult findByPids(String[] split, Integer page, Integer pagesize) {
        List<String> list = Arrays.asList(split);
        List<Long> collect = list.stream().map(e -> Long.valueOf(e)).collect(Collectors.toList());
        Criteria criteria = Criteria.where("pid").in(collect);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("created"))).skip((page - 1) * pagesize).limit(pagesize);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, movements.size(), movements);
    }

    @Override
    public Movement findMovementById(String movementId) {
        Criteria criteria = Criteria.where("id").is(movementId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Movement.class);
    }
}
