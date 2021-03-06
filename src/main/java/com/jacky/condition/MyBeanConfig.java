package com.jacky.condition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/20 7:18 PM
 */

@Configuration
public class MyBeanConfig {
    @Bean
    public Country country() {
        return new Country(); // 只实例化一次
    }

    @Bean
    public UserInfo userInfo() {
        return new UserInfo(country()); // 直接使用已存在的country
    }
}

//@Component     //注解并没有通过 cglib 来代理@Bean 方法的调用，因此像下面这样配置时，就是两个不同的 country
//@Configuration // @Configuration 采用cglib 来增强生成代理类，其中所有带 @Bean 注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例
//public class MyBeanConfig {
//
//    @Bean  // 实例化另一个Country
//    public Country country(){
//        return new Country();
//    }
//
//    @Bean
//    public UserInfo userInfo(){
//        return new UserInfo(country());  // 实例化一个Country
//    }
//
//}

// 如果非要使用@Component+@Bean，可采用以下方法保证只有一个country实例
//@Component
//public class MyBeanConfig {
//
//    @Autowired // 从容器中取得实例 @Autowired是根据类型进行自动装配的，如果需要按名称进行装配，则需要配合@Qualifier [1]  使用
//    private Country country;
//
//    @Bean  // 只实例化一次，并注入到Spring容器
//    public Country country(){
//        return new Country();
//    }
//
//    @Bean
//    public UserInfo userInfo(){
//        SpringContextUtil.getBean("SccbaMiYangHeadConfig"); 也可以手动装配或提取
//        return new UserInfo(country); // 直接使用从容器中取得的实例
//    }
//
//}