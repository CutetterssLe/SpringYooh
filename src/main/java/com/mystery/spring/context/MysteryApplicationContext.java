package com.mystery.spring.context;

import com.mystery.spring.BeanDefinition;
import com.mystery.spring.BeanPostProcessor;
import com.mystery.spring.InitializingBean;
import com.mystery.spring.annotation.Autowired;
import com.mystery.spring.annotation.Component;
import com.mystery.spring.annotation.ComponentScan;
import com.mystery.spring.annotation.Scope;
import com.mystery.spring.aware.BeanNameAware;
import com.mystery.spring.enumm.ScopeEnum;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
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
        //扫描class，转为BeanDefinition存入map中
        ComponentScan scan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        //得到包路径
        String path = scan.value();
        //扫描包路径得到classList
        List<Class<?>> classList = getBeanClass(path);

        assert classList != null;

        for (Class<?> clazz : classList) {
            if (clazz.isAnnotationPresent(Component.class)) {
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setClazz(clazz);

                //要么spring自动生成，要么component获取
                Component component = clazz.getAnnotation(Component.class);

                String beanName = component.value();

                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    try {
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(beanPostProcessor);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException
                            | SecurityException e) {
                        e.printStackTrace();
                    }
                }

                //解析scope
                if (clazz.isAnnotationPresent(Scope.class)) {
                    Scope scope = clazz.getAnnotation(Scope.class);

                    String scopeValue = scope.value();

                    if (ScopeEnum.SINGLETON.name().equals(scopeValue)) {
                        beanDefinition.setScope(ScopeEnum.SINGLETON);
                    } else {
                        beanDefinition.setScope(ScopeEnum.PROTOTYPE);
                    }
                } else {
                    beanDefinition.setScope(ScopeEnum.SINGLETON);
                }
                beanDefinitionMap.put(beanName, beanDefinition);
            }
        }
    }

    public List<Class<?>> getBeanClass(String path) {
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader classLoader = MysteryApplicationContext.class.getClassLoader();
        String realPath = path.replace(".", "/");
        URL resource = classLoader.getResource(realPath);
        assert resource != null;
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("/", ".");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        classList.add(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classList;
    }

    public void instanceSingletonBean() {
        if (!beanDefinitionMap.isEmpty()) {
            for (String beanName : beanDefinitionMap.keySet()) {
                //取出beanDefinition
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                //实例化beanDefinition为bean
                if (beanDefinition.getScope().equals(ScopeEnum.SINGLETON)) {
                    Object bean = doCreateBean(beanName, beanDefinition);
                    //放入单例缓存池
                    singletonObjects.putIfAbsent(beanName, bean);
                }
            }
        }
    }

    public Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            Object instance = declaredConstructor.newInstance();

            //属性填充
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    String name = field.getName();
                    Object bean = getBean(name);
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }

            //Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }
            // ....各种Aware

            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorAfterInitialization(beanName, instance);
            }

            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getBean(String beanName) {
        if (singletonObjects.containsKey(beanName)) {
            return singletonObjects.get(beanName);
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        return doCreateBean(beanName, beanDefinition);

    }

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> aClass = classLoader.loadClass("com.mystery.service.HelloService");
        System.out.println(aClass);
    }
}
