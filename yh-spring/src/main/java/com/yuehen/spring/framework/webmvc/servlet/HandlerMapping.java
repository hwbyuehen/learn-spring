package com.yuehen.spring.framework.webmvc.servlet;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Getter
@Setter
public class HandlerMapping {
    private Pattern pattern;
    private Object controller;
    private Method method;

    public HandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

}
