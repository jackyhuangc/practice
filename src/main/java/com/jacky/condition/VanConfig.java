package com.jacky.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 3:46 PM
 */
// 使用配置可以避免冲突 No qualifying bean of type 'com.jacky.condition.Fighter' available: expected single matching bean but found 2: babana,billy
@Configuration
// 使用@Conditional可以选择性的注入或不注入指定配置(比如测试环境下可以简化配置，不需要某些复杂的配置，减少资源占用。
// 举例：@Conditional(JobStartCondition.class)开发环境下不启动XXL-JOB客户端) xxl-job 启动流程，先启动main应用->
@Conditional(LinuxCondition.class)
public class VanConfig {

    // @ConditionalOnBean（xxx.class）就是为了判断 xxx.class是否存在，若Billy没有注入，则不起作用
    @Bean
    @ConditionalOnBean(Billy.class)
    public Fighter fighter() {
        return new Billy();
    }

    // @ConditionalOnMissingBean 则是在第一点不存在的情况下起作用
    @Bean
    @ConditionalOnMissingBean
    public Fighter fighter2() {
        return new Babana();
    }
}