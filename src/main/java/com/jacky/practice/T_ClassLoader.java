package com.jacky.practice;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/25 21:02
 * @since jdk1.8
 */
public class T_ClassLoader {
    public static void main(String[] args)
    {
        ClassLoader cls=T_ClassLoader.class.getClassLoader();
        //System.out.println(cls.toString());
        while (cls!=null) {

            System.out.println(cls.toString());
            cls=cls.getParent();
        }

        System.out.println(cls);
    }
}
