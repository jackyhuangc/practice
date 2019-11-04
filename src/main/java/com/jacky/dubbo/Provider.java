package com.jacky.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.jacky.dubbo.service.Circle;
import com.jacky.dubbo.service.Shape;

import java.io.IOException;

/**
 * 使用广播方式的dubbo服务端
 *
 * @author Jacky
 * @date 2019/10/24 11:25 AM
 */
public class Provider {

    public void start(int port) {

        // 服务config
        ServiceConfig serviceConfig = new ServiceConfig();

        // 应用配置 <dubbo:application/>
        ApplicationConfig applicationConfig = new ApplicationConfig("provider");
        serviceConfig.setApplication(applicationConfig);

        // 协议配置 <dubbo:protocol/> 不配 默认一个20880 冲突
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo", port);
        serviceConfig.setProtocol(protocolConfig);

        // 注册中心
        // 使用multicast广播方式作为注册中心 暴露服务
        RegistryConfig registryConfig = new RegistryConfig("multicast://224.1.2.3:1234");
        //<!-- 标准使用zookeeper注册中心暴露服务地址 -->
        //<dubbo:registry address="zookeeper://127.0.0.1:2181" />

        serviceConfig.setRegistry(registryConfig);

        serviceConfig.setInterface(Shape.class);
        serviceConfig.setRef(new Circle());

        // 暴露服务
        serviceConfig.export();

        port = ((URL) serviceConfig.getExportedUrls().get(0)).getPort();
        System.out.println(String.format("服务启动：%s", port));
    }

    public static void main(String[] args) throws IOException {
        new Provider().start(-1);

        System.in.read();
    }
}
