package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.JsonUtil;
import com.jacky.common.util.LogUtil;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.Data;
import redis.clients.jedis.*;
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
        //testAdd2();
        testAdd3();
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

        // 常用字符串键值对操作
//        jedis.set("key","value");
//        jedis.setnx("key","value");

        // List类型是按照插入顺序排序的字符串链表,可排序
        //jedis.lpush("key","value1");

        long xxx = jedis.srem("xxx", "x");
        int get = jedis.getDB();
        long count = jedis.dbSize();
        //String ret = jedis.flushDB();
        // 切换数据库 ，redis默认有16个数据库(0-15)，可配置databases = 16 //默认有16个数据库
//        jedis.select(1);
        get = jedis.getDB();
        count = jedis.dbSize();

        // sadd 我们可以将Set类型看作为没有排序的字符集合
        jedis.sadd("1", "1-1");
        jedis.sadd("1", "1-2");
        jedis.sadd("1", "1-3");
        jedis.sadd("1", "1-4");
        jedis.sadd("1", "1-5");

        // sinter 用于保存条件，比如分类
        Set<String> set = jedis.smembers("1");
        long size = jedis.scard("1");

        // Zsort 相比sadd,增加分数，可排序，常用与游戏排名、微博热点话题等使用场景。
        jedis.zadd("zkey", 1, "");

        // 哈希表的方式适合于存储静态数据，如用户信息等，可以只更新某个字段(按filed覆盖)
        Map<String, String> map2 = jedis.hgetAll("3");
        List<String> list2 = jedis.hmget("2", "3");
        List<String> list3 = jedis.hmget("3", "3");
        String list4 = jedis.hget("3", "3");

        boolean exist = jedis.hexists("key", "xxx");
        Map<String, String> map = new HashMap<>();
        map.put("2-1", "fdsfdsaf");
        map.put("2-1", "xxxxxxxx");

        // hmset 用于存储数据源(基本上不变的值，比如合约基础数据)
        // hmset 会覆盖掉原值
        String retx = jedis.hmset("2", map);

        map.put("2-1", "ddddddd");
        String ret = jedis.hmset("2", map);

        // hget必须要有key是才能用，否则报错
        String str = jedis.hget("2", "2-1");
        List<String> list = jedis.hmget("2", "2-1");
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

    public static void testAdd3() {
        //                String[] ids = new String[]{
//                        "", "", "", "", "",
//                        "", "", "", "", "",
//                        "", "", "", "", ""};


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

        long ret = 0;// jedis.del("sz000060&2019-06-26");

        Instrument instrument = new Instrument();
        instrument.setInstrumentID("sz300748");
        instrument.setOffDate(DateUtil.addDays(DateUtil.today(), 30));
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000795");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600366");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000060");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600704");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000959");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600111");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600549");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600259");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600058");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600259");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000758");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sh600111");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000969");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));
        instrument.setInstrumentID("sz000970");
        jedis.sadd("stock&instrument", JsonUtil.toJson(instrument));

        List<String> listValues = new ArrayList<>();

        ScanParams scanParams = new ScanParams();
        scanParams.count(100);
        //scanParams.match("xxxx*"); 模糊匹配
        ret = 0;
        String cursor = "0";
        while (true) {
            ScanResult<String> scanResult = jedis.sscan("stock&instrument", cursor, scanParams);
            List<String> result = scanResult.getResult();
            cursor = scanResult.getCursor();

            for (String member : result) {
                listValues.add(member);
                //ret = jedis.srem("stock&instrument", member);
            }

            if (result.size() < 100) {
                break;
            }
        }
    }
}


@Data
class Instrument {

    private String instrumentID;
    private String instrumentName;
    private Date onDate;
    private Date offDate;
}
