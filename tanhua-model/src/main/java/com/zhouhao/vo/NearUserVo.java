package com.zhouhao.vo;

import com.zhouhao.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearUserVo {

    private Long userId;
    private String avatar;
    private String nickname;

    public static NearUserVo init(UserInfo userInfo) {
        NearUserVo vo = new NearUserVo();
        vo.setUserId(userInfo.getId());
        vo.setAvatar(userInfo.getAvatar());
        vo.setNickname(userInfo.getNickname());
        return vo;
    }
}