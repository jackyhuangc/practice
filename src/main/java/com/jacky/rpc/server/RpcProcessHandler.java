package com.jacky.rpc.server;

import com.jacky.common.util.JsonUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 具体请求处理的线程实现类
 *
 * @author Jacky
 * @date 2019/9/4 3:35 PM
 */
public class RpcProcessHandler implements Runnable {

    Socket socket;

    public RpcProcessHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectInputStream ins = null;
        ObjectOutputStream outs = null;
        try {

            // 可以通过java.io.ObjectInputStream将二进制流还原成对象。
            ins = new ObjectInputStream(socket.getInputStream());
            // java.io.ObjectOutputStream是实现序列化的关键类，它可以将一个对象转换成二进制流
            outs = new ObjectOutputStream(socket.getOutputStream());

            RpcTransformDto rpcTransformDto = (RpcTransformDto) ins.readObject();

            System.out.println("接收到请求数据...");
            System.out.println(JsonUtil.toJson(rpcTransformDto));
            Object obj = Dispatcher.dispatch(rpcTransformDto);
            outs.writeObject(obj);
            outs.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
    }
}
