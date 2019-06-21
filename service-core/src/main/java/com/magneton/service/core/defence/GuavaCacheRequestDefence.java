package com.magneton.service.core.defence;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springmodules.cache.annotations.Cacheable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于GuavaCache的请求防御实现
 * <p>
 * 由于GuavaCache是基于本地内存实现的，可能会出现占用大量内容的情况发生
 * 导致内存不足，如果在业务上会产生大量Key的时候，建议自己实现使用外部缓存
 * 的实现，以避免内存空间不足导致的服务宕机
 *
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class GuavaCacheRequestDefence implements RequestDefence {

    private RequestDefenceConfig config;
    private LoadingCache<String, Def> cache;

    static class Def {
        private AtomicInteger num;
        private long time;

        public Def(int num, long time) {
            this.num = new AtomicInteger(num);
            this.time = time;
        }
    }

    @Override
    public void afterConfigSet(RequestDefenceConfig config) {
        this.config = config;
        final Integer tokenNum = Integer.valueOf(config.getTokenNum());
        cache = CacheBuilder.newBuilder()
                .maximumSize(Integer.MAX_VALUE)
                .expireAfterWrite(config.getRefreshTime(), TimeUnit.SECONDS)
                .build(new CacheLoader<String, Def>() {
                    @Override
                    public Def load(String key) throws Exception {
                        return new Def(tokenNum, System.currentTimeMillis());
                    }
                });
    }

    @Override
    public void remote(String key) {
        cache.invalidate(key);
    }

    @Override
    public long ttl(String key) {
        Def def = cache.getUnchecked(key);
        if (def == null) {
            return -1;
        }
        if (def.num.get() <= 0) {
            //超过Token限制
            long time = def.time;
            //计算剩余冷却时间
            long gap = config.getRefreshTime() - (System.currentTimeMillis() - time) / 1000;
            if (gap <= 0) {
                return -1;
            }
            //如果存在冷却防御机制
            int defTime = config.getRefreshDefineTime();
            if (defTime > 0) {
                //冷却防御机制，因为GuavaCache没有缓存时间，所以每次只是刷新冷却时间
                def.time = System.currentTimeMillis();
                cache.put(key, def);
                return config.getRefreshTime();
            }
            return gap;
        }
        return -1;
    }

    @Override
    public int decrAcquire(String key) {
        Def def = cache.getUnchecked(key);
        if (def == null) {
            return config.getTokenNum();
        }
        int num = def.num.decrementAndGet();
        cache.put(key, def);
        if (num <= 0) {
            return 0;
        }
        return num;
    }

    @Override
    public Mode mode() {
        return Mode.LOCAL;
    }
}
