package com.jacky.concurrent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/10/15 3:52 PM
 */
public abstract class ClassA<T> {

    Class clazz;

    ClassA() {

        /**
         * 子类需定义具体的泛型实现类， 才能用此方法获取类型。public class ClassB extends ClassA<String>
         */

        // 第一步：获得Class
        // 正在被调用的类的Class,CustomerImple或者LinkManImple.
        Class clazz = this.getClass();

        // 第二步：获取参数化类型参数（BaseDaoImpl<Customer> BaseDaoImpl<LinkMan>）
        // 因为type是顶级接口没有定义任何方法，所以需要强转为子接口ParameterizedType
        ParameterizedType ptype = (ParameterizedType) clazz.getGenericSuperclass();

        // 第三步：根据（参数化类型参数）获得（实际化类型参数）：
        // 得到一个实际类型参数的数组？Map<Integer,String>
        // 因为Java中接口可以多实现
        Type[] types = ptype.getActualTypeArguments();

        // 第四步：只获得第一个实际化类型参数
        this.clazz = (Class) types[0];// 得到 Customer LinkMan User
    }

    public abstract void test();
}
