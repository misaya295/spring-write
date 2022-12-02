package com.spring;

import com.misaya.service.BeanNameAware;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MisayaApplicationContext {

    private Class configClass;

    //单例池
    private ConcurrentHashMap<String, Object> singletonObject = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public MisayaApplicationContext(Class configClass) throws ClassNotFoundException {
        this.configClass = configClass;

        //解析配置类
        //componentScan注解---》扫描路径---》扫描 -->>Beandefinition--->BeandefinitionMap
         scan(configClass);

        beanDefinitionMap.entrySet().forEach(
                x -> {
                    String beanName = x.getKey();
                    BeanDefinition value = x.getValue();
                    if (value.getScope().equals("singleton")) {
                        //单例bean
                        Object bean = createBean(beanName,value);
                        singletonObject.put(beanName, bean);
                    }

                }
        );


    }

    public Object createBean(String beanName,BeanDefinition beanDefinition)  {

        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {

                    //属性赋值
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);

                }
            }


            //Aware回调
            if (instance instanceof BeanNameAware) {

                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //初始化



            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(Class configClass) throws ClassNotFoundException {
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


                Class<?> aClass = classLoader.loadClass(className);
                if (aClass.isAnnotationPresent(Component.class)) {
                    //当前这个类是一个bean

                    //解析类，判断当前bean是单例bean，还是prototype的bean
                    Component component = aClass.getDeclaredAnnotation(Component.class);
                    String BeanName = component.value();

                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClazz(aClass);
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
                Object bean = createBean(beanName,beanDefinition);
                return bean;

            }


        } else {
            //不存在对应的bean
            throw new NullPointerException();
        }

    }
}
