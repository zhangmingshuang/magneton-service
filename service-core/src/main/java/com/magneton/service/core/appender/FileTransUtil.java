package com.magneton.service.core.appender;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @date 2018/11/29 20:24
 * @description
 */
public class FileTransUtil {

    public static final long merge(Path file1, Path file2) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file1.toFile());
             FileChannel fileChannel1 = fos.getChannel();
             FileInputStream fis = new FileInputStream(file2.toFile());
             FileChannel fileChannel2 = fis.getChannel()) {

            fileChannel2.transferTo(0, fileChannel2.size(), fileChannel1);

            return fileChannel1.size();
        }
    }
}
