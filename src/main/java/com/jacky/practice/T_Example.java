package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.ss.extractor.ExcelExtractor;

import java.util.*;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-02 10:52
 * @since jdk1.8
 */
public class T_Example {

    String str = new String("good");

    char[] ch = {'a', 'b', 'c'};

    public static void main(String args[]) {

        try {

            Map<String, Object> map = new HashMap<>();
            List list= new ArrayList();
            list.add("web");
            map.put("scope", list);

            Set<String> scope = new LinkedHashSet((Collection) (map.containsKey("scope") ? (Collection) map.get("scope") : Collections.emptySet()));

            list=list;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
//		T_Example ex = new T_Example();
//
//		ex.change(ex.str, ex.ch);
//
//		System.out.print(ex.str + " and ");
//
//		System.out.print(ex.ch);

    }

    public void change(String str, char ch[]) {

        // 替换无法改变引用内容
        str = "test ok";

        // 可以改变具体内容
        ch[0] = 'g';

    }
}
