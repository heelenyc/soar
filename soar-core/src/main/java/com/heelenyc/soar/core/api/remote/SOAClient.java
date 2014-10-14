package com.heelenyc.soar.core.api.remote;

import java.lang.reflect.Method;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public interface SOAClient {

    /**
     * @param serviceURI
     * @param method
     * @param args
     * @param timeout
     * @return
     */
    Object invoke(String serviceURI, Method method, Object[] args, int timeout);

}
