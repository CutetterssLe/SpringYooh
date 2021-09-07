package com.mystery.spring.aware;

/**
 * @author Mystery
 */
public interface BeanNameAware {
    /**
     * 设置beanName
     * @param beanName
     */
    void setBeanName(String beanName);
}
