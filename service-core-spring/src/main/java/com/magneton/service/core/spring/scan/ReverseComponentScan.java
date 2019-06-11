package com.magneton.service.core.spring.scan;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangmsh
 * @since 2018-12-06 08:57
 * <p>
 * 实现@ComponentScan无法实现的反向扫描功能
 * ＠ComponentScan只能是启动项目配置目录扫描，在启动项目配置的目录下的类可以被扫描到
 * 但是，如果需要在其他的，如jar包，　或者 module中，需要自己实现自己的扫描功能
 * 那么@ComponentScan是无法做到的，因为@ComponentScan是无法跨模块的
 *
 * <pre>
 * {@code @SpringBootApplication}
 * {@code @ComponentScan("com.xuetu")}
 * {@code public class SnakeDemoApplication {
 *   public static void main(String[] args) {
 *       SpringApplication.run(SnakeDemoApplication.class, args);
 *   }
 *  }}</pre>
 * <p>
 * 此时，项目Ａ可以描述得到所有在com.xuetu包下的组件
 * 但是，该用法在一个多模块依赖的项目时并不是可取的，　因为多模块信赖项目
 * 比如Dao模块，如果存在 mysql, mongodb, redis等各个组件时，
 * 如果使用该注意扫描，就会把mysql, mongodb, redis都扫描进去
 * 所以，此时，建议的做法是，　在模块内创建　@EnableMysql注解
 * 然后使用　＠ReverseComponentScan("com.xuetu.dao.mysql")来扫描mysql的组件
 * 如此，能控制比较细粒度的信赖，高层模块也就不需要了解低层模块的结构等信息
 * <p>
 * 需要注意的是，这个作用要只放在不会被 {@code @Component}扫描到的地方
 * 否则会实际化二次，请注意
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ReverseComponentScanner.class)
public @interface ReverseComponentScan {

    /**
     * 配置要扫描的目录
     *
     * @return 配置的包目录
     * @see ComponentScan#basePackages()
     */
    String[] basePackages() default {};
}
