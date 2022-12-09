package com.misaya.service;


import com.spring.Autowired;
import com.spring.Component;
import com.spring.Scope;

/**
 * @author chenwenkang
 */
@Component("userService")
@Scope("prototype")
public class UserServiceImp implements BeanNameAware,InitializingBean,UserService{



    @Autowired
    private OrderService orderService;


    private String beanName;

    private String name;


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void test() {
        System.out.println(orderService);
        System.out.println(beanName);

    }


    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        //
        System.out.println("初始化");

    }
}
