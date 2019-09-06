package com.magneton.service.support.druid;

import org.apache.ibatis.session.Configuration;

/**
 * @author zhangmingshuang
 * @since 2019/9/3
 */
public interface MybatisSessionConfiuration {
    void config(Configuration configuration);
}
