package com.magneton.service.core.spring.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhangmsh
 * @since 2018-12-06 08:57
 */
public class ReverseComponentScanner implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseComponentScanner.class);

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    public ClassPathBeanDefinitionScanner createScanner(BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        return scanner;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> allAnnotationAttributes
                = importingClassMetadata.getAllAnnotationAttributes(ReverseComponentScan.class.getName());

        ClassPathBeanDefinitionScanner scanner = this.createScanner(registry);

        final List<String> basePackages = new ArrayList<>();
        allAnnotationAttributes.forEach((key, list) -> {
            for (int i = 0, s = list.size(); i < s; i++) {
                String[] values = (String[]) list.get(i);
                for (String pkg : values) {
                    if (StringUtils.hasText(pkg)) {
                        LOGGER.info("ReverseComponentScanner >> add package [{}]", pkg);
                        basePackages.add(pkg);
                    }
                }
            }
        });
        scanner.scan(StringUtils.toStringArray(basePackages));
    }
}
