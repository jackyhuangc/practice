package com.jacky.annotation;

import com.jacky.common.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-23 14:29
 */
@Component
public class MyBeanPostProcesser implements BeanPostProcessor {

    /**
     * 这是init之前执行的方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

//        if (beanName.equals("testService")) {
//            LogUtil.warn("初始化准备......");
//        }


        if (bean instanceof TestService) {
            LogUtil.warn("初始化准备......在init方法之前处理一些操作");
            ((TestService) bean).setName("张三");
        }

        return bean;
    }

    /**
     * 这是init之后执行的方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof TestService) {
            LogUtil.warn("初始化完成......在init之后记录一些日志");
        }

        // Bean的后置处理器的作用主要是将创建出来的bean进行验证或者proxy，然后得到包装的bean，比如我们通过aop生成的代理类添加了很多前置、后置或环绕增强的机制，可用于数据脱敏操作
        // 我们可以在Spring配置文件中添加多个BeanPostProcessor(后置处理器)接口实现类，在默认情况下Spring容器会根据后置处理器的定义顺序来依次调用。
        return bean;
    }
}
