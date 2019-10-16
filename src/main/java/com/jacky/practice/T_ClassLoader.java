package com.jacky.practice;

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

    private static void test(Object obj) {

        if (null == obj) {
            return;
        }

        List<Field> fields = getAllFields(obj.getClass());
        for (Field field : fields) {
            Object value = getFieldValue(field.getName(), obj);

            ClassLoader classLoader = field.getType().getClassLoader();
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