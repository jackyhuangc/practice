package com.jacky.annotation;

import com.jacky.common.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019-12-23 15:22
 */
@Component
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

        if (beanName.equals("testService")) {
            // 实例化阶段
            LogUtil.warn("实例化准备......");
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {

        if (beanName.equals("testService")) {
            // 实例化阶段
            LogUtil.warn("实例化完成......");
        }
        return true;
    }
}
