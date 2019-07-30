package com.jacky.practice;

public class SynchronizedDemo implements Runnable {
    private static int count = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new SynchronizedDemo());
            thread.start();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("result: " + count);
    }

    @Override
    public void run() {
        // 作用范围：类对象
        synchronized (SynchronizedDemo.class) {
            for (int i = 0; i < 1000000; i++)
                count++;
        }
//
//        // 作用范围：类对象实例
//        synchronized (this) {
//            for (int i = 0; i < 1000000; i++)
//                count++;
//        }
        //count1();
    }

    // 作用范围：类对象
    public static synchronized void count1() {
        for (int i = 0; i < 1000000; i++)
            count++;
    }

    // 作用范围：类对象实例
    public synchronized void count2() {
        for (int i = 0; i < 1000000; i++)
            count++;
    }
}