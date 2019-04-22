package com.yuehen.spring.framework.beans.support;

import com.yuehen.spring.framework.beans.config.BeanDefinition;
import com.yuehen.spring.framework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
}
