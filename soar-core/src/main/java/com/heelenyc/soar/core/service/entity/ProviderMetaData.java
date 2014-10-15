package com.heelenyc.soar.core.service.entity;

import com.heelenyc.soar.core.api.Constants;

/**
 * @author yicheng
 * @since 2014年10月11日
 * 
 */
public final class ProviderMetaData extends ServiceMetaData {

    private String ip;
    private int port;
    private int weight;

    public String getIP() {
        return ip;
    }

    public void setIP(String iP) {
        ip = iP;
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

    /**
     * @return
     */
    public String getTargetURI() {
        return String.format(Constants.TARGETURI_FORMAT, getIP(), getPort(), getServiceURI());
    }
}
