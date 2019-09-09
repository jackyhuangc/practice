package com.jacky.com.jacky.synchronize;

import javassist.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/6 10:24 AM
 */
public class T_Synchronize {

    public static void main(String[] args) {
        Object objMonitor = new Object();

        Proxy.newProxyInstance(T_Synchronize.class.getClassLoader(), new Class[]{}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    // 代理工厂创建动态代理
    public static void testJavassistFactoryProxy() throws Exception {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();

        // 设置被代理类的类型
        proxyFactory.setSuperclass(RayTest.class);

        // 创建代理类的class
        Class<ProxyObject> proxyClass = (Class<ProxyObject>) proxyFactory.createClass();

        // 创建对象
        Object proxyTest = (Object) proxyClass.newInstance();

        ((ProxyObject) proxyTest).setHandler(new MethodHandler() {
            // 真实主题
            Object test = new Object();

            public Object invoke(Object self, Method thisMethod,
                                 Method proceed, Object[] args) throws Throwable {
                String before = "before ";
                Object str = thisMethod.invoke(test, args);
                String after = " after";
                return before + str + after;
            }
        });
        //String exe = proxyTest.exe();
        //System.out.println(exe);
    }

    // 动态代码创建的例子
    // 下面例子使用 Javassist 的 API成功组织出代理类的一个子类，可以看出 添加构造函数，添加属性，
    // 添加方法，内容 都是通过字符串类型完成即可。 通过 Javassist 强大的字节生成能力可以达到动态
    // 增加类和实现动态代理的功能.
    public static void testJavassistDefineClass() throws Exception {
        // 创建类池，true 表示使用默认路径
        ClassPool classPool = new ClassPool(true);

        String className = RayTest.class.getName();
        // 创建一个类 RayTestJavassistProxy
        CtClass ctClass = classPool.makeClass(className + "JavassistProxy");

        // 添加超类
        // 设置 RayTestJavassistProxy 的父类是 RayTest.
        ctClass.setSuperclass(classPool.get(RayTest.class.getName()));

        // 添加默认构造函数
        ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));

        // 添加属性
        ctClass.addField(CtField.make("public " + className + " real = new " +
                className + "();", ctClass));

        // 添加方法，里面进行动态代理 logic
        ctClass.addMethod(CtNewMethod.make("public String exe() { return \"before \" + real.exe() + \" after\";}",
                ctClass));
        Class<RayTest> testClass = (Class<RayTest>) ctClass.toClass();
        RayTest rayTest = testClass.newInstance();
        String exe = rayTest.exe();
        System.out.println(exe);
    }
}
