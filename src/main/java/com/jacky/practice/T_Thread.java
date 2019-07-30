package com.jacky.practice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/22 19:37
 * @since jdk1.8
 */
public class T_Thread {
    private static List<String> list=new CopyOnWriteArrayList<>();
    public static void main(String[] args) {
        demo4();
    }

    // 如何让两个线程依次执行？
    private static void demo1() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println(String.format("A %s [%s]", i, sf.format(new Date())));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread B = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    A.join(); // Waits for this thread to die.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 3; i++) {
                    System.out.println(String.format("B %s [%s]", i, sf.format(new Date())));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        A.start();
        B.start();
    }

    // 那如何让 两个线程按照指定方式有序交叉运行呢？
    private static void demo2() {
        Object lock = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("INFO: A 等待锁");
                synchronized (lock) {
                    System.out.println("INFO: A 得到了锁 lock");
                    System.out.println("A 1");
                    try {
                        System.out.println("INFO: A 准备进入等待状态，放弃锁 lock 的控制权");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("INFO: 有人唤醒了 A, A 重新获得锁 lock");
                    System.out.println("A 2");
                    System.out.println("A 3");
                }
            }
        });
        Thread B = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("INFO: B 等待锁");
                synchronized (lock) {
                    System.out.println("INFO: B 得到了锁 lock");
                    System.out.println("B 1");
                    System.out.println("B 2");
                    System.out.println("B 3");
                    System.out.println("INFO: B 打印完毕，调用 notify 方法");
                    lock.notify();
                }
            }
        });
        A.start();
        // 有必要先让A获得锁，否则会造成死锁
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        B.start();
    }

    //  四个线程 A B C D，其中 D 要等到 A B C 全执行完毕后才执行，而且 A B C 是同步运行的，使用CountDownLatch倒计数器
    private static void demo3() {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("D线程等待其他3个线程执行完成。。。");
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("D线程执行完成");

            }
        }).start();

        for (int i = 0; i < 3; i++) {
            final int tmp = i + 1;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("线程" + tmp + "开始执行");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("线程" + tmp + "执行完成");
                    countDownLatch.countDown();
                }
            }).start();
        }
    }

    // 使用CyclicBarrier计数器，来实现多线程等待后再执行的操作
    private static void demo4() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        for (int i = 0; i < 3; i++) {
            final int tmp = i + 1;
            new Thread(new Runnable() {

                @Override
                public void run() {

                    System.out.println("线程" + tmp + "正在进行准备工作。。。");
                    try {
                        Thread.sleep(1000 * (tmp - 1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + tmp + "准备工作完成。。。");
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + tmp + "执行工作完成。。。");
                }
            }).start();
        }

/*        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println("主线程工作也执行完成。。。");*/
    }

    // 通过FutureTask结合Callable线程，获取任务执行结果，并返回主线程使用
    private static void demo5() {
        Callable<String> callable = new T_Excutor("XXXXX");

        // 方式一：通过Thread+FutureTask实现异步任务，计算异步结果
        FutureTask<String> task = new FutureTask<>(callable);
        new Thread(task).start();

        System.out.println("ffffffffffff");
        try {
            System.out.println("执行结果：" + task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
