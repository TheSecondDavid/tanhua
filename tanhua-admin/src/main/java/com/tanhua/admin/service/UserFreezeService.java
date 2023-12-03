package com.tanhua.admin.service;

import com.alibaba.fastjson.JSON;
import com.zhouhao.BusinessException;
import com.zhouhao.ErrorResult;
import com.zhouhao.constants.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void checkUserStatus(Integer state,Long userId) {
        String value = redisTemplate.opsForValue().get(Constants.FREEZE_USER + userId);
        if(!StringUtils.isEmpty(value)) {
            Map map = JSON.parseObject(value, Map.class);
            Integer freezingRange = (Integer) map.get("freezingRange");
            if(freezingRange == state) {
                throw new BusinessException(ErrorResult.builder().errMessage("您的账号被冻结！").build());
            }
        }
    }
}
