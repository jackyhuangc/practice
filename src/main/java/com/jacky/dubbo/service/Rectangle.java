package com.jacky.dubbo.service;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.extension.Adaptive;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/24 11:33 AM
 */
//@Adaptive
@Activate(group = "consumer")
public class Rectangle implements Shape {
    @Override
    public void draw() {

        System.out.println("画一个长方形");
    }

    @Override
    @Activate(group = "rect")
    public void draw(URL url, String str) {

        System.out.println(str + "画一个长方形" + url);
    }
}
