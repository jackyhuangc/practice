package com.jacky.practice;

import com.jacky.common.util.LogUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

// @Configuration // Spring 容器在启动时，会加载默认的一些 PostPRocessor，其中就有 ConfigurationClassPostProcessor，这个后置处理程序专门处理带有 @Configuration 注解的类，这个程序会在 bean 定义加载完成后，在 bean 初始化前进行处理。
@Component//使用Component注解，并且不指定其属性name的值，则bena的ID默认为类的名称的第一个字母小写的字符串
//@Component("beanID")//使用Component注解，并且指定其name属性的值。则bean的ID就是指定的值，不再是默认的情况。
@Scope("prototype")//作用域  默认为singleton 启动时创建/类似@Bean方式，即为单例  prototype 使用时创建
public class BeanAnnotation {

    public BeanAnnotation() {
        LogUtil.error("*******************BeanAnnotation对象开始初始化*******************");
    }

    public void say(String arg) {
        System.out.println("BeanAnnotation : print = " + arg);
    }

    //测试作用域用hashcode值来区分
    public void scope() {
        LogUtil.error("BeanAnnotation : = " + this.hashCode());
    }

    @Bean("beanAnno")
    public BeanAnnotation beanAnno() {
        BeanAnnotation annotation = new BeanAnnotation();
        LogUtil.error("BeanAnnotation : = " + annotation.hashCode());
        return annotation;
    }
}