package com.magneton.service.support.druid;

import org.springframework.core.PriorityOrdered;

/**
 * 用来支持自动化的Mybatis印射文件配置
 *
 * @author zhangmingshuang
 * @since 2019/4/26
 */
public interface MybatisPropertiesSupport {//extends PriorityOrdered {

    String[] localMapper();

//    @Override
//    default int getOrder() {
//        return HIGHEST_PRECEDENCE;
//    }
}
