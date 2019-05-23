package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.ThreadPoolUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.jacky.common.*;

import java.util.concurrent.*;

public class T_ThreadPool {
    public static void main(String[] args) {

        //testThreadPoolTaskExecutor();

        CountDownLatch countDownLatch = new CountDownLatch(20);
        //CountDownLatch强调一个线程等多个线程完成某件事情。CyclicBarrier是多个线程互等，等大家都完成。
        //CyclicBarrier cyclicBarrier = new CyclicBarrier(6);

        for (int i = 0; i < 20; i++) {
            ThreadPoolUtil.execute((t1, c) -> {
                try {
                    Thread.sleep(1000);
                    c.countDown();
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
            }, 1, countDownLatch);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
//        catch (BrokenBarrierException e) {
//            e.printStackTrace();
//        }

        // 若有需要关闭连接池，待所有线程处理完后再关闭
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

        }

        poolTaskExecutor.shutdown();
    }
}
