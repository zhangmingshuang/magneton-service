package com.magneton.service.core.appender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zhangmingshuang
 * @date 2018/11/29 13:40
 * @description 文件写入
 */
public class FileChannelAppender implements FileAppender {

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;

    public FileChannelAppender(String file) {
        this.init(Paths.get(file));
    }

    public FileChannelAppender(Path path) {
        this.init(path);
    }

    private void init(Path path) {
        try {
            this.randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
            this.fileChannel = randomAccessFile.getChannel();
        } catch (FileNotFoundException e) {
            this.close();
            throw new RuntimeException(e);
        }
    }

    @Override
    public long size() throws IOException {
        return this.fileChannel.size();
    }

    @Override
    public void append(byte[] bytes) throws IOException {
        long size = this.fileChannel.size();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.fileChannel.write(buffer, size);
        buffer.clear();
        buffer = null;
    }

    @Override
    public void clean() throws IOException {
        this.fileChannel.truncate(0);
    }

    @Override
    public void close() {
        if (this.randomAccessFile != null) {
            try {
                this.randomAccessFile.close();
            } catch (IOException e2) {

            }
        }
        if (this.fileChannel != null) {
            try {
                this.fileChannel.close();
            } catch (IOException e3) {

            }
        }
    }
}
