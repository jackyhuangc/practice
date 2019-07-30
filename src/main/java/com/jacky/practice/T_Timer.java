package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.LogUtil;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import sun.rmi.runtime.Log;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class T_Timer {

    public static void main(String[] args) {
        testTimer();

        //LogUtil.info("begin");
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            int counter = 0;
//
//            @Override
//            public void run() {
//                counter++;
//                LogUtil.info("调度开始1..." + Thread.currentThread().getName()
//                        + DateUtil.format(DateUtil.parseDate(scheduledExecutionTime()), "yyyy-MM-dd HH:mm:ss"));
//                try {
//                    if (counter == 5) {
//                        Thread.sleep(5000);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 3 * 1000, 1 * 1000);
//
//        LogUtil.info("end");
//
////        timer.schedule(new TimerTask() {
////            @Override
////            public void run() {
////                LogUtil.info("调度开始2..." + Thread.currentThread().getName());
////                try {
////                    Thread.sleep(2000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        }, 3 * 1000, 10 * 1000);
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // cancel方式终止线程
//        timer.cancel();
////        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
////                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
////        executorService.scheduleAtFixedRate(new Runnable() {
////            @Override
////            public void run() {
////                //do something
////            }
////        }, 3 * 1000, 3 * 1000, TimeUnit.SECONDS);


    }

    public static void testTimer() {
        final Timer timer = new Timer();
        // JDK 1.7
        AtomicInteger atomicInteger = new AtomicInteger();
        // JDK 1.8 如果是 JDK8，推 荐使用 LongAdder 对象，比 AtomicLong 性能更好(减少乐观锁的重试次数)。
        LongAdder longAdder = new LongAdder();
        //timer.scheduleAtFixedRate(new TimerTask() {   // 会压缩时间，始终保持步调一致，因此可能会导致瞬间的资源占用率升高
        timer.schedule(new TimerTask() {//分别注释这行和上面这行试一试效果    // 会同步调整计划，重新规划时间
            int count = 1;

            @Override
            public void run() {
                longAdder.add(1);
                if (longAdder.longValue() == 10) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        System.out.println("延迟5s");
                        e.printStackTrace();
                    }
                }
                SimpleDateFormat sf = new SimpleDateFormat(
                        "yyyy MM dd hh:mm:ss");
                System.out.println("当前时间："
                        + sf.format(System.currentTimeMillis()) + "计划时间："
                        + sf.format(scheduledExecutionTime()));

//                LogUtil.info("计划时间："
//                        + DateUtil.format(DateUtil.parseDate(scheduledExecutionTime()), "yyyy-MM-dd HH:mm:ss"));
            }
        }, 1000, 1000);
    }

    public static void testScheduledExcutorService() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(false).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogUtil.info("ScheduledExecutorService调度中1...");
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogUtil.info("ScheduledExecutorService调度中2...");
                int i = 0;

                // 用ScheduledExcutorService代替Timer，这样不同的任务之间未捕获的异常才不会互相影响或导致其他任务终止
                i = i / i;
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS);
    }
}
