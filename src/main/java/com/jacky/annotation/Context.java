package com.jacky.annotation;

import com.jacky.javassist.T_Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/12 7:29 PM
 */
@ComponentScan("com.jacky.annotation")
//@EnableAspectJAutoProxy
@EnableAspectJAutoProxy(proxyTargetClass = true)// 强制使用cglib方式生成代理类
@EnableTransactionManagement
public class Context {

    @Autowired
    @Qualifier("test")
    IUserService userService3;

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Context.class);

        //configApplicationContext.scan("com.jacky.annotation");
        //configApplicationContext.refresh();

        Context cxt = context.getBean(Context.class);

        // 如果加入了AOP拦截配置，且如果是采用JDK动态代理，则肯定是实现的接口，只能用接口转换
        IUserService userService1 = (IUserService) context.getBean("test");
        // 如果加入了AOP拦截配置，且如果是采用Cglib动态代理，则可以用对象转换(可以强制指定proxyTargetClass = true，用Cglib动态代理实现了接口的对象)
        UserService userService2 = (UserService) context.getBean("test");

        // 如果启用了事务（@EnableTransactionManagement、@Transactional），且如果是采用JDK动态代理，则肯定是实现的接口，只能用接口转换
        IUserService transcationUserService = context.getBean("transactionUserService", IUserService.class);

        String name = userService1.getUser("xxxx");

        /***
         * 1.启用了事务（@EnableTransactionManagement、@Transactional），实现了接口，将使用Jdk动态代理生成代理对象；getBean时只能用接口转换
         * 2.启用了事务（@EnableTransactionManagement、@Transactional），实现了接口，强制指定@EnableTransactionManagement(proxyTargetClass = true)或@EnableAspectJAutoProxy(proxyTargetClass=true)，将使用Cglib动态代理生成代理对象；getBean时可用接口和对象转换
         * 3.启用了事务（@EnableTransactionManagement、@Transactional），没有实现任何接口，将使用Cglib动态代理生成代理对象；getBean时可用对象转换
         * 4.加入aop拦截配置，实现了接口，将使用Jdk动态代理生成代理对象；getBean时只能用接口转换
         * 5.加入aop拦截配置，实现了接口，强制指定@EnableAspectJAutoProxy(proxyTargetClass=true)，将使用Cglib动态代理生成代理对象；getBean时可用接口和对象转换
         * 6.加入aop拦截配置，没有实现任何接口，将使用Cglib动态代理生成代理对象；getBean时可用对象转换
         * 7.没有开启事务，也未加入aop拦截配置，无论实现了接口与否，生成的都是原生对象pojo bean；如果实现了接口，可用接口和对象转换。
         *
         *
         * PS:最终的测试结果大致是这样的，在1.6和1.7的时候，JDK动态代理的速度要比CGLib动态代理的速度要慢，但是并没有教科书上的10倍差距，在JDK1.8的时候，JDK动态代理的速度已经比CGLib动态代理的速度快很多了，希望小伙伴在遇到这个问题的时候能够有的放矢！
         * ***/
    }
}
