package com.site.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 记录用户操作哪个模块
     */
    String title() default "";

    /**
     * 记录用户操作的动作
     */
    String action() default  "";
}
