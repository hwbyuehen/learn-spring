package com.yuehen.spring.framework.beans;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/18
 */
public class BeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;
    
    public BeanWrapper(Object instance) {
        this.wrappedInstance = instance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }
}
