package com.magneton.service.support.pubsub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Redis pub/sub支持
 *
 * 注解订阅一个频道
 *
 * @author zhangmingshuang
 * @since 2018/8/13 13:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface Subscribe {

    String[] channels();

    String redisTemplate() default "";
}
