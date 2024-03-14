package com.yuhengx.IocAssistance.annotation;

import java.lang.annotation.*;

/**
 * @author white
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface AliothResource {}