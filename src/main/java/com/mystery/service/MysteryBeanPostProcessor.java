package com.mystery.service;

import com.mystery.spring.BeanPostProcessor;
import com.mystery.spring.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Mystery
 */
@Component
public class MysteryBeanPostProcessor implements BeanPostProcessor {

    private static final String NAME = "helloService";


    @Override
    public Object postProcessorBeforeInitialization(String beanName, Object bean) {
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(String beanName, Object bean) {
        if (NAME.equals(beanName)) {
            return Proxy.newProxyInstance(MysteryBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy1, Method method, Object[] args) throws Throwable {
                    System.out.println(beanName + "切面逻辑执行......");
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }
}
