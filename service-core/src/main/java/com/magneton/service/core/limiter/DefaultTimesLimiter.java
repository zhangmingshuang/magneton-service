package com.magneton.service.core.limiter;

import com.magneton.service.core.util.SegmentLocks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于TreeMap的默认次数限制器
 * <p>
 * 使用示例与说明
 * <p>
 * 1.制定规则
 * <pre>
 * Map<String, LimiterRule> rules = new HashMap<>();
 * LimiterRule rule = new LimiterRule();
 * rule.setExpireIn(60);//设置时间限制的时间范围
 * rule.setTimes(3);//表示在时间限制范围内，只允许请求3次数
 * rules.put("rule",rule);//设置规则名称为"rule"
 * </pre>
 * 2. 创建限制器
 * <pre>
 *  TimesLimiterConfig config = new TimesLimiterConfig();
 *  config.setRules(rules);
 *  TimesLimiter limiter = new DefaultTimesLimiter();
 *  limiter.afterConfigSet(config);
 * </pre>
 * 3.使用
 * <pre>
 *  boolean increase = limiter.increase("任意Key","rule");
 * </pre>
 * 该方法会返回是否成功，如果失败表示没有足够的次数可以消耗，需要在配置的过期时间之后才可以再次允许
 *
 * @author zhangmingshuang
 * @since 2019/6/20
 */
public class DefaultTimesLimiter implements TimesLimiter {

    public static class Limiter {
        private long createTime;
        private LongAdder times;
        private LimiterRule rule;
        private long expireIn;

        public Limiter(LimiterRule rule) {
            this.rule = rule;
            if (this.rule != null) {
                this.times = new LongAdder();
                this.createTime = System.currentTimeMillis();
                this.expireIn = rule.getExpireIn() * 1000;
            }
        }

        public boolean isNull() {
            return rule == null;
        }

        public boolean increase() {
            if (rule == null) {
                return true;
            }
            times.increment();
            int limit = rule.getTimes();
            if (times.intValue() <= limit) {
                return true;
            }
            long timePast = System.currentTimeMillis() - createTime;
            if (timePast >= expireIn) {
                //限制过时了
                createTime = System.currentTimeMillis();
                times.reset();
                return true;
            }
            return false;
        }
    }


    private Map<String, Map<String, Limiter>> limiters = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 65535;
        }
    };

    private TimesLimiterConfig config;
    private SegmentLocks segmentLocks;

    @Override
    public void afterConfigSet(TimesLimiterConfig config) {
        this.config = config;
        segmentLocks = new SegmentLocks(1);
    }

    @Override
    public int remain(String key, String rule) {
        Map<String, Limiter> keyLimit = limiters.get(key);
        Limiter limiter;
        if (keyLimit == null
                || (limiter = keyLimit.get(rule)) == null) {
            LimiterRule limiterRule = config.getRules().get(rule);
            if (limiterRule == null || config.isForce()) {
                LimiterRule defaultRule = config.getDefaultRule();
                return defaultRule.getTimes();
            }
            return limiterRule.getTimes();
        }
        if (limiter.isNull()) {
            return -1;
        }
        int times = limiter.rule.getTimes() - limiter.times.intValue();
        return times < 0 ? 0 : times;
    }

    @Override
    public boolean increase(String key, String rule, int incr) {
        Map<String, Limiter> keyLimit = limiters.get(key);
        if (keyLimit == null) {
            ReentrantLock lock = segmentLocks.getLock(key);
            lock.lock();
            try {
                keyLimit = limiters.get(key);
                if (keyLimit == null) {
                    Map<String, Limiter> limiter = new ConcurrentHashMap<>(1, 1);
                    limiters.put(key, limiter);
                    keyLimit = limiter;
                }
            } finally {
                lock.unlock();
            }
        }
        Limiter limiter = keyLimit.get(rule);
        if (limiter == null) {
            //没有获取到规则
            ReentrantLock lock = segmentLocks.getLock(key);
            lock.lock();
            try {
                limiter = keyLimit.get(rule);
                if (limiter == null) {
                    LimiterRule limiterRule = config.getRules().get(rule);
                    if (limiterRule == null && config.isForce()) {
                        limiterRule = new LimiterRule();
                        LimiterRule defaultRule = config.getDefaultRule();
                        limiterRule.setTimes(defaultRule.getTimes());
                        limiterRule.setExpireIn(defaultRule.getExpireIn());
                    }
                    Limiter l = new Limiter(limiterRule);
                    keyLimit.put(rule, l);
                    limiter = l;
                }
            } finally {
                lock.unlock();
            }
        }
        return limiter.increase();
    }
}
