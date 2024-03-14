package com.yuhengx.IocAssistance.annotation;

import java.lang.annotation.*;

/**
 * @author white
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Documented
public @interface AliothValue {
    String[] value();
}
