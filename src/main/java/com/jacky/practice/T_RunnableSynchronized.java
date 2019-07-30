package com.jacky.practice;

import java.io.IOException;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-01-29 16:10
 * @since jdk1.8
 */
public class T_RunnableSynchronized {

    public static Object lock = new Object();
    public static int Counter = 0;

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        // 同品种同客户的一个仓位（操作成功后，需回写数据库），通过Synchronized保证了每次只有一个线程操作实例对象方法代码块
        Godown godown = new Godown(30);

        // 卖单线程1
        SuperMarket s = new SuperMarket(60, godown);
        new Thread(s).start();

        // 下单线程1
        Customer c = new Customer(50, godown);
        new Thread(c).start();
//
//        for(int i=0;i<30;i++) {
//            // 下单线程2
//            Customer d = new Customer(50, godown);
//            new Thread(d).start();
//        }

//		try {
//			throw new Exception("xxx");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("IOException");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Exception");
//		}

        int result = getValue(2);

        System.out.println(result);
    }

    public static int getValue(int i) {
        int result = 0;

        switch (i) {

            case 1:
                result = result + i * 1;
            case 2:
                result = result + i * 2;
                // 没有break,代码将从此处开始继续往下执行
            case 3:
                result = result + i * 3;
        }

        return result;
    }
}
