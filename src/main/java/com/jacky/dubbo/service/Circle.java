package com.jacky.dubbo.service;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/24 11:33 AM
 */
public class Circle implements Shape {
    @Override
    public void draw() {

        System.out.println("画一个圆形");
    }

    @Override
    public void draw(URL url) {

        System.out.println("画一个圆形" + url);
    }
}
