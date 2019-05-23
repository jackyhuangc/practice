package com.jacky.practice;


import com.jacky.common.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NULL {

    public void test() {
        System.out.print("test");
    }

    public static void haha() {
        System.out.println("haha");
    }

    public static void main(String[] args) {

        String test=",,,,";
        String[] array=test.split(",");

        List<String> list = new ArrayList<String>();
        list.add("a");

        // 使用构造器创建一个包含list的列表list1
        List<String> list1 = new ArrayList<String>(list);
        // 使用subList生成与list相同的列表list2
        List<String> list2 = list.subList(0, list.size());

        // 集合转数组
        String[] strArray=new String[list.size()];
        list2.toArray(strArray);
        list2.add("b");

        Arrays.asList(strArray);

        System.out.println(list.equals(list1));
        System.out.println(list.equals(list2));

        Date date = DateUtil.parseDate("2019-05-10 00:00:00");
        boolean ret = date.before(date);
        boolean ret1 = date.after(date);

        // 输出为haha，因为null值可以强制转换为任何java类类型,(String)null也是合法的。但null强制转换后是无效对象，其返回值还是为null，而static方法的调用是和类名绑定的，不借助对象进行访问所以能正确输出
        ((NULL) null).haha();
    }
}