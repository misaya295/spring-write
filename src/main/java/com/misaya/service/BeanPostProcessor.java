package com.misaya.service;

public interface BeanPostProcessor {

    Object postProcessBeforInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);

}
