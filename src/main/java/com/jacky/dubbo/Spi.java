package com.jacky.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.jacky.dubbo.service.Shape;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.*;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/24 11:25 AM
 */
public class Spi {

    public static void main(String[] args) {

//        // 普通的Java SPI
//        // META-INF\services的目录下增加一个配置文件，文件必须以接口的全限定类名保持一致，例如：com.jacky.dubbo.service.Shape
//        ServiceLoader<Shape> serviceLoader = ServiceLoader.load(Shape.class);
//        serviceLoader.forEach((s) -> {
//            s.draw();
//        });

        /**
         * jdk SPI仅通过接口类名获取所有实现，但是Duboo SPI可以根据接口类名和key值获取具体一个实现
         * 可以对扩展类实例的属性进行依赖注入，即IOC/DI
         * 可以采用装饰器模式实现AOP功能
         * 需要将服务提供者配置文件设计成KV键值对的形式，Key是拓展类的name，Value是扩展的全限定名实现类 Circle=com.jacky.dubbo.service.Circle
         * 可以发现Dubbo的源码中有很多地方都用到了@SPI注解，例如：Protocol（通信协议），LoadBalance（负载均衡）等。基于Dubbo
         SPI，我们可以非常容易的进行拓展。ExtensionLoader是扩展点核心类，用于载入Dubbo中各种可配置的组件，比如刚刚说的Protocol和LoadBalance等
         */

        ExtensionLoader<Shape> extensionLoader = ExtensionLoader.getExtensionLoader(Shape.class);

        // 获取实例1 获取默认的实现，接口需要加@SPI("Circle")
        Shape shape1 = extensionLoader.getDefaultExtension();
        shape1.draw();

        // 获取实例2 直接通过配置中的key定义别名获取扩展实现类，来源cachedClasses，此方式与3冲突
        // Shape shape2 = extensionLoader.getExtension("Rect");
        // shape2.draw();

        // 获取实例3 自适应扩展方式 @Adaptive直接配置在类上（只能注解一个实现类）来源cachedAdaptiveClass，此方式与2冲突
//        Shape shape3 = extensionLoader.getAdaptiveExtension();
//        shape3.draw();

        // 获取实例4 自适应扩展方式 @Adaptive配置在方法上面，通过解析url参数，拿到key
        // 动态生成$Adaptive class。先通过字符串拼接java代码，再通过javassist compiler编译  Shape名称自动转小写
        URL url = URL.valueOf("dubbo://localhost/test?shape=Rect");
        Shape shape4 = extensionLoader.getAdaptiveExtension();
        shape4.draw(url,"xxx");

        // 获取实例5 通过配置key查找
        List<Shape> list1 = extensionLoader.getActivateExtension(url, new String[]{"Circle"});

        // 获取实例6 通过@Activate group查找
        List<Shape> list2 = extensionLoader.getActivateExtension(url, new String[]{}, "consumer");
        list2.forEach((shape) -> {

            shape.draw();
        });
    }
}
