package com.zhouhao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Date;

@Configuration
@Slf4j
public class MybatisPlusConfig {
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MybatisPlusAutoFillConfig();
    }
    public static class MybatisPlusAutoFillConfig implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            setFieldValByName("created", new Date(), metaObject);
            setFieldValByName("updated", new Date(), metaObject);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            setFieldValByName("updated", new Date(), metaObject);
        }
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
