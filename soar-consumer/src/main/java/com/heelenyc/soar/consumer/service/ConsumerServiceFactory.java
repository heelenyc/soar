package com.heelenyc.soar.consumer.service;

import com.heelenyc.soar.core.service.ServiceFactory;

/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public class ConsumerServiceFactory extends ServiceFactory {

    @Override
    public void shutdown() {
        
    }

    public synchronized <T> T getInstance(String serviceURI){
        return null;
    }
}
