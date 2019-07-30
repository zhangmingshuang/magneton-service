package com.magneton.service.core.spring.automation;

import org.springframework.core.annotation.Order;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 17:29
 *  初始化
 */
public interface AutomationInitialize {

    void initialize() throws Throwable;

    /**
     * 取得排序
     *
     * @return 排序值
     */
    default int getOrder() {
        Order order = this.getClass().getAnnotation(Order.class);
        return order == null ? 0 : order.value();
    }
}
