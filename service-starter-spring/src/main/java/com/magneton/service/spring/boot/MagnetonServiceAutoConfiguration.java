package com.magneton.service.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;


/**
 * Spring环境启动器
 * 用来扫描基于 com.magneton.service.core.spring 包下的所有依赖类
 *
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore
@ComponentScan(basePackages = "com.magneton.service.core.spring")
public class MagnetonServiceAutoConfiguration implements PriorityOrdered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public MagnetonServiceAutoConfiguration() {
    }

}
