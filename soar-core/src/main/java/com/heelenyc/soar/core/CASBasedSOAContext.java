package com.heelenyc.soar.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.heelenyc.soar.core.api.SOAContext;

/**
 * 基于concurrent包ConcurrentHashMap构建的soacontext，线程安全，但是不保证读写的一致性 </br> 单例模式
 * 
 * @author yicheng
 * @since 2014年10月13日
 * 
 */
public class CASBasedSOAContext implements SOAContext {

    private static SOAContext instance = new CASBasedSOAContext();

    public static SOAContext getInstance() {
        return instance;
    }

    private ConcurrentMap<String, Map<String, Object>> datas = new ConcurrentHashMap<String, Map<String, Object>>();

    private CASBasedSOAContext() {
    }

    @Override
    public boolean exists(String dataSetKey, String key) {
        if (!datas.containsKey(dataSetKey)) {
            return false;
        }

        return datas.get(dataSetKey).containsKey(key);
    }

    @Override
    public Map<String, Object> get(String dataSetKey) {
        if (!datas.containsKey(dataSetKey)) {
            return null;
        }

        return Collections.unmodifiableMap(datas.get(dataSetKey));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String dataSetKey, String key) {
        if (!datas.containsKey(dataSetKey)) {
            return null;
        }

        return (T) datas.get(dataSetKey).get(key);
    }

    @Override
    public void put(String dataSetKey, String key, Object value) {
        if (!datas.containsKey(dataSetKey)) {
            Map<String, Object> dataSet = new HashMap<String, Object>();
            datas.put(dataSetKey, dataSet);
        }

        Map<String, Object> map = datas.get(dataSetKey);
        synchronized (map) {
            map.put(key, value);
        }
    }

    @Override
    public void remove(String dataSetKey, String key) {
        if (!datas.containsKey(dataSetKey)) {
            return;
        }

        Map<String, Object> map = datas.get(dataSetKey);
        synchronized (map) {
            map.remove(key);
        }
    }

}
