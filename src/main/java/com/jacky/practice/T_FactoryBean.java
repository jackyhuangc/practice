package com.jacky.practice;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

//@SpringBootApplication
public class T_FactoryBean {
    public T_FactoryBean(IPrint printProxy) {
        printProxy.execute(10, " log print ");
        printProxy.execute(0, " console print ");
    }

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(T_FactoryBean.class, args);

        IPrint iPrint = (IPrint) new SpiFactoryBean(null, IPrint.class).getObject();
        iPrint.execute(1, "xxxx", "xxx1");
        iPrint.execute(-1, "xxxx", "xxx1");
    }
}

interface ISpi<T> {
    boolean verify(T condition);

    /**
     * 排序，数字越小，优先级越高
     *
     * @return
     */
    default int order() {
        return 10;
    }
}

interface IPrint extends ISpi<Integer> {

    default void execute(Integer level, Object... msg) {
        print(msg.length > 0 ? (String) msg[0] : null);
    }

    void print(String msg);
}


class SpiFactoryBean<T> implements FactoryBean<T> {
    private Class<? extends ISpi> spiClz;

    private List<ISpi> list;

    public SpiFactoryBean(ApplicationContext applicationContext, Class<? extends ISpi> clz) {
        this.spiClz = clz;

        Map<String, ? extends ISpi> map = applicationContext.getBeansOfType(spiClz);
        list = new ArrayList<>(map.values());
        list.sort(Comparator.comparingInt(ISpi::order));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        // jdk动态代理类生成
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                for (ISpi spi : list) {
                    if (spi.verify(args[0])) {
                        // 第一个参数作为条件选择
                        return method.invoke(spi, args);
                    }
                }

                throw new Exception("no Spi server can execute! spiList: " + list);
            }
        };

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{spiClz},
                invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return spiClz;
    }
}

@Component
class ConsolePrint implements IPrint {
    @Override
    public void print(String msg) {
        System.out.println("console print: " + msg);
    }

    @Override
    public boolean verify(Integer condition) {
        return condition <= 0;
    }
}

@Slf4j
@Component
class LogPrint implements IPrint {
    @Override
    public void print(String msg) {
        log.info("log print: {}", msg);
    }

    @Override
    public boolean verify(Integer condition) {
        return condition > 0;
    }
}