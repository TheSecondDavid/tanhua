package com.zhouhao.api;

import com.zhouhao.entity.Movement;
import com.zhouhao.vo.PageResult;

public interface MovementApi {

    void publish(Movement movement);

    PageResult findByUserId(Long userId, Integer page, Integer pagesize);

    PageResult findFriendsMovementsByUserId(Long userId, Integer page, Integer pagesize);

    PageResult findByPids(String[] split, Integer page, Integer pagesize);

    Movement findMovementById(String movementId);

}