/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magneton.service.core.spring.redis;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Redis connection configuration using Lettuce.
 *
 * @author Mark Paluch
 * @author Andy Wilkinson
 */
class MultiLettuceConnectionConfiguration extends MultiRedisConnectionConfiguration {

    private final LettuceRedisProperties properties;

    private final List<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;

    MultiLettuceConnectionConfiguration(LettuceRedisProperties properties,
                                        ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                        ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                        ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers) {
        super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
        this.properties = properties;
        this.builderCustomizers = builderCustomizers.getIfAvailable(Collections::emptyList);
    }


    public LettuceConnectionFactory getRedisConnectionFactory() throws UnknownHostException {
        ClientResources clientResources = DefaultClientResources.create();
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(
                clientResources, this.properties.getLettuce().getPool());
        LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(clientConfig);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(
            LettuceClientConfiguration clientConfiguration) {
        if (getSentinelConfig() != null) {
            return new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new LettuceConnectionFactory(getClusterConfiguration(),
                    clientConfiguration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(
            ClientResources clientResources, LettuceRedisProperties.Pool pool) {
        LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(this.properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientResources(clientResources);
        customize(builder);
        return builder.build();
    }

    private LettuceClientConfigurationBuilder createBuilder(LettuceRedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new PoolBuilderFactory().createBuilder(pool);
    }

    private LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }
        if (this.properties.getTimeout() != null) {
            builder.commandTimeout(this.properties.getTimeout());
        }
        if (this.properties.getLettuce() != null) {
            LettuceRedisProperties.Lettuce lettuce = this.properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null
                    && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(
                        this.properties.getLettuce().getShutdownTimeout());
            }
        }
        return builder;
    }

    private void customizeConfigurationFromUrl(
            LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    private void customize(
            LettuceClientConfigurationBuilder builder) {
        for (LettuceClientConfigurationBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        public LettuceClientConfigurationBuilder createBuilder(LettuceRedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder()
                    .poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig getPoolConfig(LettuceRedisProperties.Pool properties) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }

    }

}
