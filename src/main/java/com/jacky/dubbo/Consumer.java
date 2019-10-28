package com.jacky.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.jacky.dubbo.service.Circle;
import com.jacky.dubbo.service.Shape;

/**
 * 使用广播方式的dubbo消费端
 *
 * @author Jacky
 * @date 2019/10/24 11:25 AM
 */
public class Consumer {

    public Object getRemoteCall(String url) {
        ReferenceConfig referenceConfig = new ReferenceConfig();

        ApplicationConfig applicationConfig = new ApplicationConfig("consumer");
        referenceConfig.setApplication(applicationConfig);

        // 使用multicast广播方式作为注册中心 订阅服务
        RegistryConfig registryConfig = new RegistryConfig("multicast://224.1.2.3:1234");
        referenceConfig.setRegistry(registryConfig);

        referenceConfig.setInterface(Shape.class);

        referenceConfig.setLoadbalance("roundrobin");
        return referenceConfig.get();
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        Shape shape = (Shape) consumer.getRemoteCall(null);

        shape.draw();
    }
}
