package com.jacky.practice;

import java.util.HashMap;
import java.util.Map;

public class T_HashMap {

    public static void main(String[] args) {

        HashMap<String, String> map = new HashMap<>();

        map.put("test1", "test11");
        map.put("test1", "test12");
        map.put("test2", "test22");

        for (String key : map.keySet()) {
            System.out.println(map.get(key));
        }


        for (Map.Entry<String,String> entry:map.entrySet())
        {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
