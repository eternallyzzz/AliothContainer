package com.yuhengx.IocAssistance.annotation;

import java.lang.annotation.*;

/**
 * @author white
 */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AliothBean {
}
