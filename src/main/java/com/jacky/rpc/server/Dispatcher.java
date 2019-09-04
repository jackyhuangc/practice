package com.jacky.rpc.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 3.定义调用入口
 *
 * @author Jacky
 * @date 2019/9/4 3:09 PM
 */
public class Dispatcher {

    /**
     * 反射的方式分发调用具体的业务实现及传参
     *
     * @param rpcTransformDto
     * @return
     */
    public static Object dispatch(RpcTransformDto rpcTransformDto) {
        Object obj = null;
        try {

            Class<?>[] paraTypes = new Class<?>[rpcTransformDto.getParams().length];

            for (int i = 0; i < paraTypes.length; i++) {
                paraTypes[i] = rpcTransformDto.getParams()[i].getClass();
            }

            /**
             *  Java程序在运行时，Java运行时系统一直对所有的对象进行所谓的运行时类型标识。这项信息纪录了每个对象所属的类。
             *  虚拟机通常使用运行时类型信息选准正确方法 去执行，用来保存这些类型信息的类是Class类
             *
             *  Class.forName 获取指定路径的Class对象
             */
            Class clazz = Class.forName(rpcTransformDto.getFullClazzPath());
            Method method = clazz.getMethod(rpcTransformDto.getMethodName(), paraTypes);

            // 用对象实例调用指定方法+参数
            obj = method.invoke(clazz.newInstance(), rpcTransformDto.getParams());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
