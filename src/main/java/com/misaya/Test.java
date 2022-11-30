package com.misaya;

import com.spring.MisayaApplicationContext;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {


        MisayaApplicationContext applicationContext = new MisayaApplicationContext(AppConfig.class);

        Object userservice = applicationContext.getBean("userservice");
    }
}
