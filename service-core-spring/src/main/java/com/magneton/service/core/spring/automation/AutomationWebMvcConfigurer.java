package com.magneton.service.core.spring.automation;

import com.magneton.service.core.MagnetonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 14:16
 * 默认的实现 {@link WebMvcConfigurer}
 * 主要实现对接口实现类 {@link AutomationHandlerInterceptor} 和 {@link AutomationResourceHandler} 的自动化装配
 */
@Component
@Configuration
@ConditionalOnProperty(prefix = MagnetonConstants.PROPERTIES_PREFIX, name = "automation", havingValue = "true", matchIfMissing = true)
public class AutomationWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired(required = false)
    private AutomationHandlerInterceptor[] automationHandlerInterceptors;
    @Autowired
    private AutomationResourceHandler[] automationResourceHandlers;

    @Autowired(required = false)
    private List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers;

    @Bean
    @ConditionalOnMissingBean(AutomationResourceHandler.class)
    @ConditionalOnProperty(prefix = MagnetonConstants.PROPERTIES_PREFIX, name = "automationDefaultResourceHandler", havingValue = "true", matchIfMissing = true)
    public AutomationResourceHandler automationResourceHandler() {
        return () -> ResourceHandlerMapping.builder()
                .add(MagnetonConstants.DEFAULT_RESOURCE_HANDLER,
                        MagnetonConstants.DEFAULT_RESOURCE_LOCATION)
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (automationHandlerInterceptors != null && automationHandlerInterceptors.length > 0) {
            Arrays.sort(automationHandlerInterceptors, (o1, o2) -> o2.getOrder() - o1.getOrder());
            for (AutomationHandlerInterceptor interceptor : automationHandlerInterceptors) {
                InterceptorRegistration interceptorRegistration = registry.addInterceptor(interceptor)
                        .addPathPatterns(interceptor.pathPatterns());
                String[] excludePathPatterns = interceptor.excludePathPatterns();
                if (excludePathPatterns != null) {
                    interceptorRegistration.excludePathPatterns(excludePathPatterns);
                }
            }
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //添加自定义的HandlerMethodArgumentResolver
        resolvers.addAll(handlerMethodArgumentResolvers);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (automationResourceHandlers != null && automationResourceHandlers.length > 0) {
            for (AutomationResourceHandler automationResourceHandler : automationResourceHandlers) {
                ResourceHandlerMapping mapping = automationResourceHandler.mapping();
                if (mapping == null) {
                    continue;
                }
                List<Map.Entry<String[], String[]>> mappings = mapping.getMappings();
                if (mappings == null || mappings.size() < 1) {
                    continue;
                }
                mappings.forEach(m -> registry.addResourceHandler(m.getKey())
                        .addResourceLocations(m.getValue()));
            }
        }
    }

}
