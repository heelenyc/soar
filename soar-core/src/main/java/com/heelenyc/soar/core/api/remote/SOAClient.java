package com.heelenyc.soar.core.api.remote;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public interface SOAClient {

    URI getTargetURI();
    /**
     * @param serviceURI
     * @param method
     * @param args
     * @param timeout
     * @return
     */
    Object invoke(String serviceURI, Method method, Object[] args, int timeout);

}
