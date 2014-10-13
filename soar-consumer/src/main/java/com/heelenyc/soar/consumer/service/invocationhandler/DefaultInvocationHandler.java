package com.heelenyc.soar.consumer.service.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public class DefaultInvocationHandler extends AbstractInvocationHandler {

    @Override
    protected Object doInvoke(Method method, Object[] args) throws Throwable {
        return method.invoke(getTargetImpObj(),args);
    }


}
