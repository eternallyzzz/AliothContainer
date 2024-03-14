package com.yuhengx.IocAssistance.factory;

import com.yuhengx.IocAssistance.context.AliothContainer;

import java.util.List;

/**
 * @author white
 */
public class AliothBeanContainer implements AliothContainer {
    protected AliothBeanContainer() {}

    @Override
    public List<Object> beans() {
        return AliothBeanFactory.getBeans();
    }

    /**
     * @param beanName bean name
     * @return object
     */
    @Override
    public Object getBean(String beanName) {
        return AliothBeanFactory.getBean(null, beanName);
    }

    /**
     * @param clazz bean type
     * @return object
     */
    @Override
    public <T> T getBean(Class<T> clazz) {
        return (T) AliothBeanFactory.getBean(clazz, null);
    }

    /**
     * @param clazz bean type
     * @param beanName bean name
     * @return object
     */
    @Override
    public <T> T getBean(Class<T> clazz, String beanName) {
        return (T) AliothBeanFactory.getBean(clazz, beanName);
    }
}
