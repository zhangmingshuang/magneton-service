package com.magneton.service.spring.boot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring环境启动器
 * 用来扫描基于 com.magneton.service.core.spring 包下的所有依赖类
 *
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@Configuration
@ComponentScan(basePackages = "com.magneton.service.core.spring")
public class MagnetonServiceAutoConfiguration {

}
