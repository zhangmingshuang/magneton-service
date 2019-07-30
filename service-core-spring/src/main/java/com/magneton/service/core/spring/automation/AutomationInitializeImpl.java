package com.magneton.service.core.spring.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 17:32
 */
@Component
@Configurable
public class AutomationInitializeImpl implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomationInitializeImpl.class);

    private static class Initializer {

        private AutomationInitialize[] automationInitializes;

        public Initializer(AutomationInitialize[] automationInitializes) {
            this.automationInitializes = automationInitializes;
        }

        public void initialize() {
            for (int i = 0, l = automationInitializes.length; i < l; ++i) {
                AutomationInitialize automationInitialize = automationInitializes[i];
                if (automationInitialize == null) {
                    continue;
                }
                try {
                    long s = System.currentTimeMillis();
                    automationInitialize.initialize();

                    Class clazz = automationInitialize.getClass();

                    Package pkg = clazz.getPackage();

                    LOGGER.info("initialization {}.{} finish. used {}ms",
                            pkg.getName(), clazz.getSimpleName(), (System.currentTimeMillis() - s));
                } catch (Throwable e) {
                    LOGGER.error("initialization {} exception", automationInitialize.getClass());
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    @Bean
    public Initializer initializer(@Autowired(required = false)
                                           AutomationInitialize[] automationInitializes) {
        if (automationInitializes == null || automationInitializes.length < 1) {
            return null;
        }
        Arrays.sort(automationInitializes, (o1, o2) -> o2.getOrder() - o1.getOrder());
        return new Initializer(automationInitializes);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null && beanName.equals("initializer")
                && bean instanceof Initializer) {
            ((Initializer) bean).initialize();
        }
        return bean;
    }
}
