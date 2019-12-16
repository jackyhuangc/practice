package com.jacky.concurrent;

import com.jacky.common.util.LogUtil;

import java.util.concurrent.*;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-16 18:42
 */
public class T_BlockingQueue {

    public static void main(String[] args) throws InterruptedException {

        //throwExc();

        // returnBoolean();

        //sync();

        syncQueue();
    }

    /**
     * 抛异常版本
     */
    public static void throwExc() {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);

        LogUtil.info(String.valueOf(blockingQueue.add("a")));
        LogUtil.info(String.valueOf(blockingQueue.add("b")));
        LogUtil.info(String.valueOf(blockingQueue.add("c")));

        // 队列慢将抛异常 java.lang.IllegalStateException: Queue full
        //LogUtil.info(String.valueOf(blockingQueue.add("d")));


        LogUtil.info(String.valueOf(blockingQueue.element()));

        LogUtil.info(String.valueOf(blockingQueue.remove("a")));
        LogUtil.info(String.valueOf(blockingQueue.remove("b")));
        LogUtil.info(String.valueOf(blockingQueue.remove("c")));

        // remove方法，jdk1.8后不会再抛异常，返回的是boolean类型
        LogUtil.info(String.valueOf(blockingQueue.remove("d")));
    }

    /**
     * 返回boolean值版本
     */
    public static void returnBoolean() {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);

        LogUtil.info(String.valueOf(blockingQueue.offer("a")));
        LogUtil.info(String.valueOf(blockingQueue.offer("b")));
        LogUtil.info(String.valueOf(blockingQueue.offer("c")));
        LogUtil.info(String.valueOf(blockingQueue.offer("d")));

        LogUtil.info(String.valueOf(blockingQueue.peek()));

        LogUtil.info(String.valueOf(blockingQueue.poll()));
        LogUtil.info(String.valueOf(blockingQueue.poll()));
        LogUtil.info(String.valueOf(blockingQueue.poll()));
        LogUtil.info(String.valueOf(blockingQueue.poll()));
    }

    /**
     * 阻塞版本
     * <p>
     * 可用于 MQ消息队列，biz task线程池创建方案
     */
    public static void sync() throws InterruptedException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);

        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");

        // 队列慢将阻塞，直至队列可用
        // blockingQueue.put("d");

        blockingQueue.take();
        blockingQueue.take();
        blockingQueue.take();
        // 队列空将阻塞，直至队列可用
        blockingQueue.take();
    }

    /**
     * 同步队列，不存储元素（默认 fair=false 非公平锁）
     */
    public static void syncQueue() {
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                LogUtil.info("put a");
                blockingQueue.put("a");
                LogUtil.info("put b");
                blockingQueue.put("b");
                LogUtil.info("put c");
                blockingQueue.put("c");
                LogUtil.info("put d");
                blockingQueue.put("d");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "AAA").start();

        new Thread(() -> {

            try {

                TimeUnit.SECONDS.sleep(3);
                LogUtil.info("take "+blockingQueue.take());

                TimeUnit.SECONDS.sleep(3);
                LogUtil.info("take "+blockingQueue.take());

                TimeUnit.SECONDS.sleep(3);
                LogUtil.info("take "+blockingQueue.take());

                TimeUnit.SECONDS.sleep(3);
                LogUtil.info("take "+blockingQueue.take());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "BBB").start();
    }
}
