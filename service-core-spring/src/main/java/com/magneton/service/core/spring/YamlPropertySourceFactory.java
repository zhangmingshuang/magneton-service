package com.magneton.service.core.spring;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

/**
 * {@link PropertySource}加载配置文件时，默认使用的是{@link DefaultPropertySourceFactory}
 * 该加载器是不支持的yaml加载的
 * 所以，扩展该类型以兼容yaml的配置加载
 * <p>
 * 使用方式为：
 * <pre>
 *      @PropertySource(value = "classpath:redis-multi.yml",
 *      factory = YamlPropertySourceFactory.class)
 * </pre>
 *
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name,
                                                  EncodedResource resource) throws IOException {

        if (resource == null) {
            return super.createPropertySource(name, resource);
        }
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource());
        return sources.get(0);
    }
}
