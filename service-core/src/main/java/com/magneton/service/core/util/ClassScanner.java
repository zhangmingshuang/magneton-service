package com.magneton.service.core.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;

/**
 * @author zhangmingshuang
 * @since 2019/5/20
 */
public class ClassScanner {

    public static void forPackage(String... pkgs) {
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
                            ClassScanner.loadClassInPackageByFile(pkg, url);
                            break;
                        case "jar":
                            ClassScanner.loadClassInPackageByJar(pkg, url);
                            break;
                        default:
                            throw new RuntimeException("不支持的文件扫描协议");
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private static void loadClassInPackageByJar(String pkg, URL url) throws Throwable {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
        String folder = pkg.replace('.', '/');
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith(folder) && name.endsWith(".class")) {
                Class.forName(pkg + "." + name.substring(name.lastIndexOf("/") + 1, name.indexOf(".")));
            }
        }
    }

    private static void loadClassInPackageByFile(String pkg, URL url) throws Throwable {
        File file = new File(url.toURI());
        File[] files = file.listFiles();
        loadFiles(pkg, files);
    }

    private static void loadFiles(String pkg, File[] files) throws Throwable {
        for (File f : files) {
            String name = f.getName();
            if (name.indexOf(".") == -1) {
                if (f.isDirectory()) {
                    loadFiles(pkg + "." + name, f.listFiles());
                }
                continue;
            }
            Class.forName(pkg + "." + name.substring(0, name.indexOf(".")));
        }
    }
}
