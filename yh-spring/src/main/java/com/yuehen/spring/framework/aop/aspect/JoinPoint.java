package com.yuehen.spring.framework.aop.aspect;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
