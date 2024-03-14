package com.yuhengx.IocAssistance.factory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author white
 */
public class AliothInit {
    private static final AliothInit ALIOTH_INIT = new AliothInit();
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private AliothInit() {
        AliothBeanFactory.init();
    }



    public static AliothInit getAlioth() {
        if (INITIALIZED.compareAndSet(false, true)) {
            return ALIOTH_INIT;
        }
        return null;
    }

    public static void aliothInit() {
        if (INITIALIZED.compareAndSet(false, true)) {
            AliothBeanFactory.init();
        }
    }
}
