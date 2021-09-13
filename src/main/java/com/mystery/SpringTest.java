package com.mystery;

import com.mystery.service.HelloService;
import com.mystery.spring.context.MysteryApplicationContext;

/**
 * @author Mystery
 */
public class SpringTest {

    public static void main(String[] args) {
        MysteryApplicationContext context = new MysteryApplicationContext(MysteryConfiguration.class);

        HelloService helloService = (HelloService) context.getBean("helloService");

        System.out.println(helloService.sayHello("张星"));
    }
}
