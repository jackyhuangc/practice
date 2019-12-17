package com.jacky.cas;


import com.jacky.common.util.LogUtil;
import com.jacky.rpc.dto.UserDto;
import lombok.Data;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 请输入描述
 *CAS：Compare and Swap，即比较再交换。
 *
 * jdk5增加了并发包java.util.concurrent.*,其下面的类使用CAS算法实现了区别于synchronouse同步锁的一种乐观锁。JDK 5之前Java语言是靠synchronized关键字保证同步的，这是一种独占锁，也是是悲观锁
 *
 * CAS（Compare and swap），即比较并交换，也是实现我们平时所说的自旋锁或乐观锁的核心操作。
 *
 * 适用场景：采用乐观锁机制，比Synchronized效率高
 * @author Jacky
 * @date 2019/12/4 2:43 PM
 */
public class T_CAS {

    static UserDto user = new UserDto();

    static {
        user.setName("张三");
        user.setAge(10);
    }

    static AtomicReference<UserDto> atomicReference = new AtomicReference<>(user);

    // 更类似数据库的乐观锁，时间戳版本号的概念
    static AtomicStampedReference<UserDto> atomicStampedReference = new AtomicStampedReference<>(user, 0);

    public static void main(String[] args) throws InterruptedException {

        System.out.println(String.format("**************验证ABA问题的产生****************"));

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            // 1.线程1和线程2同时取到了【张三】
            UserDto zhangsan = atomicReference.get();

            try {
                // 2.线程1只休眠1秒，立即执行修改逻辑
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 3.线程1先修改
            UserDto lisi = new UserDto();
            lisi.setName("李四");
            lisi.setAge(11);
            boolean blOk1 = atomicReference.compareAndSet(zhangsan, lisi);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s", Thread.currentThread().getName(), zhangsan.getName(), lisi.getName(), blOk1));
            boolean blOk2 = atomicReference.compareAndSet(lisi, zhangsan);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s", Thread.currentThread().getName(), lisi.getName(), zhangsan.getName(), blOk2));
        }, "线程1").start();

        new Thread(() -> {
            // 1.线程1和线程2同时取到了【张三】
            UserDto zhangsan = atomicReference.get();

            try {

                // 2.线程2休眠2秒，比线程1延迟执行修改逻辑
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 3.线程2后修改，虽然能正常修改，但是过程已经被线程1修改多次，也就是ABA问题
            UserDto lisi = new UserDto();
            lisi.setName("李四");
            lisi.setAge(11);
            boolean blOk1 = atomicReference.compareAndSet(zhangsan, lisi);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s", Thread.currentThread().getName(), zhangsan.getName(), lisi.getName(), blOk1));
            boolean blOk2 = atomicReference.compareAndSet(lisi, zhangsan);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s", Thread.currentThread().getName(), lisi.getName(), zhangsan.getName(), blOk2));
        }, "线程2").start();

        TimeUnit.SECONDS.sleep(5);
        System.out.println(String.format("**************验证ABA问题的解决****************"));

        new Thread(() -> {
            // 1.线程3和线程4同时取到了【张三】
            UserDto zhangsan = atomicStampedReference.getReference();
            // 版本号原子引用，增加了版本号机制，更类似数据库的乐观锁，时间戳版本号的概念
            int stamp = atomicStampedReference.getStamp();
            LogUtil.info(String.format("%s第一次取到【%s】，版本【%s】", Thread.currentThread().getName(), zhangsan.getName(), stamp));

            try {
                // 2.线程1只休眠1秒，立即执行修改逻辑
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 3.线程1先修改
            UserDto lisi = new UserDto();
            lisi.setName("李四");
            lisi.setAge(11);
            boolean blOk1 = atomicStampedReference.compareAndSet(zhangsan, lisi, stamp, ++stamp);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s，版本变更为【%s】", Thread.currentThread().getName(), zhangsan.getName(), lisi.getName(), blOk1, atomicStampedReference.getStamp()));
            boolean blOk2 = atomicStampedReference.compareAndSet(lisi, zhangsan, stamp, ++stamp);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s，版本变更为【%s】", Thread.currentThread().getName(), lisi.getName(), zhangsan.getName(), blOk2, atomicStampedReference.getStamp()));
        }, "线程3").start();

        new Thread(() -> {
            // 1.线程3和线程4同时取到了【张三】
            UserDto zhangsan = atomicStampedReference.getReference();
            // 版本号原子引用，增加了版本号机制
            int stamp = atomicStampedReference.getStamp();
            LogUtil.info(String.format("%s第一次取到【%s】，版本【%s】", Thread.currentThread().getName(), zhangsan.getName(), stamp));

            try {

                // 2.线程2休眠2秒，比线程1延迟执行修改逻辑
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 3.线程2后修改，虽然能正常修改，但是过程已经被线程1修改多次，也就是ABA问题
            UserDto lisi = new UserDto();
            lisi.setName("李四");
            lisi.setAge(11);

            boolean blOk1 = atomicStampedReference.compareAndSet(zhangsan, lisi, stamp, ++stamp);
            LogUtil.info(String.format("%s修改【%s】为【%s】：%s，期望版本【%s】，实际版本【%s】", Thread.currentThread().getName(), zhangsan.getName(), lisi.getName(), blOk1, stamp, atomicStampedReference.getStamp()));

        }, "线程4").start();
    }
}
