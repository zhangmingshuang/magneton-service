package com.magneton.service.core.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PressureMeasurement {

    private CountDownLatch start;
    private CountDownLatch end;
    private int pollSize = 10;

    public PressureMeasurement() {
        this(10);
    }

    public PressureMeasurement(int pollSize) {
        this.pollSize = pollSize;
        start = new CountDownLatch(1);
        end = new CountDownLatch(pollSize);
    }

    public long latch(MyFunctionalInterface functionalInterface) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(pollSize);
        for (int i = 0; i < pollSize; i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        start.await();
                        functionalInterface.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        end.countDown();
                    }
                }
            };
            executorService.submit(run);
        }

        start.countDown();
        long s = System.currentTimeMillis();
        end.await();
        long e = System.currentTimeMillis();
        executorService.shutdown();
        return e - s;
    }

    @FunctionalInterface
    public interface MyFunctionalInterface {
        void run();
    }
}
