package com.jacky.practice;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-01-29 16:52
 * @since jdk1.8
 */
public class Godown {

    public static final int max_size = 100000;// 最大库存量
    private int curnum=100;// 当前库存量

    public int getCurnum()
    {
        return this.curnum;
    }

    public void setCurnum(int curnum){
        this.curnum=curnum;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("更新数据库数量。。。。。。。。。。。。。。。。。");
        // 可更新数据库
    }

    Godown(int curnum) {
        this.curnum = curnum;// 初始化库存
    }

    // 在Java语言中，每一个对象有一把锁。线程可以使用synchronized关键字来获取对象上的锁。synchronized关键字可应用在方法级别(粗粒度锁)或者是代码块级别(细粒度锁)。
    public synchronized void produce(int neednum) {

        // 判断能否继续生产
        while (neednum + curnum > max_size) {
            System.out.println(String.format("即将超过库存量，暂停生产！当前库存：%s", curnum));

            // 当前的生产线程等待，让出锁，否则该while循环会无休止进行，其他线程无法操作该Godown对象实例
            try {
                wait();
                System.out.println("xxxxxxxxxxxxxxxxxxxx");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // 继续生产
        //curnum += neednum;
        this.setCurnum(curnum+neednum);
        System.out.println(String.format(Thread.currentThread()+"再次生产%s个，当前库存：%s", neednum, curnum));

        // 唤醒在此对象监视器上等待的所有线程
        notifyAll();
    }

    public synchronized void consume(int neednum) {
        // 判断能否进行消费
        while (curnum < neednum) {
            System.out.println(String.format("库存不足，不能消费！当前库存：%s", curnum));
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //curnum -= neednum;
        this.setCurnum(curnum-neednum);
        System.out.println(String.format(Thread.currentThread()+"已经消费%s个，当前库存：%s", neednum, curnum));

        // 唤醒在此对象监视器上等待的所有线程
        notifyAll();
    }
}
