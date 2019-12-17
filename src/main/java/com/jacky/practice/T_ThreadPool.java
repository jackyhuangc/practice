package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.LogUtil;
import com.jacky.common.util.ThreadPoolUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class T_ThreadPool {
    public static void main(String[] args) {

        //testThreadPoolTaskExecutor();
        // 自旋锁运用非常广泛， jdk中的juc包原子操作类中都是, 比如： AtomicInteger
        AtomicInteger atomicInteger = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        //CountDownLatch强调一个线程等多个线程完成某件事情。CyclicBarrier是多个线程互等，等大家都完成。
        //CyclicBarrier cyclicBarrier = new CyclicBarrier(6);

        // 方式1：以自定义的计数器控制流程，结束后主动关闭线程池
        for (int i = 0; i < 100; i++) {
            ThreadPoolUtil.execute(() -> {
                try {
                    Thread.sleep(10000);
                    atomicInteger.addAndGet(1);
                
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("%s------%s,%s,%s", DateUtil.now(), Thread.currentThread().getId(), Thread.currentThread().getName(), 0));


                // 执行完成后将计数器+1
//                try {
//                    c.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                }
            });
        }

        // 方式2：后台线程池方式启动，不能主动关闭主流程，任务可能未完全完成
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            list.add(String.valueOf(i) + "test");
//        }

//        ThreadPoolUtil.execute((t1) -> {
//
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            atomicInteger.addAndGet(1);
//            LogUtil.error(t1);
//
//        }, list, false);

        LogUtil.error(String.format("主线程执行完成，累计执行了%s次", atomicInteger.get()));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        LogUtil.error(String.format("所有线程执行完成，累计执行了%s次", atomicInteger.get()));

        // 若有需要关闭连接池，待所有线程处理完后再关闭，请用execute(runable)方式，主动使用计数器
        ThreadPoolUtil.shutdown();
    }

    public static void testThreadPoolTaskExecutor() {

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(5);
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(15);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(5);
        //线程池维护线程所允许的空闲时间(默认60S)
        // keepAliveTime和maximumPoolSize及BlockingQueue的类型均有关系。如果BlockingQueue是无界的，那么永远不会触发maximumPoolSize，自然keepAliveTime也就没有了意义。
        // 空闲线程等待的最长时间，否则回收
        poolTaskExecutor.setKeepAliveSeconds(3000);

        // 1.优先<CorePoolSize，其次<QueueCapacity，再考虑是否满足<MaxPoolSize  即尽可能的降低系统资源消耗，核心优先使用，否则宁肯暂时等待，等不下去再看是否还有可用线程
        // 2.如果实际的线程任务数量>CorePoolSize+QueueCapacity+MaxPoolSize，则会采用拒绝策略
        // 3.这个策略重试添加当前的任务，他会自动重复调用 execute() 方法，直到成功 在调用者的线程中(主线程)执行被拒绝的任务
        poolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        poolTaskExecutor.initialize();
        CountDownLatch countDownLatch = new CountDownLatch(20);

        for (int i = 0; i < 20; i++) {
            poolTaskExecutor.execute(() -> {
                try {
                    Thread.sleep(1000);
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("------%s,%s,%s", Thread.currentThread().getId(), Thread.currentThread().getName(), poolTaskExecutor.getActiveCount()));
            });
        }

        //阻塞当前线程，直到倒数计数器倒数到0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LogUtil.error(e);
        }

        poolTaskExecutor.shutdown();
    }
}
