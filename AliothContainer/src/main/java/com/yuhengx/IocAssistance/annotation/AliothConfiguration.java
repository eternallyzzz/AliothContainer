package com.yuhengx.IocAssistance.annotation;

import java.lang.annotation.*;

/**
 * @author white
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AliothConfiguration {
    /**
     * @return value == "" ? "by type" : "by name"
     */
    String value() default "";
}
