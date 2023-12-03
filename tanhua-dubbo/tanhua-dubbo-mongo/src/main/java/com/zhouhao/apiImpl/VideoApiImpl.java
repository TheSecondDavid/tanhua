package com.zhouhao.apiImpl;

import com.zhouhao.api.VideoApi;
import com.zhouhao.entity.Video;
import com.zhouhao.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

@DubboService(version = "2.0")
public class VideoApiImpl implements VideoApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idService;

    //保存
    public void save(Video video) {
        video.setId(ObjectId.get());
        video.setCreated(System.currentTimeMillis());
        video.setVid(idService.getNextId("video"));
        mongoTemplate.save(video);
    }

    @Override
    public PageResult findAll(Integer page, Integer pagesize) {
        long count = mongoTemplate.count(new Query(), Video.class);
        //2、分页查询数据列表
        Query query = new Query().limit(pagesize).skip((page-1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> list = mongoTemplate.find(query, Video.class);
        //3、构建返回
        return new PageResult(page,pagesize,(int) count,list);
    }

    @Override
    public List<Video> findByUserId(Integer page, Integer pagesize, Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria).limit(pagesize).skip((page-1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> list = mongoTemplate.find(query, Video.class);
        return list;
    }
}
