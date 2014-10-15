package com.heelenyc.soar.consumer.remote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.soar.core.api.remote.SOAClient;

/**
 * 维护所有的通行client，每个client与target serviceuri一一对应
 * 
 * @author yicheng
 * @since 2014年10月13日
 * 
 */
public class SOAClientFactory {

    private static Logger logger =  LoggerFactory.getLogger(SOAClientFactory.class);
    private static Lock lock = new ReentrantLock();
    private static Map<String, SOAClient> clientsMap = new ConcurrentHashMap<String, SOAClient>();

    /**
     * 如果没有，创建通信客户端，否则重用
     * 
     * @param targetURI
     * @return
     */
    public static SOAClient getInstance(String targetURI) {
        if (clientsMap.containsKey(targetURI)) {
            return clientsMap.get(targetURI);
        }
        
        lock.lock();

        try {
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return null;
    }
}
