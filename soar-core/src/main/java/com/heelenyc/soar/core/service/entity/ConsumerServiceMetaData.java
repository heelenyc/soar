package com.heelenyc.soar.core.service.entity;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public class ConsumerServiceMetaData extends ServiceMetaData {

    /**
     * 主机ip或者host
     */
    private String ip;
    private int timeout; // 超时时间
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
}
