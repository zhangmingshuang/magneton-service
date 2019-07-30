package com.magneton.service.core.spring.converter;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magneton.service.core.MagnetonConstants;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 13:51
 */
@Configurable
@Component
@ConditionalOnProperty(prefix = MagnetonConstants.PROPERTIES_PREFIX, name = "messageConverter", havingValue = "true", matchIfMissing = true)
public class FastjsonHttpMessageConverters {

    @Bean
    @ConditionalOnMissingBean(FastjsonHttpMessageConvertersConfig.class)
    public FastjsonHttpMessageConvertersConfig fastjsonHttpMessageConvertersConfig() {
        return new FastjsonHttpMessageConvertersConfig() {
            @Override
            public MediaType[] mediaTypes() {
                return new MediaType[]{MediaType.APPLICATION_JSON_UTF8};
            }

            @Override
            public FastJsonConfig fastJsonConfig() {
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                        SerializerFeature.DisableCircularReferenceDetect);
                fastJsonConfig.setCharset(MagnetonConstants.DEFAULT_CHARSET);
                //序列号配置
                SerializeConfig serializeConfig = SerializeConfig.globalInstance;
                //处理long型精度在JS转化时精度缺失问题
                serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
                serializeConfig.put(Long.class, ToStringSerializer.instance);
                serializeConfig.put(Long.TYPE, ToStringSerializer.instance);

                fastJsonConfig.setSerializeConfig(serializeConfig);

                return fastJsonConfig;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(HttpMessageConverters.class)
    public HttpMessageConverters httpMessageConverters(FastjsonHttpMessageConvertersConfig fastjsonHttpMessageConvertersConfig) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        MediaType[] mediaTypes = fastjsonHttpMessageConvertersConfig.mediaTypes();
        converter.setSupportedMediaTypes(Arrays.asList(mediaTypes));
        converter.setFastJsonConfig(fastjsonHttpMessageConvertersConfig.fastJsonConfig());

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(MagnetonConstants.DEFAULT_CHARSET);

        return new HttpMessageConverters(converter, stringHttpMessageConverter);
    }
}
