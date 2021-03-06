package com.yuehen.spring.demo.aspect;

import com.yuehen.spring.framework.aop.aspect.JoinPoint;
import com.yuehen.spring.framework.aop.intercept.MethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(JoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //在调用一个方法之后，执行after方法
    public void after(JoinPoint joinPoint){
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void around(JoinPoint joinPoint) {
        log.info("Invoker before around Method!!! TargetObject:" +  joinPoint.getThis() +
                " Args:" + Arrays.toString(joinPoint.getArguments()));
        MethodInvocation methodInvocation = (MethodInvocation)joinPoint;
        try {
            methodInvocation.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        log.info("Invoker after around Method!!! TargetObject:" +  joinPoint.getThis() +
                " Args:" + Arrays.toString(joinPoint.getArguments()));

    }

    public void afterThrowing(JoinPoint joinPoint, Throwable ex){
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }

}
