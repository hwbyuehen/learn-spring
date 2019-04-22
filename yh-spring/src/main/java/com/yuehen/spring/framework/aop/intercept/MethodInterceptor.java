package com.yuehen.spring.framework.aop.intercept;

/**
 * 方法攔截器
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
