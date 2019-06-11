package com.magneton.service.core.spring.scan;

import com.magneton.service.core.MagnetonLogger;
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

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> allAnnotationAttributes
                = importingClassMetadata.getAllAnnotationAttributes(ReverseComponentScan.class.getName());

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        final List<String> basePackages = new ArrayList<>();
        allAnnotationAttributes.forEach((key, list) -> {
            for (int i = 0, s = list.size(); i < s; i++) {
                String[] values = (String[]) list.get(i);
                for (String pkg : values) {
                    if (StringUtils.hasText(pkg)) {
                        MagnetonLogger.debug("ReverseComponentScanner >> add package {}.", pkg);
                        basePackages.add(pkg);
                    }
                }
            }
        });
        scanner.scan(StringUtils.toStringArray(basePackages));
    }
}
