package com.zhouhao.utils;

import com.zhouhao.entity.User;

public class UserHolder {
    private static ThreadLocal<User> tl = new ThreadLocal<>();

    //将用户对象，存入Threadlocal
    public static void set(User user) {
        tl.set(user);
    }

    //从当前线程，获取用户对象
    public static User get() {
        return tl.get();
    }

    //从当前线程，获取用户对象的id
    public static Long getUserId() {
        return Long.valueOf(tl.get().getId());
    }

    //从当前线程，获取用户对象的手机号码
    public static String getMobile() {
        return tl.get().getMobile();
    }
}
