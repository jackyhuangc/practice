package com.jacky.practice;

import com.jacky.common.util.Md5Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-02 10:43
 * @since jdk1.8
 */
class HelloA {

    // 3、将执行基类/子类构造函数（构造器）
    public HelloA() {
        System.out.println("HelloA");
    }

    // 2、将执行基类/子类代码构造块
    {
        System.out.println("I'm A class");
    }

    // 1、先执行基类/子类static修饰语句
    static {
        System.out.println("static A");
    }


}

/**
 * （1）类加载之后，按从上到下（从父类到子类）执行被static修饰的语句；（2）当static语句执行完之后,再执行main方法；（3）如果有语句new了自身的对象，将从上到下执行构造代码块、构造器（两者可以说绑定在一起）。
 *
 * @author huang
 */
public class HelloB extends HelloA {
    public HelloB() {
        System.out.println("HelloB");
    }

    {
        System.out.println("I'm B class");
    }

    static {
        System.out.println("static B");
    }

    public static void main(String[] args) throws IOException {


        String ip=getMyIP();
//		System.out.println("-------main start-------");
//
//		// 2、再执行main方法，并且new对象，将从上到下执行构造代码块、构造器
//		new HelloB();
//		new HelloB();
//		System.out.println("-------main end-------");
        List<String> list = Arrays.asList(new String[]{"a", "b", "c"});

        list.stream().anyMatch(s -> {

            System.out.println(s);
            return true;
        });


//        {
//            "merchant_name":"paysvr",
//                "sign_company":"nbfc",
//                "card_uuid":"MYUUID1234567890"
//        }
        String str = "card_uuid=MYUUID1234567890&merchant_name=paysvr&sign_company=nbfc&";

        String md5 = Md5Util.encode(str + "91530b51e73610596301b5a650826d59");

    }



    public static String getMyIP() throws IOException {
        String url="http://ip.chinaz.com/getip.aspx";
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText =  sb.toString();;
            jsonText=jsonText.replaceAll("'", "");
            jsonText=jsonText.substring(1,jsonText.length()-1);
            jsonText=jsonText.replaceAll(",", "<br/>");
            return jsonText;
        } finally {
            is.close();
            // System.out.println("同时 从这里也能看出 即便return了，仍然会执行finally的！");
        }
    }
}