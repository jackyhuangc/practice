package com.jacky.practice;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/25 21:02
 * @since jdk1.8
 */
public class T_ClassLoader {
    public static void main(String[] args) {

//        System.out.println(ClassLoader.getSystemClassLoader());
//        String classPath = System.getProperty("java.class.path");
//        for (String path : classPath.split(";")) {
//            System.out.println(path);
//        }

        ClassLoader cls = T_ClassLoader.class.getClassLoader();

        //System.out.println(cls.toString());
        while (cls != null) {

            System.out.println(cls.toString());
            cls = cls.getParent();
        }

        System.out.println(cls);
    }

}


class Father {
    public void showName() {
        System.out.println("Father...");
    }
}

class Child extends Father {
    public void showName() {
        System.out.println("children");
    }
}