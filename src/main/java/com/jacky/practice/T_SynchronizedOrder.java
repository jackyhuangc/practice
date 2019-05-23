package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.LogUtil;
import com.jacky.common.util.StringUtil;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CyclicBarrier;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/23 10:55
 * @since jdk1.8
 */

public class T_SynchronizedOrder {

    private static int MAX_THREAD = 100;
    // private static List<Order> listOrder = new CopyOnWriteArrayList<>();
    private static Set<Order> listOrder = new CopyOnWriteArraySet<>();
    private static Set<Order> listOrderNew = new CopyOnWriteArraySet<>();

    // 静态代码块
    static {
        for (int i = 0; i < 10000; i++) {
            listOrder.add(new Order(i, String.format("OR%04d", i), 0));
        }
    }

    static int ORDER_NUM = 0;

    static synchronized void CountUp() {

        ORDER_NUM++;
        LogUtil.info(String.format("计数器：%s", ORDER_NUM));
    }

    public static void main(String[] args) {

        System.out.println("这是一段测试代码");
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(MAX_THREAD + 1);
        // 写线程
        new Thread(() -> {
            for (int i = 10000; i < 10100; i++) {
                Order order = new Order(i, String.format("OR%04d", i), 0);

                if (listOrder.contains(order)) {
                    System.out.println(String.format("订单【%s】已存在！！！", order.getOrderNO()));
                } else {
                    listOrder.add(order);
                    System.out.println(String.format("订单【%s】已生成！！！", order.getOrderNO()));
                }
            }
        }
        );//.start();

        // 100个线程消费所有的订单
        for (int i = 0; i < MAX_THREAD; i++) {
            new Thread(() -> {
                System.out.println(String.format("正在启动线程【%s】", Thread.currentThread().getName()));

                // 多线程访问数组不安全，所以采用同步锁保证安全
                while (!listOrder.isEmpty()) {

                    Order order = null;
                    String token = UUID.randomUUID().toString();
                    for (Order item : listOrder) {
                        if (item.getLock(token)) {
                            order = item;
                            break;
                        }

                        // 方法锁将影响所有线程
//                        if (item.getLock2()) {
//                            order = item;
//                            break;
//                        }
                    }

                    // 模拟处理过程
                    if (order != null) {
                        try {
                            Thread.sleep(10);
                            String orderNo = order.getOrderNO();
                            if (order.getVersion() > 0) {
                                boolean ret = listOrder.stream().anyMatch(p -> p.getOrderNO().equals(orderNo));
                                LogUtil.warn("重复锁定！！！！*************数据是否在集合中：" + ret);
                            }

                            if (new Random().nextInt(10) == 9) {
                                throw new Exception("模拟人工异常，同时解锁");
                            }

                            // 完成一系列工作任务，并记录版本信息
                            order.setId(order.getId() * 100 + new Random(100).nextInt());
                            order.setVersion(order.getVersion() + 1);

                            listOrder.remove(order);
                            listOrderNew.add(order);
                            CountUp();
                            LogUtil.info(String.format("正在处理订单【%s】，当前线程【%s】,剩余订单【%s】", order.getOrderNO(),
                                    Thread.currentThread().getName(), listOrder.size()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 异常后解锁
                            order.unLock(token);
                        } finally {
                            // 完成后解锁
                            // 解锁的目的不就是要重复用吗？如果不需要重复用，就不应该解锁
                            //order.unLock(token);
                        }
                    }
                }

                System.out.println(String.format("线程结束【%s】", Thread.currentThread().getName()));
                // 执行完成后将计数器+1
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }

        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("所有线程都已执行完成，返回主线程！！！%s,%s,%s,%s", ORDER_NUM, listOrder.size(), listOrderNew.size(), listOrderNew.stream().filter(s -> s.getVersion() > 1).count()));

    }
}

class Order {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(String orderNO) {
        this.orderNO = orderNO;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    private int id;
    private String orderNO;
    private double price;
    private int version;// 记录下版本变更

    // volatile 在一个线程的工作内存中修改了该变量的值，该变量的值立即能回显到主内存中，从而保证所有的线程看到这个变量的值是一致的
    private volatile boolean lock = true;// 是否允许获取锁
    private String token = "";// 获取锁时的token唯一标识
    private String timestamp = "";// 记录下每次加解锁历史的时间戳

    Order(int id, String orderNO, double price) {
        this.id = id;
        this.orderNO = orderNO;
        this.price = price;
    }

    @Override
    public int hashCode() {
        // super.hashCode();
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        // super.equals(obj);
        return this.id == ((Order) obj).id;
    }

    // 同步锁-方法 修饰方法范围是整个函数 效率最低
    public synchronized boolean getLock1(String token) {
//        try {
//            // 方式锁将影响所有线程
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if (this.lock) {
            if (!StringUtil.isEmtpy(this.timestamp)) {
                LogUtil.warn("锁已被占用，重复占用！");
            }

            this.lock = false;
            this.token = token;
            this.timestamp = this.timestamp + "Lock" + DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss:SSSS");
            //LogUtil.info(String.format("获取锁耗时：%s", DateUtil.diffSeconds(date, DateUtil.now())));
            return true;
        } else {
            System.out.println("锁已被占用，第二次！！！");
        }

        return false;

    }

    // 同步锁-锁对象，效率低
    public boolean getLock2(String token) {
        Date date = DateUtil.now();

        synchronized (Order.class) {
            if (this.lock) {
                if (!StringUtil.isEmtpy(this.timestamp)) {
                    LogUtil.warn("锁已被占用，重复占用！");
                }

                this.lock = false;
                this.token = token;
                this.timestamp = this.timestamp + "Lock" + DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss:SSSS");
                LogUtil.info(String.format("获取锁耗时：%s", DateUtil.diffSeconds(date, DateUtil.now())));
                return true;
            } else {
                System.out.println("锁已被占用，第二次！！！");
            }

            return false;
        }

        // 如果锁不可用，不能直接返回this.lock，因为前面有句println，打印有毫秒级效率影响，此时lock可能已经解锁而变为可用状态，导致后面再出现解锁失败
        // return this.lock;
        //return false;
    }

    // 同步锁-代码块 双重检查锁，效率较高且安全
    public boolean getLock(String token) {
        Date date = DateUtil.now();
        // 先判断一次，降低不必要的锁导致的效率问题
        if (this.lock) {
            // 作用范围：类对象实例
            synchronized (this) {
                if (this.lock) {
                    if (!StringUtil.isEmtpy(this.timestamp)) {
                        LogUtil.warn("锁已被占用，重复占用！");
                    }

                    this.lock = false;
                    this.token = token;
                    this.timestamp = this.timestamp + "Lock" + DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss:SSSS");
                    LogUtil.info(String.format("获取锁耗时：%s", DateUtil.diffSeconds(date, DateUtil.now())));
                    return true;
                } else {
                    System.out.println("锁已被占用，第二次！！！");
                }
            }
        } else {
            //System.out.println("锁已被占用，第一次！！！");
            // 为什么很耗性能
        }

        // 如果锁不可用，不能直接返回this.lock，因为前面有句println，打印有毫秒级效率影响，此时lock可能已经解锁而变为可用状态，导致后面再出现解锁失败
        // return this.lock;
        return false;
    }

    public void unLock(String token) {
        if (!this.token.equals(token) || this.lock) {
            System.out.println(String.format("解锁失败，锁未被占用！%s,%s,%s", this.token, token, this.lock));
        } else {
            this.timestamp = this.timestamp + "UnLock" + DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss:SSSS");
            this.lock = true;
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

/*
 * 1.初始化： 单线程订单加载到并发集合ListOrder(CopyOnWriteArrayList)
 * 单线程仓位加载到并发集合ListPosition(CopyOnWriteArrayList)
 *
 * 2.订单处理 多线程并发处理订单信息ListOrder 处理过程中，需判断仓位Position是否满足交易条件(多买少卖) 启动数据库事务
 * 订单价格，成交数量，成交时间等更新 数据库中仓位更新(排它锁)，(优选乐观锁,通过时间戳版本字段) 提交数据库事务
 * 提交成功后，再更新内存中的仓位Position（有increase,decrease），需通过Synchronized,wait(),notify()
 * 进行仓位判断，若仓位不足，则需要等待
 *
 *
 */
