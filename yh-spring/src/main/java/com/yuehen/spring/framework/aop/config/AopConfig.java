package com.yuehen.spring.framework.aop.config;

import lombok.Data;

@Data
public class AopConfig {
    //切點
    private String pointCut;
    //代理前後方法，異常，類
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAround;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
