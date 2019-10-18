package com.magneton.service.core.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author zhangmingshuang
 * @since 2019/5/20
 */
public class ClassScanner {

    public static List<Class> findWithAnnotation(Class<? extends Annotation> annotationClass, String... pkgs) {
        ClassLoader classLoader = ClassScanner.class.getClassLoader();
        try {
            for (String pkg : pkgs) {
                String folder = pkg.replace('.', '/');
                Enumeration<URL> resources = classLoader.getResources(folder + "/");
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    String protocol = url.getProtocol();
                    switch (protocol) {
                        case "file":
                            return ClassScanner.loadClassInPackageByFile(pkg, url, annotationClass);
                        case "jar":
                            return ClassScanner.loadClassInPackageByJar(pkg, url, annotationClass);
                        default:
                            throw new RuntimeException("不支持的文件扫描协议");
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return Collections.emptyList();
    }

    public static List<Class> forPackage(String... pkgs) {
        return findWithAnnotation(null, pkgs);
    }


    private static List<Class> loadClassInPackageByJar(String pkg, URL url, Class annotationClass) throws Throwable {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
        String folder = pkg.replace('.', '/');
        List<Class> list = new ArrayList<>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith(folder) && name.endsWith(".class")) {
                Class<?> aClass = Class.forName(pkg + "." + name.substring(name.lastIndexOf("/") + 1, name.indexOf(".")));
                if (annotationClass != null
                        && aClass.getAnnotation(annotationClass) == null) {
                    continue;
                }
                list.add(aClass);
            }
        }
        return list;
    }

    private static List<Class> loadClassInPackageByFile(String pkg, URL url, Class annotationClass) throws Throwable {
        File file = new File(url.toURI());
        File[] files = file.listFiles();
        return loadFiles(pkg, files, annotationClass);
    }

    private static List<Class> loadFiles(String pkg, File[] files, Class annotationClass) throws Throwable {
        List<Class> list = new ArrayList<>();
        for (File f : files) {
            String name = f.getName();
            if (name.indexOf(".") == -1) {
                if (f.isDirectory()) {
                    loadFiles(pkg + "." + name, f.listFiles(), annotationClass);
                }
                continue;
            }
            Class<?> aClass = Class.forName(pkg + "." + name.substring(0, name.indexOf(".")));
            if (annotationClass != null
                    && aClass.getAnnotation(annotationClass) == null) {
                continue;
            }
            list.add(aClass);
        }
        return list;
    }
}
