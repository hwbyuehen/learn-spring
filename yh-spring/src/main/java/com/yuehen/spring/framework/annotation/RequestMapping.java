package com.yuehen.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
