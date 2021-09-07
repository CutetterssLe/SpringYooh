package com.mystery.spring;

/**
 * @author Mystery
 * AOP操作
 */
public interface BeanPostProcessor {
    /**
     * 初始化前
     * @param beanName
     * @param bean
     * @return
     */
    Object postProcessorBeforeInitialization(String beanName, Object bean);

    /**
     * 初始化后
     * @param beanName
     * @param bean
     * @return
     */
    Object postProcessorAfterInitialization(String beanName, Object bean);
}
