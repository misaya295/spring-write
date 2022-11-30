package com.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MisayaApplicationContext {

    private Class configClass;

    //单例池
    private ConcurrentHashMap<String, Object> singletonObject = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public MisayaApplicationContext(Class configClass) throws ClassNotFoundException {
        this.configClass = configClass;

        //解析配置类
        //componentScan注解---》扫描路径---》扫描
        ComponentScan declaredAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = declaredAnnotation.value();
        System.out.println(value);


        //扫描
        //bootstrap --->jre/lib
        //ext---->jre/ext/lib
        //app----->classpath--->
        ClassLoader classLoader = MisayaApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource("com/misaya/service");
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String absolutePath = f.getAbsolutePath();
                String className = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                className = className.replace("/", ".");


                Class<?> aClass = classLoader.loadClass("com.misaya.service.UserService");
                if (aClass.isAnnotationPresent(Component.class)) {
                    //当前这个类是一个bean

                    //解析类，判断当前bean是单例bean，还是prototype的bean
                    Component component = aClass.getDeclaredAnnotation(Component.class);
                    String BeanName = component.value();

                    BeanDefinition beanDefinition = new BeanDefinition();
                    if (aClass.isAnnotationPresent(Scope.class)) {
                        Scope scope = aClass.getDeclaredAnnotation(Scope.class);
                        beanDefinition.setScope(scope.value());

                    } else {
                        beanDefinition.setScope("singleton");
                    }

                    //扫描到所有类的定义
                    beanDefinitionMap.put(BeanName, beanDefinition);

                }

            }


        }


    }

    public Object getBean(String beanName) {

        if (beanDefinitionMap.containsKey(beanName)) {

            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object o = singletonObject.get(beanName);
                return o;
            } else {
                // 创建bean对像

            }


        } else {
            //不存在对应的bean
            throw new NullPointerException();
        }
        return null;
    }
}
