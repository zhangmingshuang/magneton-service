package com.magneton.service.support.druid.util;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class DataUtils {

    public static final <T> T singleResult(@Nullable List<T> results) {
        if (CollectionUtils.isEmpty(results)){
            return null;
        }
        if (results.size() > 1){
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.get(0);
    }
}
