package com.magneton.service.core.spring.automation;

import lombok.Getter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 14:23
 *
 */
@Getter
public class ResourceHandlerMapping {

    private List<Map.Entry<String[], String[]>> mappings = new ArrayList<>(1);

    public static ResourceHandlerMappingBuilder builder() {
        return new ResourceHandlerMappingBuilder();
    }

    public static class ResourceHandlerMappingBuilder {
        private ResourceHandlerMapping mapping;
        private String[] tempHandler;

        private ResourceHandlerMappingBuilder() {
            this.mapping = new ResourceHandlerMapping();
        }

        public ResourceHandlerMappingBuilder add(String resourceHandler, String resourceLocation) {
            mapping.mappings.add(new AbstractMap.SimpleEntry<>(new String[]{resourceHandler}, new String[]{resourceLocation}));
            return this;
        }

        public ResourceHandlerMappingBuilder add(String[] resourceHandler, String[] resourceLocation) {
            mapping.mappings.add(new AbstractMap.SimpleEntry<>(resourceHandler, resourceLocation));
            return this;
        }

        /**
         * 配置resourceHandler.该方法与 {@link #location(String...)}配套使用
         *
         * @param resourceHandler 静态资源处理器
         * @return 构建器
         */
        public ResourceHandlerMappingBuilder handler(String... resourceHandler) {
            this.tempHandler = resourceHandler;
            return this;
        }

        /**
         * 配置resourceLocations， 该方法不能前置调用，必须是在调用了 {@link #handler(String...)}之前调用
         * 否则认为不合法调用，会丢弃不合理的配置
         *
         * @param resourceLocation 静态资源处理器
         * @return 构建器
         */
        public ResourceHandlerMappingBuilder location(String... resourceLocation) {
            if (tempHandler == null || tempHandler.length < 1) {
                return this;
            }
            return this.add(tempHandler, resourceLocation);
        }

        public ResourceHandlerMapping build() {
            return this.mapping;
        }
    }
}
