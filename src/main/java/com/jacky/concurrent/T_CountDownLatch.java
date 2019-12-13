package com.jacky.concurrent;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/12/13 3:05 下午
 */
public class T_CountDownLatch {

    /***
     * CountDownLatch 倒计时 计算为0时释放所有等待的线程，调用countDown()减一，调用await()方法阻塞
     * CyclicBarrier  加数器 计数达到指定值时释放所有等待线程，调用await()方法计数加1，若加1后的值不等于构造方法的值，则线程阻塞
     * Semaphore    信号灯，用于多个线程访问多个共享资源，如抢车位/抢红包，多个10元红包，N多个人抢
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        //CloseDoor();
        //Conference();
        Parking();
    }

    /**
     * CountDownLatch实例，管理员清理人数，关闭自习室，生产中的例子，用CountDownLatch管理从task拉取的任务放入线程次的过程，放入完成以后，才能返回xxl-job结果，否则阻塞
     */
    public static void CloseDoor() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {

                // 至于每个人在自习室待多长时间，都有差异
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(String.format("%s号同学，已离开：", Thread.currentThread().getName()));
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();

        System.out.println(String.format("同学们都走了，管理员关门了"));
    }

    /**
     * CyclicBarrier实例，开会等人到齐，一起开始做某件事情
     */
    public static void Conference() {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {

            System.out.println(String.format("人都到齐了，开始开会了"));
        });

        // 如果不带回调方法，效果只是没有通知而已，主线程不会退出，不受影响
        //CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {

                // 至于每个人在到会场的时间，都不一样
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(String.format("%s号同事，已签到！", Thread.currentThread().getName()));
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }

    /**
     * Semaphore实例，多辆车抢多个停车位，Synchronized不一样，Synchronized是多线程共享一个资源，存在并发安全问题
     */
    public static void Parking() {
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                try {
                    semaphore.acquire();

                    System.out.println(String.format("%s号车，已入场！", Thread.currentThread().getName()));
                    // 至于每个人的技术不一样，停车的时间不一样
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println(String.format("%s号车，已出场！", Thread.currentThread().getName()));
                }

            }, String.valueOf(i)).start();
        }
    }
}
