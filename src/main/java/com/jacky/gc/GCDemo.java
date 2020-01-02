package com.jacky.gc;

import java.util.Random;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-30 10:56
 */
public class GCDemo {

    public static void main(String[] args) {
        System.out.println("**********GCDemo Hello");

        try {
            String str = "hello";
            while (true) {
                str += str + new Random().nextInt(777777777) + new Random().nextInt(888888888);
                str.intern();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("**********GCDemo Hello");
    }

    /**
     *
     * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:UseParallelGC
     */
}
