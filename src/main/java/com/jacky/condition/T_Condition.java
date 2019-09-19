package com.jacky.condition;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 3:41 PM
 */
public class T_Condition {


    @Test
    public void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.jacky.condition");
        context.refresh();

        Van van = (Van) context.getBean(Van.class);

        van.fight();
    }
}
