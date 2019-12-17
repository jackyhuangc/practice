package com.jacky.concurrent;

import com.jacky.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-16 15:24
 */
public class T_Lock {

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {

//        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
//        new Thread(reentrantLockDemo, "A").start();
//        new Thread(reentrantLockDemo, "B").start();
//
//
//        SpinLockDemo spinLock = new SpinLockDemo();
//        new Thread(() -> {
//
//            spinLock.myLock();
//
//            LogUtil.info(String.format("%s 获取到锁，开始执行任务...", Thread.currentThread().getName()));
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            spinLock.myUnLock();
//
//        }, "C").start();
//        new Thread(() -> {
//
//            spinLock.myLock();
//
//            LogUtil.info(String.format("%s 获取到锁，开始执行任务...", Thread.currentThread().getName()));
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            spinLock.myUnLock();
//        }, "D").start();


        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();

        // 先创建5个写锁线程    写的顺序必须是原子操作 1begin-1end,2begin-2end......
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {

                readWriteLockDemo.put(Thread.currentThread().getName(), new Random().nextInt(10));
            }, String.valueOf(i)).start();
        }

        // 再创建5个读锁线程，   读的顺序可以插队，可以1begin,2begin,4begin,2end,1end...... 同时，只要写锁未释放，读锁永远只有等待，读写分离
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {

                readWriteLockDemo.get(Thread.currentThread().getName());
            }, String.valueOf(i)).start();
        }

    }
}

/**
 * 独占锁：指该锁一次只能被一个线程所持有，ReentrantLock和Synchronized都是独占锁。
 * <p>
 * 共享锁：只该锁可以被多个线程锁持有。ReentrantReadWriteLock的读锁是共享锁，写锁是独占锁。该锁的共享可以保证并发读是非常高效的，
 * 读写、写读、写写的过程都是互斥的，不能共存。
 * 读写分离 读不会插队到写的过程中，互斥。
 * 写锁必须保证原子性  1begin-1end后才能2begin-2end....
 * 读锁可以打乱顺序    1begin,3begin,2begin ,2end,1end,3end.....
 * 适用场景：redis机制
 */
class ReadWriteLockDemo {

    private volatile Map<String, Object> map = new HashMap<>();

    ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void get(String key) {

        // 读写锁是互斥的，在这期间不会有读锁插队，严格读写分离
        rwLock.readLock().lock();

        try {
            LogUtil.info(String.format("%s *** read lock invoke begin", Thread.currentThread().getName()));

            TimeUnit.SECONDS.sleep(5);
            Object object = map.get(key);

            LogUtil.info(String.format("%s *** read lock invoke end", Thread.currentThread().getName()));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void put(String key, Object value) {

        // 读写锁是互斥的，在这期间不会有读锁插队，严格读写分离
        rwLock.writeLock().lock();

        try {
            LogUtil.info(String.format("%s *** write lock invoke begin", Thread.currentThread().getName()));

            TimeUnit.SECONDS.sleep(5);
            map.put(key, value);

            LogUtil.info(String.format("%s *** write lock invoke end", Thread.currentThread().getName()));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

/**
 * 尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消化，缺点是循环会消化CPU。因此低并发用自旋，高并发(锁竞争激烈)反而不能用自旋锁。
 * <p>
 * 在JDK1.6中，Java虚拟机提供-XX:+UseSpinning参数来开启自旋锁，使用-XX:PreBlockSpin参数来设置自旋锁等待的次数。
 * 在JDK1.7开始，自旋锁的参数被取消，虚拟机不再支持由用户配置自旋锁，自旋锁总是会执行，自旋锁次数也由虚拟机自动调整。
 * <p>
 * <p>
 * 适用场景：
 * 自旋锁运用非常广泛， jdk中的juc包原子操作类中都是, 比如： AtomicInteger
 */
class SpinLockDemo {
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void myLock() {
        Thread thread = Thread.currentThread();
        LogUtil.info(String.format("%s *** myLock invoke", thread.getName()));
        while (!atomicReference.compareAndSet(null, thread)) {

        }
    }

    public void myUnLock() {
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread, null);
        LogUtil.info(String.format("%s *** myUnLock invoke", thread.getName()));
    }
}

/**
 * 同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码。或者同一个线程在外层方法获取锁的时候，在进入内层方法时会自动获取锁。
 * 简单来说，就是线程可进入任何一个已经拥有的锁所同步着的代码块。可重入锁最大的作用就是避免死锁。
 * <p>
 * 适用场景：多线程访问同一个资源
 */
class ReentrantLockDemo implements Runnable {

    Lock lock = new ReentrantLock();

    @Override
    public void run() {

        get();
    }

    public void get() {
        lock.lock();
        try {
            LogUtil.info(String.format("%s *** get invoke......", Thread.currentThread().getName()));
            set();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void set() {
        lock.lock();
        try {
            LogUtil.info(String.format("%s *** set invoke......", Thread.currentThread().getName()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
