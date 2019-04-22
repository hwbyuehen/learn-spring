package com.yuehen.spring.framework.core;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
public interface BeanFactory {
    /**
     * 从ioc容器中获得一个bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;
    
    Object getBean(Class<?> beanClass) throws Exception;
}
