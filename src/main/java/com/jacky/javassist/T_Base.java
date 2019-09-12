package com.jacky.javassist;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.management.ManagementFactory;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/10 6:29 PM
 */

public class T_Base {
    public static void main(String[] args) {
        String name = ManagementFactory.getRuntimeMXBean().getName();

        String s = name.split("@")[0];
        //打印当前Pid
        System.out.println("pid:" + s);
        while (true) {
            try {
                Thread.sleep(5000L);
            } catch (Exception e) {
                break;
            }
            process();
        }
    }

    public static void process() {
        System.out.println("process");
    }
}
