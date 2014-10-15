package com.heelenyc.soar.consumer.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.heelenyc.soar.consumer.route.RandomRouteService;
import com.heelenyc.soar.consumer.service.invocationhandler.DefaultInvocationHandler;
import com.heelenyc.soar.core.CASBasedSOAContext;
import com.heelenyc.soar.core.api.Constants;
import com.heelenyc.soar.core.api.SOAContext;
import com.heelenyc.soar.core.api.route.RouteService;
import com.heelenyc.soar.core.service.ServiceFactory;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;

/**
 * @author yicheng
 * @since 2014年10月11日
 * 
 */
public class ConsumerServiceFactory extends ServiceFactory {

    private Map<String, Object> proxies = new HashMap<String, Object>();

    @Override
    public void shutdown() {

    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getInstance(String serviceURI) {
        if (proxies.containsKey(serviceURI)) {
            return (T) proxies.get(serviceURI);
        }

        // proxies not contain a proxy for serviceURI
        SOAContext soaContext = CASBasedSOAContext.getInstance();
        final ConsumerServiceMetaData consumerServiceMetaData = soaContext.get(Constants.CONSUMER_SERVICE_METADATA, serviceURI);

        InvocationHandler handler = null;
        RouteService routeService = new RandomRouteService();

        // 可以根据配置决定建什么handler和router
        handler = new DefaultInvocationHandler(consumerServiceMetaData, routeService);

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { consumerServiceMetaData.getServiceInterface() }, handler);
        proxies.put(serviceURI, proxy);
        
        return (T)proxies.get(serviceURI);
    }
    
    public void registerService(ConsumerServiceMetaData consumerServiceMetaData){
        SOAContext soaContext = CASBasedSOAContext.getInstance();
        soaContext.put(Constants.CONSUMER_SERVICE_METADATA, consumerServiceMetaData.getServiceURI(), consumerServiceMetaData);
    }
}
