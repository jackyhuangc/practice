package com.jacky.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/30 11:42 AM
 */
@Aspect
@Component
public class AspectService {

    /**
     * 验证方法切点
     */
    @Pointcut("execution(* com.jacky.annotation.UserService.*(..))")
    public void pointcut() {
        // 只是切点定义，不需要实现
    }

    @Around("pointcut()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        if (objects != null && objects.length > 0) {
            for (Object o : objects) {
                // 拦截方法调用前做些检查
                //checkField(o);
            }
        }
        return joinPoint.proceed();
    }
}
