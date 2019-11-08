package com.magneton.service.core.appender;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author zhangmingshuang
 * @date 2018/11/29 13:40
 * @description
 */
public interface FileAppender extends Closeable {

    /**
     * 取得当前文件的大小
     *
     * @return
     * @throws IOException
     */
    long size() throws IOException;

    /**
     * 添加内容
     *
     * @param bytes
     * @throws IOException
     */
    void append(byte[] bytes) throws IOException;

    /**
     * 清空内容
     *
     * @throws IOException
     */
    void clean() throws IOException;

}
