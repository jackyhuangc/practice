package com.jacky.concurrent;

import com.jacky.common.util.LogUtil;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/14 3:08 PM
 */
public class T_volatile {


    // 方法一：利用volatile 具有可见性，使得当变量发生修改时，立即触发【缓存一致性协议】时段CPU多级缓存失效，让其他使用该变量的线程重新从主内存中读取新值
    //private static volatile boolean SHOULD_EXIT = false;
    private static boolean NEED_EXIT = false;

    public static void main(String[] args) {

        Thread thread1 = new Thread(T_volatile::Run1);
        Thread thread2 = new Thread(T_volatile::Run2);
        thread1.start();

        // 延迟执行线程2
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread2.start();

    }

    private static void Run1() {

        while (!NEED_EXIT) {
            // 时间片分时操作系统(Linux等)分配给每个正在运行的进程微观上的一段CPU时间（在抢占内核中是：从进程开始运行直到被抢占的时间）
            // 在只考虑一个CPU的情况下，这些进程“看起来像”同时运行的，实则是轮番穿插地运行，由于时间片通常很短（在Linux上为5ms－800ms），用户不会感觉到
            // 死循环这种进程不会休眠，一有机会就占用cpu运行其时间片，所以就会是这种进程占用了大部分cpu时间
            // 当线程内没有休眠时间时，将一直占用时间片，无法进行线程上下文切换, 重新读取缓存资源（即SHOULD_EXIT变量的最新值）

            // 方法二：在while循环内，通常都需要加上1 ms左右的休眠。短暂交出CPU资源(缓存会被清除)，待下次获得时间片时(线程上下文切换）, 重新读取缓存资源
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

        LogUtil.error("线程1：此时volatile定义或经过了sleep休眠。发现标识已经修改...");
    }

    private static void Run2() {
        NEED_EXIT = true;
        LogUtil.error("线程2：标识已经修改...");
    }
}
