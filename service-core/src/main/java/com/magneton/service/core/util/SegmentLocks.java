package com.magneton.service.core.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
public class SegmentLocks {

    private static final int DEFAULT_CAPACITY = 1 << 2;

    private int capacity;
    private ReentrantLock[] locks;

    public SegmentLocks() {
        this(DEFAULT_CAPACITY);
    }

    public SegmentLocks(int initCapacity) {
        this.capacity = this.nextSize(initCapacity);
        this.locks = new ReentrantLock[capacity];
        for (int i = 0; i < this.capacity; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public ReentrantLock getLock(String id) {
        return this.getLock(id.hashCode());
    }

    public ReentrantLock getLock(int id) {
        id = id >> 31 == 0 ? id : ~id + 1;
        return locks[id % capacity];
    }

    /**
     * 用来处理数值为一定是2的倍数
     *
     * @param i 任务整数
     * @return 2的倍数
     */
    private int nextSize(int i) {
        int newCapacity = i;
        newCapacity |= newCapacity >>> 1;
        newCapacity |= newCapacity >>> 2;
        newCapacity |= newCapacity >>> 4;
        newCapacity |= newCapacity >>> 8;
        newCapacity |= newCapacity >>> 16;
        newCapacity++;
        return newCapacity;
    }
}
