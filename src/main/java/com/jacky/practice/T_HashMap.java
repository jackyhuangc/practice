package com.jacky.practice;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class T_HashMap {

    public static void main(String[] args) {

        // 继承于AbstractMap<K,V>和Map<K,V>且未使用synchronized，不安全但效率高，key可为空(此时key的hash为0)
        Map<String, String> map = new HashMap<>();

        map.put("test1", "test11");
        map.put("test1", "test12");
        map.put("test2", "test22");
        map.put(null, "test232");
        map.put(null, "test222");
        map.put(null, "test212");

        for (String key : map.keySet()) {
            System.out.println(map.get(key));
        }


        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        // 继承于Dictionary<K,V>和Map<K,V>且使用synchronized，安全但效率低，key为空将报空指针异常
//        Hashtable<String, String> hashtable = new Hashtable<>();
//        hashtable.put("", "");
    }
}
