package com.mystery.service;

import com.mystery.service.HelloService;
import com.mystery.spring.annotation.Component;

/**
 * @author Mystery
 */
@Component("helloService")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "你好，" + name;
    }
}
