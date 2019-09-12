package com.jacky.annotation;

import com.jacky.javassist.T_Base;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/12 7:29 PM
 */
@ComponentScan("com.jacky.annotation")
@EnableAspectJAutoProxy
public class Context {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(Context.class);

        //configApplicationContext.scan("com.jacky.annotation");
        //configApplicationContext.refresh();


        UserService userService = (UserService) configApplicationContext.getBean("test");

        String name = userService.getUser("xxxx");
    }
}
