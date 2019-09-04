package com.jacky.rpc.client;

import java.lang.reflect.Proxy;

/**
 * 4.封装客户端工厂类
 *
 * @author Jacky
 * @date 2019/9/4 3:52 PM
 */
public class RpcClientFactory {

    // 用JDK动态代理创建业务实现类
    public static <T> T getProxyInstance(Class<T> clazz) {

        return (T) Proxy.newProxyInstance(RpcClientFactory.class.getClassLoader(), new Class[]{clazz}, new RpcClientHandler(clazz));
    }
}
