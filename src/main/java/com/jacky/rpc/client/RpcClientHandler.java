package com.jacky.rpc.client;

import com.jacky.rpc.api.RpcAnnotation;
import com.jacky.rpc.server.RpcTransformDto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/4 4:17 PM
 */
public class RpcClientHandler implements InvocationHandler {

    Class<?> clazz;

    public RpcClientHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println(method.getName());
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        RpcTransformDto rpcTransformDto = new RpcTransformDto();
        rpcTransformDto.setFullClazzPath(this.clazz.getAnnotation(RpcAnnotation.class).mapped());
        rpcTransformDto.setMethodName(method.getName());
        rpcTransformDto.setParams(args);

        ObjectOutputStream outs = null;
        ObjectInputStream ins = null;

        try {

            Socket socket = new Socket("127.0.0.1", 9999);

            // java.io.ObjectOutputStream是实现序列化的关键类，它可以将一个对象转换成二进制流
            outs = new ObjectOutputStream(socket.getOutputStream());
            // 然后可以通过ObjectInputStream将二进制流还原成对象。
            ins = new ObjectInputStream(socket.getInputStream());
            outs.writeObject(rpcTransformDto);
            outs.flush();

            Object obj = ins.readObject();

            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != ins) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != outs) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
