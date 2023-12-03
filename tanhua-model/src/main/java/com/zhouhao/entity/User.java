package com.zhouhao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_user")
public class User extends BasePojo{
    String id;
    String mobile;
    String password;
    String hxUser;
    String hxPassword;
}
