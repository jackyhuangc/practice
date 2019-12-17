package com.jacky.condition;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
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
        // 初始化 到 beanDefinitionMap
        context.refresh();
        // prepareBeanFactory(beanFactory);
        // 	postProcessBeanFactory(beanFactory);->finishBeanFactoryInitialization(beanFactory)

        // （自动）方式1.直接用系统初始化是扫描注册bean(带有@Compenent等注解)的方式获取spring容器bean对象，默认的处理方式
        Billy billy = (Billy) context.getBean("billy");

        // （手动）方式2.将class对应的beanDefinition缓存到beanDefinitionMap
        BeanDefinition beanDefinition = new RootBeanDefinition(Monkey.class);
//        context.register(Monkey.class);
//        Monkey monkey = (Monkey) context.getBean("monkey");
        context.registerBeanDefinition("monkey1", beanDefinition);
        Monkey monkey1 = (Monkey) context.getBean("monkey1");

        // （手动）方式3.将单例对象注册到单例对象池中，singletonObjects
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        factory.registerSingleton("monkey2", new Monkey());
        Monkey monkey2 = (Monkey) context.getBean("monkey2");

        //  (自动) 方式4.用@Bean注解配合Configuration的方式，初始化构造bean对象并放入singletonObjects，该方式多用于数据源/redis配置等方式
        //  @Configuration 注解本质上还是 @Component,@Configuration会采用Cglib方式进行增强，生成代理类。
        //  https://blog.csdn.net/isea533/article/details/78072133
        Van van = (Van) context.getBean(Van.class);
        van.fight();
    }
}
