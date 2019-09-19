package com.jacky.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 5:53 PM
 */
public class LinuxCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {

        return true;
//        Environment environment = conditionContext.getEnvironment();
//
//        String property = environment.getProperty("os.name");
//        if (property.contains("Linux")) {
//            return true;
//        }
//        return false;
    }
}