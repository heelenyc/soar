package com.heelenyc.soar.core.service.entity;

import java.util.List;


/**
 * 服务元数据
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public final class ServiceMetaData {

    private String serviStringURI;
    
    private List<ProviderMetaData> providerList;
    
    public String getServiStringURI() {
        return serviStringURI;
    }

    public void setServiStringURI(String serviStringURI) {
        this.serviStringURI = serviStringURI;
    }

    public List<ProviderMetaData> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<ProviderMetaData> providerList) {
        this.providerList = providerList;
    }
    
}
