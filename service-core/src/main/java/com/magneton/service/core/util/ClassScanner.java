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

    public static List<Class> findWithAnnotation(Class<? extends Annotation> annotationClass, boolean iterator, String... pkgs) {
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
                            return ClassScanner.loadClassInPackageByFile(pkg, iterator, url, annotationClass);
                        case "jar":
                            return ClassScanner.loadClassInPackageByJar(pkg, iterator, url, annotationClass);
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

    public static List<Class> findWithAnnotation(Class<? extends Annotation> annotationClass, String... pkgs) {
        return findWithAnnotation(annotationClass, false, pkgs);
    }

    public static List<Class> forPackage(String... pkgs) {
        return findWithAnnotation(null, pkgs);
    }

    public static List<Class> iteratorPackage(String... pkgs) {
        return findWithAnnotation(null, true, pkgs);
    }

    private static List<Class> loadClassInPackageByJar(String pkg, boolean iterator, URL url, Class annotationClass) throws Throwable {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
        String folder = pkg.replace('.', '/');
        List<Class> list = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (jarEntry.isDirectory()) {
                continue;
            }
            if (!name.startsWith(folder) || !name.endsWith(".class")) {
                continue;
            }
            if (!iterator && !name.substring(0, name.lastIndexOf('/')).equals(folder)) {
                continue;
            }
            Class<?> aClass = classLoader.loadClass(name.substring(0, name.indexOf(".")).replace('/', '.'));
            if (annotationClass != null
                && aClass.getAnnotation(annotationClass) == null) {
                continue;
            }
            list.add(aClass);
        }
        return list;
    }

    private static List<Class> loadClassInPackageByFile(String pkg, boolean iterator, URL url, Class annotationClass) throws Throwable {
        File file = new File(url.toURI());
        File[] files = file.listFiles();
        return loadFiles(pkg, iterator, files, annotationClass);
    }

    private static List<Class> loadFiles(String pkg, boolean iterator, File[] files, Class annotationClass) throws Throwable {
        List<Class> list = new ArrayList<>();
        for (File f : files) {
            String name = f.getName();
            if (name.indexOf(".") == -1) {
                if (f.isDirectory()) {
                    if (!iterator) {
                        continue;
                    }
                    List<Class> classes = loadFiles(pkg + "." + name, iterator, f.listFiles(), annotationClass);
                    if (classes != null && !classes.isEmpty()) {
                        list.addAll(classes);
                    }
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
