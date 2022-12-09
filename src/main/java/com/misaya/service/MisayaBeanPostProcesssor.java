package com.misaya.service;


import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MisayaBeanPostProcesssor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforInitialization(Object bean, String beanName) {
//        System.out.println("初始化前");
//
//        if (bean.equals("userService")) {
//            ((UserService) bean).setName("Misaya");
//        }


        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        System.out.println("初始化后");
        if (beanName.equals("userService")) {
            //动态代理
            Object proxyInstance = Proxy.newProxyInstance(MisayaBeanPostProcesssor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    System.out.println("代理逻辑"); //找切点，通过反射执行
                    return method.invoke(bean, args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
