package com.magneton.service.core.spring.automation;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 14:19
 *  SpringMvc中，所有的请求会被认为是Servlet请求，所以需要额外的配置静态资源的映射列表
 * 如：/static/ 下是静态资源目录， 对应项目 classpath:/static/下的目录
 * 使用该接口可以实现不需要额外的实现
 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addResourceHandlers(ResourceHandlerRegistry)}
 * 方法，而直接可以被自动化配置进去
 */
public interface AutomationResourceHandler {
    /**
     * 资源地址映射配置
     *
     * @return 静态资源处理器映射
     */
    ResourceHandlerMapping mapping();
}
