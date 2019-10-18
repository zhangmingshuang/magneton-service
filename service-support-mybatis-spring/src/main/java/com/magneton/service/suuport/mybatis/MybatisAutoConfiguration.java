package com.magneton.service.suuport.mybatis;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/5/5
 */
@Configuration
public class MybatisAutoConfiguration {

    @Bean
    @Primary
    public MybatisProperties mybatisProperties(
            @Autowired MybatisPropertiesSupport[] mybatisPropertiesSupport,
            @Autowired(required = false) org.apache.ibatis.session.Configuration configuration,
            @Autowired(required = false) MybatisSessionConfiuration mybatisSessionConfiuration) {

        if (configuration == null) {
            configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
        }

        if (mybatisSessionConfiuration != null) {
            mybatisSessionConfiuration.config(configuration);
        }

        final Logger LOGGER = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

        List<String> locations = new ArrayList<>();
        for (MybatisPropertiesSupport support : mybatisPropertiesSupport) {
            String[] localMappers = support.localMapper();
            locations.addAll(Arrays.asList(localMappers));
            LOGGER.info("MybatisProperties>> {}", Arrays.toString(localMappers));
        }
        String[] mappers = new String[locations.size()];
        MybatisProperties mybatisProperties = new MybatisProperties();
        mybatisProperties.setConfiguration(configuration);
        mybatisProperties.setMapperLocations(locations.toArray(mappers));

        return mybatisProperties;
    }

}
