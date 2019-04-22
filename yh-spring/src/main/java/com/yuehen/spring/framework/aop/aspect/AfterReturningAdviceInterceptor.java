package com.yuehen.spring.framework.aop.aspect;

import com.yuehen.spring.framework.aop.aspect.AbstractAspectAdvice;
import com.yuehen.spring.framework.aop.aspect.Advice;
import com.yuehen.spring.framework.aop.aspect.JoinPoint;
import com.yuehen.spring.framework.aop.intercept.MethodInterceptor;
import com.yuehen.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class AfterReturningAdviceInterceptor extends AbstractAspectAdvice implements Advice,MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
