package com.zhouhao.apiImpl;

import cn.hutool.core.collection.CollUtil;
import com.zhouhao.api.UserLocationApi;
import com.zhouhao.entity.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.List;

@DubboService(version = "2.0")
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //更新地理位置
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //1、根据用户id查询位置信息
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
            if(location == null) {
                //2、如果不存在用户位置信息，保存
                location = new UserLocation();
                location.setUserId(userId);
                location.setAddress(address);
                location.setCreated(System.currentTimeMillis());
                location.setUpdated(System.currentTimeMillis());
                location.setLastUpdated(System.currentTimeMillis());
                location.setLocation(new GeoJsonPoint(longitude,latitude));
                mongoTemplate.save(location);
            }else {
                //3、如果存在，更新
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", location.getUpdated());
                mongoTemplate.updateFirst(query,update,UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Long> queryNearUser(Long userId, Double metre) {
        UserLocation userLocation = mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserLocation.class);
        GeoJsonPoint location = userLocation.getLocation();
        Distance distance = new Distance(metre, Metrics.KILOMETERS);
        Circle circle = new Circle(location, distance);
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> userLocations = mongoTemplate.find(query, UserLocation.class);
        return CollUtil.getFieldValues(userLocations, "userId", Long.class);
    }
}