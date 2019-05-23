package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.LogUtil;
import com.jacky.common.util.ThreadPoolUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/23 10:55
 * @since jdk1.8
 */


public class T_RedisDistributedLock {

    private static int MAX_THREAD = 20;
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
        Date now = DateUtil.now();

        // CountDownLatch强调一个线程等多个线程完成某件事情（只需要关心实际的任务数量）。CyclicBarrier是多个线程互等，等大家都完成（需要关心能执行的线程数量）。
        // CyclicBarrier cyclicBarrier = new CyclicBarrier(MAX_THREAD + 1);
        CountDownLatch countDownLatch = new CountDownLatch(MAX_THREAD);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(0);
        config.setMaxWaitMillis(-1);
        config.setTestOnBorrow(true);

        // Jedis不是线程安全的，故不应该在多线程环境中共用一个Jedis实例。但是，也应该避免直接创建多个Jedis实例，因为这种做法会导致创建过多的socket连接，性能不高。
        //要保证线程安全且获得较好的性能，可以使用JedisPool。JedisPool是一个连接池，既可以保证线程安全，又可以保证了较高的效率
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 6379,30000,"123456");

        // 读线程
        for (int i = 0; i < MAX_THREAD; i++) {

            // 1.即便有MAX_THREAD个任务，但实际并行的线程数量可能很少
            ThreadPoolUtil.execute((j, c) -> {
                System.out.println(String.format("正在启动线程【%s】", Thread.currentThread().getName()));

                // 2.把redis资源的具体使用放到线程内实现，避免浪费资源，因为实际在同时运行的线程较少
                int max1 = j.getNumActive();
                int max2 = j.getNumWaiters();
                int max3 = j.getNumIdle();

                // 等待获取redis连接是会阻塞
                Jedis jedis = j.getResource();
                while (!listOrder.isEmpty()) {

                    Order order = null;
                    String token = UUID.randomUUID().toString();
                    for (Order item : listOrder) {

                        if (tryGetDistributedLock(jedis, item.getOrderNO(), token, 50000)) {
                            order = item;
                            break;
                        }
                    }

                    // 模拟处理过程
                    if (order != null) {
                        try {
                            System.out.println(String.format("正在处理订单【%s】，当前线程【%s】,剩余订单【%s】,%s,%s,%s", order.getOrderNO(),
                                    Thread.currentThread().getName(), listOrder.size(), jedisPool.getNumActive(), jedisPool.getNumWaiters(), jedisPool.getNumIdle()));
                            Thread.sleep(10);

//                            if (new Random().nextInt(10) == 9) {
//                                throw new Exception("模拟人工异常，解锁");
//                            }

                            // 完成一系列工作任务，并记录版本信息
                            // FIXME TODO
                            order.setVersion(order.getVersion() + 1);

                            listOrder.remove(order);
                            listOrderNew.add(order);
                            CountUp();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 异常马上解锁
                            tryReleaseDistributedLock(jedis, order.getOrderNO(), token);
                        } finally {

                            // 完成后解锁
                            // 解锁的目的不就是要重复用吗？如果不需要重复用，就不应该解锁
                            //tryReleaseDistributedLock(jedis, order.getOrderNO(), token);
                        }
                    }
                }

                System.out.println(String.format("线程结束【%s】", Thread.currentThread().getName()));
                // 执行完成后将计数器+1
                c.countDown();
                jedis.close();
            }, jedisPool, countDownLatch);
        }

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
        ).start();

        // 等待所有线程完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("所有线程都已执行完成，剩余redis连接池！！！活动：%s，等待：%s，空闲：%s", jedisPool.getNumActive(), jedisPool.getNumWaiters(), jedisPool.getNumIdle()));
        System.out.println(String.format("所有线程都已执行完成，返回主线程！！！总耗时：%s，计数器：%s，剩余：%s，执行：%s，重复执行：%s", DateUtil.diffSeconds(now, DateUtil.now()), ORDER_NUM, listOrder.size(), listOrderNew.size(), listOrderNew.stream().filter(s -> s.getVersion() > 1).count()));

        // 若有需要关闭连接池，待所有线程处理完后再关闭；若是全局线程池，则不用关闭，避免浪费资源
        jedisPool.close();
        ThreadPoolUtil.shutdown();
    }

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /*
    为了确保分布式锁可用，我们至少要确保锁的实现同时满足以下四个条件：

    1.互斥性。在任意时刻，只有一个客户端能持有锁。 nx 没有锁才加锁
    2.不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。  expireTime过期自动解锁
    3.具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
    4.解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了  requestId标识
     */

    /**
     * 获取分布式锁
     *
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 过期时间(毫秒)
     * @return 是否成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        Date date = DateUtil.now();
        String result = jedis.set(lockKey, requestId, SetParams.setParams().nx().px(expireTime));
        if (LOCK_SUCCESS.equals(result)) {
            LogUtil.info(String.format("获取锁耗时：%s", DateUtil.diffSeconds(date, DateUtil.now())));
            return true;
        }
        return false;
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否成功
     */
    public static boolean tryReleaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        // 确保解锁操作原子性，就是在eval命令执行Lua代码的时候，Lua代码将被当成一个命令去执行，并且直到eval命令执行完成，Redis才会执行其他命令
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }

        return false;
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
 */
