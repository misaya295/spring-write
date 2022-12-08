package com.misaya.service;


import com.spring.Component;

@Component
public class MisayaBeanPostProcesssor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforInitialization(Object bean, String beanName) {
        System.out.println("初始化前");

        if (bean.equals("userService")) {
            ((UserService) bean).setName("Misaya");
        }


        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        System.out.println("初始化后");
        return bean;
    }
}
