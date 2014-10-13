package com.heelenyc.soar.consumer.service;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import com.heelenyc.soar.core.CASBasedSOAContext;
import com.heelenyc.soar.core.api.Constants;
import com.heelenyc.soar.core.api.SOAContext;
import com.heelenyc.soar.core.service.ServiceFactory;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;
import com.heelenyc.soar.core.service.entity.ProviderServiceMetaData;

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
        final ConsumerServiceMetaData priverMetaData = soaContext.get(Constants.KEY_STORE_CONSUMER_METADATAS,serviceURI);
        
        InvocationHandler handler = null;
        
        return null;
    }
}
