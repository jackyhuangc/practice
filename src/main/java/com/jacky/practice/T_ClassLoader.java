package com.jacky.practice;

import sun.misc.Launcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/25 21:02
 * @since jdk1.8
 */
public class T_ClassLoader {
    public static void main(String[] args) {
        // test(new T_ClassLoader());
//        System.out.println(ClassLoader.getSystemClassLoader());
//        String classPath = System.getProperty("java.class.path");
//        for (String path : classPath.split(";")) {
//            System.out.println(path);
//        }


        Object obj = new Object();
        sun.security.ec.SunEC obj1 = new sun.security.ec.SunEC();
        T_ClassLoader obj2 = new T_ClassLoader();
        // 这是启动类
        System.out.println(String.format("启动类%s,扩展类%s,应用类%s", isJdkClass(obj.getClass()), isExtClass(obj.getClass()), isAppClass(obj.getClass())));

        // 这是扩展类
        System.out.println(String.format("启动类%s,扩展类%s,应用类%s", isJdkClass(obj1.getClass()), isExtClass(obj1.getClass()), isAppClass(obj1.getClass())));

        // 这是应用类
        System.out.println(String.format("启动类%s,扩展类%s,应用类%s", isJdkClass(obj2.getClass()), isExtClass(obj2.getClass()), isAppClass(obj2.getClass())));

        ClassLoader cls = T_ClassLoader.class.getClassLoader();

        //System.out.println(cls.toString());
        while (cls != null) {

            System.out.println(cls);
            cls = cls.getParent();
        }
        System.out.println(cls);

        System.out.println(ClassLoader.getSystemClassLoader()); // AppClassLoader
        System.out.println(Launcher.getLauncher().getClassLoader()); // AppClassLoader

        // ClassLoader和Launcher类都是由启动类加载器加载到内存中的。
        // Launcher类属于sun.misc包，这个包属于charsets.jar包下，从上面的输出结果中，可以看到这个包是由启动类加载器加载的；
        // 而ClassLoader类是位于java.lang包下，位于resources.jar包下，同样也是由启动类加载器加载的
        System.out.println(ClassLoader.class.getClassLoader()); // null
        System.out.println(Launcher.class.getClassLoader()); // null

    }

    private static void test(Object obj) {

        if (null == obj) {
            return;
        }

        // 上下文类加载为系统类加载器，可以通过ClassLoader.getSystemClassLoader()或Launcher.getLauncher().getClassLoader()取到
        if (obj.getClass().getClassLoader() == ClassLoader.getSystemClassLoader()) {
            System.out.println("这是由App ClassLoader加载加载的对象");
        }

        List<Field> fields = getAllFields(obj.getClass());
        for (Field field : fields) {
            Object value = getFieldValue(field.getName(), obj);

            ClassLoader classLoader = field.getType().getClassLoader();
            if (classLoader == Launcher.getLauncher().getClassLoader()) {

            }
            if (!isJdkClass(field.getType())) {

                // 可以是ExtClassLoader 加载的对象
                // sun.security.ec.SunEC sunEC = new sun.security.ec.SunEC();
                // ClassLoader classLoader = sunEC.getClass().getClassLoader();
                // System.out.println("这是扩展类对象，由Ext ClassLoader加载：" + field.getType());

                // 可以是AppClassLoader 加载的对象
                // Class clazz = Class.forName("com.mysql.jdbc.Driver");
                // Object object = clazz.newInstance();
                // classLoader = object.getClass().getClassLoader();
                // Launcher.getLauncher().getClassLoader();
                // ClassLoader.getSystemClassLoader();

                // classLoader 为 Launcher$AppClassLoader@1423
                System.out.println("这是自定义对象，由App ClassLoader加载：" + field.getType());
                test(value);
            } else {
                // classLoader 为 null
                System.out.println(String.format("这是系统对象，由BootStrap ClassLoader加载（C++代码，不可见），%s,%s,%s", field.getName(), field.getType(), value));
            }
        }
    }

    private static List<Field> getAllFields(Class clazz) {

        List<Field> listAll = new ArrayList<>();
        listAll.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null && !isJdkClass(clazz)) {
            listAll.addAll(getAllFields(clazz.getSuperclass()));
        }

        return listAll;
    }

    private static Object getFieldValue(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    protected static Boolean isJdkClass(Class<?> clazz) {
        return clazz != null && clazz.getClassLoader() == null;
    }

    protected static Boolean isExtClass(Class<?> clazz) {
        return !isJdkClass(clazz) && !isAppClass(clazz);
    }

    protected static Boolean isAppClass(Class<?> clazz) {
        return clazz.getClassLoader() == ClassLoader.getSystemClassLoader();
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