//package com.magneton.service.core.spring.scan;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author zhangmingshuang
// * @since 2019/7/30
// */
//public class ModuleReverserComponentScan extends ReverseComponentScanner {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleReverserComponentScan.class);
//
//    private List<MagnetonModule> magnetonModules;
//
//    public ModuleReverserComponentScan(List<MagnetonModule> magnetonModules,
//                                       ResourceLoader resourceLoader) {
//        this.magnetonModules = magnetonModules;
//        super.setResourceLoader(resourceLoader);
//        System.out.println(">>ModuleReverserComponentScan");
//    }
//
//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
//                                        BeanDefinitionRegistry registry) {
//
//        if (magnetonModules == null || magnetonModules.size() < 1) {
//            return;
//        }
//        ClassPathBeanDefinitionScanner scanner = super.createScanner(registry);
//        final List<String> basePackages = new ArrayList<>();
//        for (MagnetonModule moduleReverser : magnetonModules) {
//            String[] pkgs = moduleReverser.basePackages();
//            for (String pkg : pkgs) {
//                if (StringUtils.hasText(pkg)) {
//                    LOGGER.info("ModuleReverserComponentScan >> add package {}.", pkg);
//                    basePackages.add(pkg);
//                }
//            }
//        }
//        scanner.scan(StringUtils.toStringArray(basePackages));
//    }
//}
