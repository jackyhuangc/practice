package com.jacky.practice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jacky.common.util.JsonUtil;
import com.jacky.common.util.LogUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.regex.Pattern;

/***
 *
 * 重点1：volatile,synchronized关键字及Atomic对象是为了解决【多线程高并发场景下，线程安全或资源共享】的问题
 * 重点2：ThreadLocal是存储线程本地变量的一个类似Map的容器，它为变量在每个线程中都存储了一个本地的【副本】，这些副本是线程隔离的，因此也就不存在多线程的同步问题，虽然占用了内存，但是确实解决一些不需要在多个线程之间进行共享 的变量的存储问题。
 * 关于Volatile关键字具有可见性，但不具有操作的原子性，而synchronized比volatile对资源的消耗稍微大点，但可以保证变量操作的原子性，保证变量的一致性，最佳实践则是二者结合一起使用。
 *
 * 1、对于synchronized的出现，是解决多线程资源共享的问题，同步机制采用了“以时间换空间”的方式：访问串行化，对象共享化。同步机制是提供一份变量，让所有线程都可以访问。
 *
 * 2、对于Atomic的出现，是通过原子操作指令+Lock-Free完成，从而实现非阻塞式的并发问题。
 *
 * 3、对于Volatile，为多线程资源共享问题解决了部分需求，在非依赖自身的操作的情况下，对变量的改变将对任何线程可见。
 *
 * 4、对于ThreadLocal的出现，并不是解决多线程资源共享的问题，而是用来提供线程内的局部变量，省去参数传递这个不必要的麻烦，ThreadLocal采用了“以空间换时间”的方式：访问并行化，对象独享化。ThreadLocal是为每一个线程都提供了一份独有的变量，各个线程互不影响。
 * ---------------------
 * 作者：Sunzxyong
 * 来源：CSDN
 * 原文：https://blog.csdn.net/u010687392/article/details/50549236
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public class T_ThreadLocal {

    // ThreadLocal的设计，并不是解决资源共享的问题，而是用来提供线程内的局部变量，这样每个线程都自己管理自己的局部变量，别的线程操作的数据不会对我产生影响，互不影响
    // ThreadLocal变量属于线程内部管理的，互不影响
    // 这样可以防止内存泄露，当然还有设为static
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    Pattern pattern = Pattern.compile("规则");

    static class MyRunnable implements Runnable {
        int num;

        MyRunnable(int num) {
            this.num = num;
        }

        @Override
        public void run() {

            // TODO 2019-05-01 13:00  此处实现业务逻辑 by Jacky
            threadLocal.set(String.valueOf(this.num));
            LogUtil.info(String.format("%s,%s", Thread.currentThread().getName(), threadLocal.get()));
        }
    }

    public static void main(String[] args) {

        Test t1 = new Test();
        t1.setA("a1");
        t1.setB("b1");

        String t2 = JsonUtil.toJson(t1);
        //t2="{\"a\":\"a1\",\"b\":\"b1\"}";
        t2 = "{\"a\":\"a1\",\"b\":\"b1\",\"c\":\"c1\"}";
        Test t3 = JsonUtil.fromJson(t2, new TypeReference<Test>() {
        });

        //new Thread(new MyRunnable(3)).start();
        new Thread(new MyRunnable(1)).start();
        new Thread(new MyRunnable(2)).start();
        new Thread(new MyRunnable(3)).start();
    }
}

@Data
class Test implements Serializable {
    private static final long serialVersionUID = 5012356111203204446L;
    private String a;
    private String b;
}
