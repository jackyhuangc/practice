package com.jacky.practice;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;


class JUnitBaseUtil {
    private ClassPathXmlApplicationContext context;

    private String springXmlpath;

    public JUnitBaseUtil() {
    }

    public JUnitBaseUtil(String springXmlpath) {
        this.springXmlpath = springXmlpath;
    }

    @Before
    public void before() {
        if (StringUtils.isEmpty(springXmlpath)) {
            springXmlpath = "classpath*:spring-*.xml";
        }
        try {
            context = new ClassPathXmlApplicationContext(springXmlpath.split("[,\\s]+"));
            context.start();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        context.destroy();
    }

    @SuppressWarnings("unchecked")
    protected <T extends Object> T getBean(String beanId) {
        try {
            return (T) context.getBean(beanId);
        } catch (BeansException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected <T extends Object> T getBean(Class<T> clazz) {
        try {
            return context.getBean(clazz);
        } catch (BeansException e) {
            e.printStackTrace();
            return null;
        }
    }
}


@RunWith(BlockJUnit4ClassRunner.class)
public class T_JUnitBaseUtil extends JUnitBaseUtil {
    public T_JUnitBaseUtil() {
        super("classpath*:config/spring-beanannotation.xml");
    }

    //@Test
    public void testSay() {
        BeanAnnotation beanAn = super.getBean("beanAnnotation");
        //Component("beanID")
        //BeanAnnotation beanAn = super.getBean("beanID");
        beanAn.say("this is component test.");
    }

    @Test
    public void testHash() {
        //第一次从bean中get一个hashcode
        BeanAnnotation beanAn = super.getBean("beanAnnotation");
        beanAn.scope();
        //再次从bean中get一个hashcode
        beanAn = super.getBean("beanAnnotation");
        beanAn.scope();
    }

}
