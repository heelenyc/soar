package com.heelenyc.soar.consumer.service.invocationhandler;

import java.lang.reflect.Method;

import com.heelenyc.soar.core.api.remote.SOAClient;
import com.heelenyc.soar.core.api.route.RouteService;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;

/**
 * @author yicheng
 * @since 2014年10月13日
 * 
 */
public class DefaultInvocationHandler extends AbstractInvocationHandler {

    /**
     * @param metaData
     * @param aRouteService
     */
    public DefaultInvocationHandler(ConsumerServiceMetaData metaData, RouteService aRouteService) {
        super(metaData, aRouteService);
    }

    @Override
    Object doInvoke(SOAClient soaClient, Method method, Object[] args) {
        return soaClient.invoke(consumerMetaData.getServiceURI(), method, args, consumerMetaData.getTimeout());
    }

}
