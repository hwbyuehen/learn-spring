package com.yuehen.spring.framework.context;

import com.yuehen.spring.framework.annotation.Autowired;
import com.yuehen.spring.framework.annotation.Controller;
import com.yuehen.spring.framework.annotation.Service;
import com.yuehen.spring.framework.aop.AopProxy;
import com.yuehen.spring.framework.aop.CglibAopProxy;
import com.yuehen.spring.framework.aop.JdkDynamicAopProxy;
import com.yuehen.spring.framework.aop.config.AopConfig;
import com.yuehen.spring.framework.aop.support.AdvisedSupport;
import com.yuehen.spring.framework.beans.BeanWrapper;
import com.yuehen.spring.framework.beans.config.BeanDefinition;
import com.yuehen.spring.framework.beans.config.BeanPostProcessor;
import com.yuehen.spring.framework.beans.support.BeanDefinitionReader;
import com.yuehen.spring.framework.beans.support.DefaultListableBeanFactory;
import com.yuehen.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC核心容器
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {
    private String[] configLocations;
    private BeanDefinitionReader reader;

    //单例的ioc容器缓存
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    //通用的IOC容器
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //前面两步用BeanDefinitionReader解析器解析
        //1.定位配置文件：加载扫描包下的类文件
        reader = new BeanDefinitionReader(configLocations);
        
        //2.把扫描到的类封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinition();
        
        //3.注册，把配置信息放到容器里面（ioc容器）
        doRegistryBeanDefinition(beanDefinitions);
        
        //4.把不是延迟加载的类提前初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegistryBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //容器初始化完毕
    }


    /**
     * 依赖注入，从这里开始通过读取BeanDefinition信息，通过反射机制创建一个实例返回
     * Spring做法不会把原始的对象返回，而是会用BeanWrapper进行一次包装
     * 装饰器模式：
     * 1.保留原来OOP关系
     * 2.对原对象的扩展，功能增强（为后面aop扩展做准备）
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Object instance = null;

        //应该 采用工厂模式+策略模式
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        //1.创建对象实例
        BeanWrapper beanWrapper = instantiateBean(beanName, beanDefinition);
        
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        
        beanPostProcessor.postProcessAfterInitialization(instance, beanName);
        
        //2.注入
        populateBean(beanName, beanDefinition, beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 针对有Controller和Service注解的类执行注入，获取类中Autowire注解的field，设置beanWrapper中instance对象中对应的字段值。
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();

        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的才能执行注入
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)){return;}

            Autowired autowired = field.getAnnotation(Autowired.class);
            
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            
            field.setAccessible(true);
            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    getBean(autowiredBeanName);
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实例化对象并返回
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private BeanWrapper instantiateBean(String beanName, BeanDefinition beanDefinition) {
        //1.拿到对象的类名
        String className = beanDefinition.getBeanClassName();
        
        //2.反射实例化对象
        Object instance = null;
        try {
            //增加ioc容器缓存
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //aop：做aop功能增強
                AdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的規則
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className, instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BeanWrapper(instance);
    }


    private AopProxy createProxy(AdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
