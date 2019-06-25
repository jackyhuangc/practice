package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.LogUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.*;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/26 10:25
 * @since jdk1.8
 */
public class T_Redis {
    public static void main(String[] args) {
        testAdd2();

//        Jedis jedis = new Jedis("127.0.0.1", 6379);
//
//        System.out.println("Connected to redis!!!");
//        System.out.println(jedis.get("name"));
//        String a = jedis.set("name", "test");
//        String b = jedis.get("name");
//
//        System.out.println("name值：" + b);
    }

    public static void testAdd2() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(0);
        config.setMaxWaitMillis(-1);
        config.setTestOnBorrow(true);

        // Jedis不是线程安全的，故不应该在多线程环境中共用一个Jedis实例。但是，也应该避免直接创建多个Jedis实例，因为这种做法会导致创建过多的socket连接，性能不高。
        //要保证线程安全且获得较好的性能，可以使用JedisPool。JedisPool是一个连接池，既可以保证线程安全，又可以保证了较高的效率
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 6379, 30000, "123456");

        Jedis jedis = jedisPool.getResource();

        jedis.sadd("1", "1-1");
        jedis.sadd("1", "1-2");
        jedis.sadd("1", "1-3");
        jedis.sadd("1", "1-4");
        jedis.sadd("1", "1-5");

        // sinter 用于保存条件，比如分类
        Set<String> set = jedis.sinter("1");

        Map<String, String> map = new HashMap<>();
        map.put("2-1", "fdsfdsaf");
        map.put("2-1", "xxxxxxxx");
        map.put("2-2", "xxxxxxxx");

        // hmset 用于存储数据源(基本上不变的值，比如合约基础数据)
        // hmset 会覆盖掉原值
        String ret = jedis.hmset("2", map);

        // hget必须要有key是才能用，否则报错
        String str = jedis.hget("2", "2-1");
        List<String> list=jedis.hmget("2","2-1");
        //jedis.hget(SYS_USER_TABLE, key);
    }

    public static void testAdd() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(0);
        config.setMaxWaitMillis(-1);
        config.setTestOnBorrow(true);

        // Jedis不是线程安全的，故不应该在多线程环境中共用一个Jedis实例。但是，也应该避免直接创建多个Jedis实例，因为这种做法会导致创建过多的socket连接，性能不高。
        //要保证线程安全且获得较好的性能，可以使用JedisPool。JedisPool是一个连接池，既可以保证线程安全，又可以保证了较高的效率
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 6379);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Date date = DateUtil.now();
        new Thread(() -> {
            Jedis jedis = jedisPool.getResource();
            for (int i = 0; i < 10000; i++) {

                //Order order = new Order(i, String.format("OR%04d", i), 0);
                String ret = jedis.set(String.format("OR%04d", i), String.format("OR%04d", i), SetParams.setParams().nx());
                //Long ret2 = jedis.del(String.format("OR%04d", i));

                LogUtil.info(ret);
//                if (listOrder.contains(order)) {
//                    System.out.println(String.format("订单【%s】已存在！！！", order.getOrderNO()));
//                } else {
//                    listOrder.add(order);
//                    System.out.println(String.format("订单【%s】已生成！！！", order.getOrderNO()));
//                }
            }
            countDownLatch.countDown();
            jedis.close();
        }
        ).start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jedisPool.close();
        LogUtil.info(String.format("总耗时：%s", DateUtil.diffSeconds(date, DateUtil.now())));
    }
}
