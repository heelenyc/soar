package com.heelenyc.soar.consumer.service.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.heelenyc.soar.core.api.route.RouteService;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;

/**
 * 模板方法封装invoke的基本逻辑：获取soaclient，然后调用
 * @author yicheng
 * @since 2014年10月13日
 * 
 */
public abstract class AbstractInvocationHandler implements InvocationHandler {

    protected ConsumerServiceMetaData metaData ;
    protected RouteService routeService;
    
    public AbstractInvocationHandler(ConsumerServiceMetaData consumerServiceMetaData,RouteService aRouteService){
        metaData = consumerServiceMetaData;
        routeService = aRouteService;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Avoid remote invocation for toString method.
        if ("toString".equals(method.getName())) {
            return proxy.getClass().getName();
        }
        
        final String serviceURI = metaData.getServiStringURI();
        
        final String targetURI = routeService.getTargetProvider(serviceURI, seed);
        return doInvoke(method, args);
    }

}
