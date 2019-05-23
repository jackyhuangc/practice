package com.jacky.practice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/17 16:42
 * @since jdk1.8
 */
public class T_Excutor implements Callable<String> {

    private String str;

    public T_Excutor(String str) {
        this.str = str;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        return str;
    }

    static void Test_Future() {
        Callable<String> callable = new T_Excutor("XXXXX");

        // 方式一：通过Thread+FutureTask实现异步任务，计算异步结果
        FutureTask<String> task = new FutureTask<>(callable);
        new Thread(task).start();

        // 方式二：通过线程池+Future实现异步任务，计算异步结果
        //ExecutorService es = Executors.newSingleThreadExecutor();
        //Future<String> task = es.submit(callable);// submit有返回值（支持callable接口）；便于通过get()方法捕获处理内部异常
        //es.shutdown();// 通知执行成功后顺序关闭提交的任务
        ////es.shutdownNow();// 立即关闭/中断所有任务，返回待执行队列

        // 获取结果时，会造成主线程阻塞
        //String result=task.get();
        //String result = future.get();
        //System.out.println(result);
    }

    static void Test_ThreadPool() {
        // 模拟newCachedThreadPool,但最多只能创建3个容量的线程池，因为SynchronousQueue同步队列，实质容量是0，但是由于该Queue本身的特性，在某次添加元素后必须等待其他线程取走后才能继续添加
        // SynchronousQueue的javadoc文档提到A synchronous queue does not have any internal capacity, not even a capacity of one。也就说同步队列的容量是0，不会缓存数据。
    /*    ExecutorService pool = new ThreadPoolExecutor(
                2, 3, 1, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());*/

        // 模拟newFixedThreadPool
        /*  */      ExecutorService pool = new ThreadPoolExecutor(3, 3,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        pool.execute(new T_Runnable());
        pool.execute(new T_Runnable());
        pool.execute(new T_Runnable());

        //Thread.sleep(15000);
        pool.execute(new T_Runnable());
        pool.execute(new T_Runnable());
    }

    static void Test_Callable() {
        ExecutorService e = Executors.newCachedThreadPool();//.newFixedThreadPool(5);
        List<Future<String>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(e.submit(new T_Callable()));
        }
        e.shutdown();

        for (Future<String> f : list) {
            try {
                System.out.println(String.format("执行结果：%s", f.get()));
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(String.format("isShutdown:%s,isTerminated:%s", e.isShutdown(), e.isTerminated()));

                // 立即关闭/中断所有任务，返回待执行队列
                List<Runnable> listResult = e.shutdownNow();
                System.out.println("待执行的队列数量：" + listResult.size());
                //break;
            }
        }
    }

    static void Test_Scheduled() {

        ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(2);
        System.out.println("****************************newFixedThreadPool*******************************");
        for (int i = 0; i < 4; i++) {
            final int index = i;
            // 延迟三秒执行
            //newScheduledThreadPool.schedule(new T_Callable(), 3, TimeUnit.SECONDS);
            // 周期循环执行
            newScheduledThreadPool.scheduleAtFixedRate(new T_Runnable(), 0, 5, TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // Test_Future();

        // Test_Callable();

        Test_ThreadPool();

        //Test_Scheduled();

        System.out.println("OK");
    }
}

class T_Runnable implements Runnable {
    @Override
    public void run() {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(String.format("Runnable开始执行：%s【%s】", Thread.currentThread().getName(), s.format(new Date())));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Runnable结束执行：%s【%s】", Thread.currentThread().getName(), s.format(new Date())));
    }
}

class T_Callable implements Callable<String> {

    @Override
    public String call() throws Exception {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(String.format("开始执行：%s【%s】", Thread.currentThread().getName(), s.format(new Date())));
        if (new Random().nextBoolean())
            throw new Exception("Meet error in task." + Thread.currentThread().getName());

        Thread.sleep(10000);
        System.out.println(String.format("结束执行：%s【%s】", Thread.currentThread().getName(), s.format(new Date())));
        return String.format("%s【%s】", Thread.currentThread().getName(), s.format(new Date()));
    }
}
