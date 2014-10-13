package com.heelenyc.soar.core.api;

import java.util.Map;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public interface SOAContext {
    /**
     * 检查存在性。
     */
    boolean exists(String dataSetKey, String key);

    /**
     * 读取数据。
     */
    Map<String, Object> get(String dataSetKey);

    /**
     * 读取数据。
     */
    <T> T get(String dataSetKey, String key);

    /**
     * 存储数据。
     */
    void put(String dataSetKey, String key, Object value);

    /**
     * 删除数据。
     */
    void remove(String dataSetKey, String key);
}
