package com.yuhengx.IocAssistance.context;


import java.util.List;

/**
 * @author white
 */
public interface AliothContainer {

    List<Object> beans();

    Object getBean(String beanName);

    <T> T getBean(Class<T> clazz);

    <T> T getBean(Class<T> clazz, String beanName);
}
