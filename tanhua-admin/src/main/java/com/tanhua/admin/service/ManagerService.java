package com.tanhua.admin.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhouhao.api.MovementApi;
import com.zhouhao.api.UserInfoApi;
import com.zhouhao.api.VideoApi;
import com.zhouhao.constants.Constants;
import com.zhouhao.entity.Movement;
import com.zhouhao.entity.UserInfo;
import com.zhouhao.entity.Video;
import com.zhouhao.vo.MovementsVo;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.VideoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerService {
    @DubboReference
    UserInfoApi userInfoApi;
    @DubboReference(version = "2.0")
    MovementApi movementApi;
    @DubboReference(version = "2.0")
    VideoApi videoApi;
    @Autowired
    RedisTemplate redisTemplate;

    public ResponseEntity findAllUsers(Integer page, Integer pagesize) {
        //1、调用API分页查询数据列表   Ipage<UserInfo>
        IPage<UserInfo> iPage;
        iPage = userInfoApi.findAll(page,pagesize);
        //2、需要将Ipage转化为PageResult
        PageResult result = new PageResult(page, pagesize, iPage.getRecords().size(), iPage.getRecords());
        //3、构造返回值
        return ResponseEntity.ok(result);
    }

    //根据id查询用户详情
    public ResponseEntity findById(Long userId) {
        UserInfo info = userInfoApi.findById(String.valueOf(userId));
        if(redisTemplate.hasKey(Constants.FREEZE_USER+info.getId())) {
            info.setUserStatus("2");
        }
        return ResponseEntity.ok(info);
    }
    //根据用户id分页查询此用户发布的所有视频列表
    public PageResult findAllVideos(Integer page, Integer pagesize, Long userId) {
        //1、调用API查询视频列表（PageResult<video>）
        List<Video> items = videoApi.findByUserId(page,pagesize,userId);
        //2、获取到分页对象中的List List<Video>
        UserInfo info = userInfoApi.findById(String.valueOf(userId));
        //3、一个Video转化成一个VideoVo
        List<VideoVo> list = new ArrayList<>();
        for (Video item : items) {
            VideoVo vo = VideoVo.init(info, item);
            list.add(vo);
        }
        //4、构造返回
        return ResponseEntity.ok(new PageResult(page,pagesize,list.size(),list)).getBody();
    }

    //查询指定用户发布的所有动态
    public ResponseEntity findAllMovements(Integer page, Integer pagesize, Long userId, Integer state) {
        //1、调用API查询 ：（PageResult<Publish>）
        PageResult result = movementApi.findByUserId(userId,page,pagesize);
        //2、一个Publsh构造一个Movements
        List<Movement> items = ( List<Movement>)result.getItems();
        List<MovementsVo> list = new ArrayList<>();
        for (Movement item : items) {
            UserInfo userInfo = userInfoApi.findById(String.valueOf(item.getUserId()));
            MovementsVo vo = MovementsVo.init(userInfo, item);
            list.add(vo);
        }
        //3、构造返回值
        result.setItems(list);
        return ResponseEntity.ok(result);
    }

    //用户冻结
    public Map userFreeze(Map params) {
        Integer freezingTime = (Integer) params.get("freezingTime");
        Long userId = (Long) params.get("userId");
        int days = 0;
        if (freezingTime == 1) {
            days = 3;
        }
        if (freezingTime == 2) {
            days = 7;
        }
        if (freezingTime == 3) {
            days = -1;
        }
        String value = JSON.toJSONString(params);
        redisTemplate.opsForValue().set(Constants.FREEZE_USER+userId,value,days, TimeUnit.MINUTES);
        Map map = new HashMap();
        map.put("message","冻结成功");
        return map;
    }

    //用户解冻
    public Map userUnfreeze(Map params) {
        Long userId = (Long) params.get("userId");
        String reasonsForThawing = (String) params.get("reasonsForThawing");
        redisTemplate.delete(Constants.FREEZE_USER+userId);
        Map map = new HashMap();
        map.put("message","解冻成功");
        return map;
    }
}
