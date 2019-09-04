package com.jacky.rpc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/4 3:25 PM
 */
public class RpcServer {

    public static void main(String[] args) {
        new RpcServer().startUp(9999);

        // 熔断机制
        // 负载均衡(集群环境)
        // 服务注册于发现(集群环境)
        // dubbo基于netty，netty底层实现nio
        // dubbo与spring cloud 都是客户端负载均衡
        /**
         * 负载均衡策略：
         * 随机 Random LoadBalance
         * 轮询 RoundRobin LoadBalance
         * 最少活跃调用数（权重）LeastActive LoadBalance
         * 活跃数指调用前后计数差,优先调用高的，相同活跃数的随机。使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
         * 一致性Hash ConsistentHash LoadBalance
         * 基于响应时间
         */
    }

    /**
     * 启动服务器监听具体的客户端请求及远程调用处理
     *
     * @param port
     */
    public void startUp(int port) {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            // BIO
            while (true) {
                try {

                    Socket socket = serverSocket.accept();

                    System.out.println("发现客户端连接...");
                    executor.execute(new RpcProcessHandler(socket));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
