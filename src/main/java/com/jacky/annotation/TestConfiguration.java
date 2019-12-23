package com.jacky.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-23 15:53
 */
@Configuration
@ComponentScan("com.jacky.annotation")
public class TestConfiguration {

    @Bean(initMethod = "init", destroyMethod = "destory")
    public TestService getTestService() {
        return new TestService();
    }
}
