package com.magneton.service.core.spring;

import com.alibaba.csp.sentinel.node.StatisticNode;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;


/**
 * @author zhangmingshuang
 * @since 2019/2/21
 */
public class ConcurrentTest {

    @FunctionalInterface
    public static interface Test {

        void test();
    }

    /**
     * 并发测试
     *
     * @param runnerCount 并发数
     * @param runSeconds  持续时间
     * @param test        实现
     */
    public static void start(int runnerCount, int runSeconds, Test test) {
        StatisticNode statisticNode = new StatisticNode();

        TestRunnable runnable = new TestRunnable() {
            @Override
            public void runTest() throws Throwable {
                try {
                    long s = System.currentTimeMillis();
                    test.test();
                    statisticNode.addRtAndSuccess(System.currentTimeMillis() - s, 1);
                } catch (Throwable e) {
                    statisticNode.increaseExceptionQps(1);
                } finally {
                    statisticNode.addPassRequest(1);
                }
            }
        };

        TestRunnable[] trs = new TestRunnable[runnerCount];
        for (int i = 0; i < runnerCount; i++) {
            trs[i] = runnable;
        }
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
        // 开发并发执行数组里定义的内容
        long s = System.currentTimeMillis();
        runSeconds *= 1000;
        try {
            while (true) {
                mttr.runTestRunnables();
                if (System.currentTimeMillis() - s >= runSeconds) {
                    break;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("avgRt:" + statisticNode.avgRt() + ", minRt:" + statisticNode.minRt());
        System.out.println("passQps:" + statisticNode.passQps());
        System.out.println("maxSuccessQps:" + statisticNode.maxSuccessQps()
                + ", successQps:" + statisticNode.successQps());
        System.out.println("totalException:" + statisticNode.totalException()
                + ", exceptionQps:" + statisticNode.exceptionQps());

    }
}
