package com.mystery.spring.context;

import com.mystery.spring.BeanDefinition;
import com.mystery.spring.BeanPostProcessor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Mystery
 */
public class MysteryApplicationContext {

    private static ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private static List<BeanPostProcessor> beanPostProcessorList = new CopyOnWriteArrayList<>();

    public MysteryApplicationContext(Class<?> configClass) {
        //1、扫描得到beanDefinition对象
        scan(configClass);

        //实例化非懒加载单例bean
        // 1、实例化
        // 2、属性填充
        // 3、Aware回调
        // 4、初始化
        // 5、添加到单例池
        instanceSingletonBean();

    }

    public void scan(Class<?> configClass) {

    }

    public void instanceSingletonBean() {
        if (!beanDefinitionMap.isEmpty()) {

        }
    }
}
