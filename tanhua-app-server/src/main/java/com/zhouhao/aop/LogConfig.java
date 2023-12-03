package com.zhouhao.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogConfig {

    //动态获取方法参数，支持SpringEL
    String objId() default "";

    //路由的key
    String key();

    //日志类型
    String type();
}