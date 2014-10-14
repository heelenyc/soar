package com.heelenyc.soar.consumer.service.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.heelenyc.soar.consumer.remote.SOAClientFactory;
import com.heelenyc.soar.core.api.remote.SOAClient;
import com.heelenyc.soar.core.api.route.RouteService;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;
import com.heelenyc.soar.core.service.entity.ProviderMetaData;

/**
 * 模板方法封装invoke的基本逻辑：获取soaclient，然后调用
 * @author yicheng
 * @since 2014年10月13日
 * 
 */
public abstract class AbstractInvocationHandler implements InvocationHandler {

    protected ConsumerServiceMetaData consumerMetaData ;
    protected RouteService routeService;
    
    public AbstractInvocationHandler(ConsumerServiceMetaData metaData,RouteService aRouteService){
        consumerMetaData = metaData;
        routeService = aRouteService;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Avoid remote invocation for toString method.
        if ("toString".equals(method.getName())) {
            return proxy.getClass().getName();
        }
        
        final String serviceURI = consumerMetaData.getServiceURI();
        
        final ProviderMetaData providerMetaData = routeService.getTargetProvider(serviceURI,consumerMetaData);
        
        SOAClient soaClient = SOAClientFactory.getInstance(providerMetaData.getTargetURI());
        
        return doInvoke(soaClient,method, args);
    }

    /**
     * @param soaClient
     * @param method
     * @param args
     * @return
     */
    abstract Object doInvoke(SOAClient soaClient, Method method, Object[] args) ;

}
