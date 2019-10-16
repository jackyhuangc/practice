package com.jacky.concurrent;

/**
 * 请输入描述
 * 继承时，需明确泛型对象，后面才能在超类中取到不同的类型定义
 * @author Jacky
 * @date 2019/10/15 3:52 PM
 */
public class ClassB extends ClassA<String> {


    @Override
    public void test() {

    }
}
