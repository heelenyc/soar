package com.heelenyc.soar.core.service.entity;

/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public final class ProviderMetaData {

    private String IP;
    private int port;
    private int weight;
    
    public String getIP() {
        return IP;
    }
    public void setIP(String iP) {
        IP = iP;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
