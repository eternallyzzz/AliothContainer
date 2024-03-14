package com.yuhengx.IocAssistance.exception;

/**
 * @author white
 */
public class AliothBeanFactoryException extends Exception{
    private final String message;

    public AliothBeanFactoryException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public String getLocalizedMessage() {
        return getMessage();
    }
}
