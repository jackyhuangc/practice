package com.jacky.dubbo.service;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/24 11:31 AM
 */
@SPI("Circle")//使用dubbo的spi需要添加此注解 设置默认实现类为Circle
public interface Shape {

    void draw();

    // 可自定义key @Adaptive("demo")
    @Adaptive
    void draw(URL url,String str);
}
