package com.jacky.practice;

import com.jacky.common.util.LogUtil;
import com.sun.tools.corba.se.idl.InterfaceGen;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class T_FunctionalInterface {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);

        // list.stream().filter(s -> eval(s)).forEach(System.out::println);

        eval(list, (s) -> s > 2);
        m2((s) -> Integer.parseInt(s), (s1) -> s1 + 10, "12");
        m3((s) -> Integer.parseInt(s), (s1) -> s1 + 10, "13");

        String s1 = "xxx,100";
        int index = s1.indexOf(",");
        m4((s) -> s.split(",")[1], s -> Integer.parseInt(s), s -> s + 100, "张三,100");

        testStream();
    }

    private static <T> void eval(List<T> list, Predicate<T> predicate) {

        for (T t : list) {
            if (predicate.test(t)) {
                LogUtil.info("true");
            }
        }

        Stream<T> stream = list.stream().filter(predicate);
        stream.forEach(System.out::println);
    }

    // 2.andThen : 先做什么，使用结果再做什么，合并两个apply()的结果；
    // 例如：1).将一个String的分数转换为Integer的分数 2).将刚才转换后的Integer再加10分
    public static void m2(Function<String, Integer> func1, Function<Integer, Integer> func2, String score) {

        // 在执行apply()的之前 先做什么？？？
        int ret = func1.andThen(func2).apply(score);
        LogUtil.info("转换后的分数" + ret);
    }


    /**
     * 扩展说一下 PECS(Producer Extends Consumer Super)原则
     * extends 子类继承 编译器不知道？具体是哪个子类，为了类型安全，只好阻止向其中加入任何子类 事实上，不能往使用了? extends的数据结构里写入任何的值
     * super 超类 编译器并不知道？具体是哪个超类，为了类型安全，不允许加入特定的任何超类型
     * <p>
     * 如果要从集合中读取类型T的数据，并且不能写入，可以使用 ? extends 通配符；(Producer Extends)
     * 如果要从集合中写入类型T的数据，并且不需要读取，可以使用 ? super 通配符；(Consumer Super)
     * 如果既要存又要取，那么就不要使用任何通配符
     */

    // 对于定义Lambda函数，我们需要让参数“严格”，便于我们去处理，用super，只能是基类。至少大家的基类都一样
    // 对于返回值，我们需要让参数“宽松”，尽量满足用户的需求，用extends，必须是派生类。结果可以用不同的子类接收转换
    public static <T, R> R m3(Function<? super T, ? extends R> fun1, Function<R, R> func2, T score) {
        R r = fun1.andThen(func2).apply(score);
        return r;
    }

    /**
     * String str = "赵丽颖, 20";
     * 按照顺序依次：
     * 1 . 将字符串截取数字年龄部分，得到字符串；
     * 2 . 将上一步的字符串转换成为int类型的数字；
     * 3 . 将上一步的int数字累加1 00， 得到结果int数字。
     */
    public static Integer m4(Function<String, String> func1, Function<String, Integer> func2, Function<Integer, Integer> func3, String str) {
        Integer ret = func1.andThen(func2).andThen(func3).apply(str);
        LogUtil.info(String.valueOf(ret));
        return ret;
    }

    /**
     * 1).它不是IO流；
     * 2).它是专门针对“集合”操作的一个流类，它类似于：迭代器。
     * 只是它可以支持Lambda，使用更快捷的方式操作集合；
     * 1).我们经常需要对集合中的元素进行一系列筛选，以前都是使用循环 + 判断。
     * 如果要进行一系列操作：例如：检索所有的“张姓”学员，然后再找出其中的男同学，然后年龄大于20岁的，然后取找到的前3个，使用“Stream”流可以很方便的进行操作。
     */
    public static void stream() {
//        1).List集合：
//        List<String> list = new ArrayList<>();
//        ...
//        Stream<String> stream = list.stream();
//        2).Set集合：
//        Set<String> set = new HashSet<>();
//        ...
//        Stream<String> stream = set.stream();
//        3).Map集合：
//        Map<Integer,String> map = new HashMap<>();
//        Set<Integer> keys = map.keySet();
//        Stream<Integer> keyStream = keys.stream();
//        4).数组：
//        int[] arr = {1,432,4,325,24,314,32};
//        Stream<Integer> stream = Stream.of(arr);


        List<String> list = new ArrayList<>();
        list.stream().filter(null).forEach(null);
        list.stream().filter(null).count();
        list.stream().filter(null).limit(10).forEach(null);
        list.stream().filter(null).skip(10).forEach(null);


        Stream.of("10", "20", "30", "40")
                .map(Integer::parseInt)
                .forEach(System.out::println);

        Stream<String> s1 = Stream.of("10", "20", "30");
        Stream<String> s2 = Stream.of("40", "50", "60");

        Stream.concat(s2, s1).forEach(System.out::println);

        Stream<String> s3 = Stream.of("1", "2", "3");
        Stream<String> s4 = Stream.of("4", "5", "6");

        Stream.concat(s3, s4).forEach(System.out::println);
    }

    public static void testStream() {
        List<String> one = new ArrayList<>();
        one.add("迪丽热巴");
        one.add("宋远桥");
        one.add("苏星河");
        one.add("老子");
        one.add("庄子");
        one.add("孙子");
        one.add("洪七公");
        List<String> two = new ArrayList<>();
        two.add("古力娜扎");
        two.add("张无忌");
        two.add("张三丰");
        two.add("赵丽颖");
        two.add("张二狗");
        two.add("张天爱");
        two.add("张三");

//        有以下要求：
//        1).第一个队伍只要名字为3个字的成员姓名；
//        2 . 第一个队伍筛选之后只要前3个人；
//        3 . 第二个队伍只要姓张的成员姓名；
//        4 . 第二个队伍筛选之后不要前2个人；
//        5 . 将两个队伍合并为一个队伍；
//        6 . 根据姓名创建 Person 对象；
//        7 .打印整个队伍的Person对象信息。

        one.stream().filter(s -> s.length() == 3).forEach(System.out::println);
        one.stream().filter(s -> s.length() == 3).limit(3).forEach(System.out::println);
        two.stream().filter(s -> s.startsWith("张")).forEach(System.out::println);
        two.stream().filter(s -> s.startsWith("张")).skip(2).forEach(System.out::println);
        Stream.concat(one.stream(), two.stream()).forEach(System.out::println);

        Stream.concat(one.stream(), two.stream()).map(s -> {
            Person p = new Person(s, 0);
            return p;
        }).collect(Collectors.toList());

        Stream.concat(one.stream(), two.stream()).map(s -> {
            Person p = new Person(s, 0);
            return p;
        }).collect(Collectors.toList()).forEach((s) -> {

            System.out.println(s.getName());
        });


        //Stream流常用方法收集Stream结果collect方法
        Stream.concat(one.stream(), two.stream()).collect(Collectors.toMap(s -> s.length(), Function.identity(), (s1, s2) -> s2, HashMap::new));
        Stream<String> stream = Stream.of("10", "20", "30", "40");

        List<String> strList = stream.collect(Collectors.toList());
        Set<String> strSet = stream.collect(Collectors.toSet());

//        01.能够使用Function函数式接口
//        1).抽象方法：
//        1).R apply(T t):通常用于转换（String转换为Integer）
//        2).默认方法：
//        1).andThen():连接两个结果；
//
//        02.能够使用Predicate函数式接口
//        1).抽象方法：
//        1).boolean test(T t):测试
//        2).默认方法：
//        1).and:两次测试的并且
//        2).or :两次测试的或者
//        3).negate : 非
//        03.能够理解流与集合相比的优点
//        1).“流”是对“集合”操作的工具类，可以和Lambda一起提供非常方便的检索、过滤集合元素的方
//
//        式，使操作集合比较方便。
//        2).集合就是用来存储数据的，有一些方法用于增删改查，但如果要进行一些过滤，就不是很方便了。
//        1
//        2
//        04.能够理解流的延迟执行特点
//“流”的延迟：指仍然返回这种流对象，可以链式调用；
//
//        05.能够通过集合、 映射或数组获取流
//        List:
//        Stream m = list.stream();
//        Set:
//        Stream m = set.stream();
//        Map:
//        Set keys = map.keySet();
//        Stream keyStream = keys.stream();
//        数组：
//        Stream stream = Stream.of(数组对象/数值列表)
//        06.能够掌握常用的流操作
//        1).filter():过滤；
//        2).forEach():逐一获取；
//        3).limit():取前几个；
//        4).skip():跳过前几个；
//        5).count():统计数量
//        6).concat():合并两个流；
//        7).map():将一种类型的流转换为另一中类型的流；
//        8).paraller():获取并行流
//        9).collect():收集结果
//
//        07.能够使用流进行并发操作
//        1).获取并发流：
//        1).集合对象.parallerStream().正常调用
//        2).集合对象.stream().paraller().正常调用
//        08.能够将流中的内容收集到集合中
//        Stream stream = Stream.of(“10”,”20”,”30”);
//        List list = stream.collect(Collectors.toList());
//
//        09.能够将流中的内容收集到数组中
//        Stream stream = Stream.of(“10”,”20”,”30”);
//        Object[] objArray = stream.toArray();
    }

}