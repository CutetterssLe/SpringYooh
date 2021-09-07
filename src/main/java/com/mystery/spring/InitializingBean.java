package com.mystery.spring;

/**
 * @author Mystery
 */
public interface InitializingBean {
    /**
     * PostConstruct -> init-method -> afterPropertiesSet
     */
    void afterPropertiesSet();
}
