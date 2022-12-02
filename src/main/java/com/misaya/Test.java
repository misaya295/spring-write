package com.misaya;

import com.misaya.service.UserService;
import com.spring.MisayaApplicationContext;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {


        MisayaApplicationContext applicationContext = new MisayaApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();

    }
}
