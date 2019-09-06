package com.magneton.service.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringApplicationContext
 *
 * @author zhangmsh
 * SpringApplication的内容类
 * 可以使用该类获取到Spring管理的Bean
 */
@Component
public final class ApplicationContext implements ApplicationContextAware {

    private static org.springframework.context.ApplicationContext ac;

    @Override
    public void setApplicationContext(
            org.springframework.context.ApplicationContext applicationContext)
            throws BeansException {
        ac = applicationContext;
    }

    public org.springframework.context.ApplicationContext getApplication() {
        return ac;
    }

    public static org.springframework.context.ApplicationContext getApplicationContext() {
        return ac;
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T) ac.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return (T) ac.getBean(beanName, clazz);
    }

    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }

}
