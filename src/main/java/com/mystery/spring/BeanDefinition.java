package com.mystery.spring;

import com.mystery.spring.enumm.ScopeEnum;

/**
 * @author Mystery
 */
public class BeanDefinition {

    private Class<?> clazz;

    private ScopeEnum scope;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        this.scope = scope;
    }
}
