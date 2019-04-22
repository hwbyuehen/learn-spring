package com.yuehen.spring.framework.aop.aspect;

import com.yuehen.spring.framework.aop.intercept.MethodInterceptor;
import com.yuehen.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class MethodAroundAdviceInterceptor extends AbstractAspectAdvice implements Advice,MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodAroundAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.around(mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void around(Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}
