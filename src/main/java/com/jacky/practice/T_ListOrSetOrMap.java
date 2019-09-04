package com.jacky.practice;


import com.jacky.common.util.JsonUtil;

import java.util.*;


import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class T_ListOrSetOrMap {

    public static void main(String[] args) {

        testListSetMap();

        testList();
        testSet();
        testMap();

        /***
         首先搞清楚有序、无序及排序性质的差别。
         集合的有序、无序是指插入元素时，保持插入的顺序性，也就是先插入的元素优先放入集合的前面部分。
         而排序是指插入元素后，集合中的元素是否自动排序。（例如升序排序）

         HashMap、 HashSet、 HashTable 等 基于哈希存储方式的集合是无序的。其它的集合都是有序的。
         可以看到LinkedHashMap.Entry 继承自HashMap.Node 除了Node 本身有的几个属性外，额外增加了before after
         用于指向前一个Entry 后一个Entry。也就是说，元素之间维持着一条总的链表数据结构。
         正式因为这个链表才保证了LinkedHashMap的有序性。

         LindedList(基于链表) ，ArrayList(基于动态数组)都是有序的。
         而TreeMap(SortedMap接口) TreeSet(SortedSet接口) 等集合是排序的，即实现了排序接口。
         ***/
        testOrderAndSort();

        //主要根据集合的特点来选用，
        // 1.比如我们需要根据键值获取到元素值时就选用Map接口下的集合，需要排序时选择TreeMap,不需要排序时就选择HashMap,需要保证线程安全就选用ConcurrentHashMap.
        // 2.当我们只需要存放元素值时，就选择实现Collection接口的集合，需要保证元素唯一时选择实现Set接口的集合比如TreeSet或HashSet，不需要就选择实现List接口的比如ArrayList或LinkedList，然后再根据实现这些接口的集合的特点来选用。
        // 3.需要排序是再选用Tree相关的Set或Map
        // 4.有大量需要增删改的数据，可用LinkedSet或LinkedList

        // 最常用 List ArrayList 单线程中 或 Collections.synchronizedList(new ArrayList<>()) 多线程中
        // 最常用 Set HashSet/TreeSet 单线程中(可排序) 或多线程 Collections.synchronizedSet(new TreeSet)或 CopyOnWriteArraySet
        // 最常用 Map HashMap 单线程 或 ConcurrentHashMap多线程

    }

    // 这是基础，List、Set、Map
    private static void testListSetMap() {
        // 1.List接口存储一组不唯一（可以有多个元素引用相同的对象），有序的对象
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("1");

        // 2.不允许重复的集合。不会有多个元素引用相同的对象。
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("1");// 不允许重复，CopyOnWriteArraySet是一个集合，所以它是不可以放置重复的元素的，它的取重逻辑是在add中体现的

        // 3.使用键值对存储。Map会维护与Key有关联的值。两个Key可以引用相同的对象，但Key不能重复，典型的Key是String类型，但也可以是任何对象。

        // 继承于AbstractMap<K,V>和Map<K,V>且未使用synchronized，不安全但效率高，key可为空(此时key的hash为0)
        Map<String, String> map = new HashMap<>();

        map.put("test1", "test11");
        map.put("test1", "test12");
        map.put("test2", "test22");
        map.put(null, "test232");
        map.put(null, "test222");
        map.put(null, "test212");

        // 继承于Dictionary<K,V>和Map<K,V>且使用synchronized，安全但效率低，key为空将报空指针异常
        // Hashtable<String, String> hashtable = new Hashtable<>();
        // hashtable.put("", "");

        list.forEach((s) -> {
            System.out.println(s);
        });

        set.forEach((s) -> {
            System.out.println(s);
        });

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    // 这是List实现类
    private static void testList() {


        // Vector类的所有方法都是同步的。可以由两个线程安全地访问一个Vector对象、但是一个线程访问Vector，
        // 代码要在同步操作上耗费大量的时间。Arraylist不是同步的，所以在不需要同步时建议使用Arraylist。

        // 继承自AbstractList<E>和实现List<E>且未使用synchronized，不安全但效率高，ArrayList实现了RandomAccess接口，采用随机访问更快
        List<String> list1 = new ArrayList<>();
        list1.add("1");

        // 继承自AbstractList<E>和实现List<E>且使用synchronized(方法上)，安全但效率低。可以由两个线程安全地访问一个Vector对象、
        // 但是一个线程访问Vector，代码要在同步操作上耗费大量的时间，Vector实现了RandomAccess接口，采用随机访问更快
        List<String> list2 = new Vector<>();
        list2.add("1");

        // 继承自AbstractSequentialList<E>和实现List<E>、Deque<E>且未使用synchronized，主要用于大量数据的情况下频繁的插入、删除等修改操作，很少用到
        // Arraylist底层使用的是数组（存读数据效率高，插入删除特定位置效率低，PS:主要是要扩容，效率慢），LinkedList底层使用的是双向循环链表数据结构（插入，删除效率特别高）。
        // 学过数据结构这门课后我们就知道采用链表存储，插入，删除元素时间复杂度不受元素位置的影响，都是近似O（1）而数组为近似O（n），因此当数据特别多，
        // 而且经常需要插入删除元素时建议选用LinkedList.一般程序只用Arraylist就够用了，因为一般数据量都不会蛮大，Arraylist是使用最多的集合类。
        // ArrayList未实现了RandomAccess接口，采用迭代访问更慢

        List<String> list3 = new LinkedList<>();

        // ArraryList和LinkedList非线程安全，可用Collections.synchronizedList()包装实现线程安全，主要是所有get,add,remove等操作都加上了synchronized代码块，保证安全
        list3 = Collections.synchronizedList(new LinkedList<>());
        list3.add(0, "1");
    }

    // 这是Set实现类
    private static void testSet() {
        // - HashSet（无序，唯一）:哈希表或者叫散列集(hash table)，继承AbstractSet<E>和实现Set<E>,未使用synchronized，不安全
        Set<String> set1 = new HashSet<>(); // 无序

        // - LinkedHashSet：链表和哈希表组成 。 由链表保证元素的排序，由哈希表保证元素的唯一性，继承HashSet<E>和实现Set<E>,未使用synchronized，不安全
        Set<String> set2 = new LinkedHashSet<>(); // 有序

        // - TreeSet（有序，唯一）：红黑树(自平衡的排序二叉树。)，继承AbstractSet<E>和实现NavigableSet<E>（SortedSet<E>）,未使用synchronized，不安全
        Set<String> set3 = new TreeSet<>(); // 自然排序
        set3.add("1");
        set3.add("1");// 不会重复添加

        // 如果要保证线程安全可以使用，CopyOnWriteArraySet，CopyOnWriteArraySet“线程安全”机制，和CopyOnWriteArrayList一样，是通过volatile和互斥锁来实现的
        Set<String> set4 = new CopyOnWriteArraySet(); // 有序
        set4.add("1");
    }

    // 这是Map实现类
    private static void testMap() {
        // HashMap：基于哈希表的Map接口实现（哈希表对键进行散列，Map结构即映射表存放键值对）
        // 继承自AbstractMap<K,V>和实现Map<K,V>且未使用synchronized，不安全但效率高，key可为空(此时key的hash为0)
        Map<String, String> map1 = new HashMap<>();

        map1.put("test1", "test11");
        map1.put("test1", "test12");
        map1.put("test2", "test22");
        map1.put(null, "test232");
        map1.put(null, "test222");
        map1.put(null, "test212");

        for (String key : map1.keySet()) {
            System.out.println(map1.get(key));
        }

        System.out.println("********1********");
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("********2********");

        // LinkedHashMap:HashMap  的基础上加上了链表数据结构，继承自HashMap<K,V>和实现Map<K,V>且未使用synchronized，不安全但效率高，但有序
        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("test1", "test11");
        map2.put("test1", "test12");
        map2.put("test2", "test22");

        // HashTable:哈希表 继承自Dictionary<K,V>和实现Map<K,V>且使用synchronized，安全但效率低，key为空将报空指针异常
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("test1", "test11");
        //hashtable.put(null, "test11");// 报错

        System.out.println("****************");
        // TreeMap:红黑树（自平衡的排序二叉树）
        Map<String, String> map3 = new TreeMap<>();
        map3.put("test1", "test11");
        map3.put("test1", "test12");
        map3.put("test2", "test22");
        map3 = ((TreeMap<String, String>) map3).descendingMap();
        for (Map.Entry<String, String> entry : map3.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        // 若要保证线程安全可用此实现类 ConcurrentHashMap, Collections.synchronizedMap()
    }

    private static void testOrderAndSort() {

        // 有序
        List<String> arrayList = new ArrayList<String>();
        arrayList.add("s1");
        arrayList.add("s3");
        arrayList.add("s4");
        arrayList.add("s5");
        arrayList.add("s2");
        System.out.println("ArrayList:==========================");
        for (String arrList : arrayList) {
            System.out.println(arrList);
        }

        // 有序
        List<String> linkedList = new LinkedList<String>();
        linkedList.add("s1");
        linkedList.add("s3");
        linkedList.add("s4");
        linkedList.add("s5");
        linkedList.add("s2");
        System.out.println("LinkedList:===========================");
        for (String linkList : linkedList) {
            System.out.println(linkList);
        }

        // 无序
        Set<String> hashSet = new HashSet<String>();
        hashSet.add("s1");
        hashSet.add("s3");
        hashSet.add("s4");
        hashSet.add("s5");
        hashSet.add("s2");
        System.out.println("HashSet:==============================");
        for (String hashst : hashSet) {
            System.out.println(hashst);
        }

        // 有序
        Set<String> linkedHashSet = new LinkedHashSet<String>();
        linkedHashSet.add("s1");
        linkedHashSet.add("s3");
        linkedHashSet.add("s4");
        linkedHashSet.add("s5");
        linkedHashSet.add("s2");
        System.out.println("LinkedHashSet:=========================");
        for (String linkedst : linkedHashSet) {
            System.out.println(linkedst);
        }

        // 自然排序
        Set<String> treeSet = new TreeSet<String>();
        treeSet.add("s1");
        treeSet.add("s3");
        treeSet.add("s4");
        treeSet.add("s5");
        treeSet.add("s2");
        System.out.println("TreeSet:==============================");
        for (String treest : treeSet) {
            System.out.println(treest);
        }

        // 倒序再排一次
        treeSet = ((TreeSet<String>) treeSet).descendingSet();
        for (String treest : treeSet) {
            System.out.println(treest);
        }
    }

    @Test
    public void test1() {

//        List<String> primarySources = new ArrayList<>();
//        primarySources.add("1");
//        primarySources.add("2");
//        primarySources.add("3");
//        primarySources.add("4");
//        primarySources.add("1");
//        primarySources.add("2");

        String[] primarySources = new String[]{"1", "2", "1"};

        Set<String> list = new LinkedHashSet<>(Arrays.asList(primarySources));
    }

    @Test
    public void test2() {
        List<String> list = Collections.EMPTY_LIST;
        list = Arrays.asList("awg", "weg", "wweg", "wegwe");
        list.forEach(System.out::println);
        list.forEach(str -> {
            if ("awg".equals(str)) {
                return;
            }
            System.out.println(str);
        });
    }

    /**
     * T orElse(T other)
     * 产生这个Optional的值，或者在该Optional为空时，产生other。
     * <p>
     * T orElseGet(Supplier<? extends T>other)
     * 产生这个Optional的值，或者在该Optional为空时，产生调用other的结果。
     * ————————————————
     * 版权声明：本文为CSDN博主「孑辞」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/qq_40448026/article/details/80275652
     */
    @Test
    public void test3() {
        List<Integer> list = Arrays.asList();
        //通过reduce方法得到一个Optional类
        Integer a = list.stream().findFirst().orElse(null);

        // orElseGet 产生调用other的结果，不能null(因为类型是Supplier，有方法调用)，否则会报错
        // Integer b = list.stream().findFirst().orElseGet(null);
        Integer b = list.stream().findFirst().orElseGet(() -> {
            return null;
        });

        /**
         * Predicate：用于测试是否符合条件。
         * Function：接受一个参数，返回一个参数。接收的是参数，关键看怎么用，小写传参，大写调用
         * Consumer：接受一个参数，不返回参数。
         * Supplier：无输入参数，返回一个结果。
         */

        System.out.println("a  " + a);
        System.out.println("b  " + b);

        list = Arrays.asList(10, 20, 30);
        a = list.stream().findFirst().orElse(get("a"));
        b = list.stream().findFirst().orElseGet(() -> get("b"));
        System.out.println("a  " + a);
        System.out.println("b  " + b);

        Consumer<String> s = (s1) -> {

        };

        s.accept("s1");

        Supplier<String> s2 = () -> {
            return "s2";
        };

        Function<String, String> f = (f1) -> {
            return f1;
        };

        f.apply("f1");

        Function<String, Integer> f1 = Integer::new;

        f1.apply("1");

        String result = s2.get();

        TestClass testClass = new TestClass();

        // 小写调用是将accept中的实参当做调用方法的入参传进入，Consumer的泛型要与调用方法的入参保持一致，否则会报错
        Consumer<String> t1 = testClass::aaa;
        t1.accept("abc");

        // 大写调用是将accept中的实参去调用冒号后的方法，是调用，Consumer的泛型要与类保持一致
        Consumer<TestClass> t2 = TestClass::aaa1;
        t2.accept(testClass);
        t2.accept(new TestClass());

        Function<TestClass, String> t3 = TestClass::bbb1;
        t3.apply(testClass);

        Function<String, String> t4 = testClass::bbb;
        t4.apply("xxx");

        Supplier<Boolean> t5 = testClass::ccc;
        t5.get();

        Supplier<TestClass> t6 = TestClass::new;
        t6.get();
    }

    @Test
    public void test5() {
        String str = "{\n" +
                "  \"fromSystem\" : \"BIZ\",\n" +
                "  \"key\" : \"TEST12345678\",\n" +
                "  \"type\" : \"AcctRegister\",\n" +
                "  \"data\" : {\n" +
                "    \"reback_URL\" : \"http://124.133.252.132:7066/gateway/sccba/orgRegister?AuthTokenID=519291cb961ee7fdc8438d419bd52016\",\n" +
                "    \"company_name\" : \"公司名称\",\n" +
                "    \"company_id_type\" : \"01\",\n" +
                "    \"company_id_num\" : \"公司证件号码\",\n" +
                "    \"legal\" : \"法人\",\n" +
                "    \"legal_id_type\" : \"01\",\n" +
                "    \"legal_id_num\" : \"340803199608152312\",\n" +
                "    \"operator\" : \"代办人\",\n" +
                "    \"operator_id_type\" : \"01\",\n" +
                "    \"operator_id_num\" : \"340803199608152312\",\n" +
                "    \"operator_tel\" : \"13512345678\",\n" +
                "    \"operator_email\" : \"g_cdbiz@kuainiugroup.com\",\n" +
                "    \"acct_type\" : \"01\",\n" +
                "    \"acct_num\" : 12345678\"\n" +
                "  }\n" +
                "}";


        String str1 = "{\n" +
                "  \"from_system\": \"BIZ\",\n" +
                "  \"key\" : \"TEST123456\",\n" +
                "  \"type\": \"CardBind\",\n" +
                "  \"data\": {\n" +
                "    \"reback_URL\": \"http://test.deposit.kuainiu.io/account/do-bind-card?id=2&reback=1\",\n" +
                "    \"sub_acct_no\": \"81503410000000000025413\",\n" +
                "    \"acct_id\": 2\n" +
                "  }\n" +
                "}";

        String str2 = "{\n" +
                "  \"from_system\": \"BIZ\",\n" +
                "  \"key\" : \"TEST123456\",\n" +
                "  \"type\": \"CardUnbind\",\n" +
                "  \"data\": {\n" +
                "    \"reback_URL\": \"http://test.deposit.kuainiu.io/account/do-bind-card?id=2&reback=1\",\n" +
                "    \"sub_acct_no\": \"815034100000000000254131\",\n" +
                "    \"acct_id\": 2\n" +
                "  }\n" +
                "}";

        String str3 = "{\n" +
                "  \"key\" : \"TEST123456\",\n" +
                "    \"type\":\"AcctDetailStatement\",\n" +
                "    \"from_system\":\"DSQ\",\n" +
                "    \"data\":{\n" +
                "        \"start_date\":\"2022-02-02\",\n" +
                "        \"end_date\":\"2022-04-05\"\n" +
                "    }\n" +
                "}";

    }

    public static int get(String name) {
        System.out.println(name + "执行了方法");
        return 1;
    }

    public static void testConsumer(Consumer<String> c, String t) {
        c.accept(t);
    }


    public static String testSupplier(Supplier<String> s) {
        return s.get();
    }

//    @Data
//    class test
//    {
//        private String a;
//        private String b;
//    }

    class TestClass {

        public void aaa(String aa) {
        }

        public void aaa1() {
            return;
        }

        public String bbb1() {
            return "";
        }

        public String bbb(String a) {
            return "";
        }

        public boolean ccc() {
            return true;
        }

        public Boolean ccc1(String aa) {
            return true;
        }

        public String ddd() {
            return "";
        }
    }
}
