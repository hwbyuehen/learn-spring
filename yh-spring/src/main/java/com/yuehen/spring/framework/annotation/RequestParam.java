package com.yuehen.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
