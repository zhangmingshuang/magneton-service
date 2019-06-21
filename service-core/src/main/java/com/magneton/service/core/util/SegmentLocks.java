package com.magneton.service.core.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
public class SegmentLocks {

    private int mask;
    private ReentrantLock[] locks;

    public SegmentLocks(int level) {
        if (level < 0) {
            level = 1;
        }
        mask = (level + 7) & ~7;
        locks = new ReentrantLock[mask];
        for (int i = 0; i < mask; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public ReentrantLock getLock(String id) {
        return this.getLock(id.hashCode());
    }

    public ReentrantLock getLock(int id) {
        id = id >> 31 == 0 ? id : ~id + 1;
        return locks[id % mask];
    }
}
