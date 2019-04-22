package com.yuehen.spring.framework.aop;

public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
