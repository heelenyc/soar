package com.heelenyc.soar.consumer.remote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.heelenyc.soar.core.api.remote.SOAClient;

/**
 * 维护所有的通行client，每个client与serviceuri一一对应
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public class SOAClientFactory {

    private static Lock lock = new ReentrantLock();
    private static Map<String, SOAClient> clientsMap = new ConcurrentHashMap<String, SOAClient>();
    
    public static SOAClient getInstance(String serviceURI){
        return null;
    }
}
