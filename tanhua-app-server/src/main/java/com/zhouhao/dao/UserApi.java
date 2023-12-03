package com.zhouhao.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouhao.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserApi extends BaseMapper<User> {
}
