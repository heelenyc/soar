package com.heelenyc.soar.core.service.entity;

import java.util.List;


/**
 * 服务元数据
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public final class ProviderServiceMetaData extends ServiceMetaData {

    
    private List<ProviderMetaData> providerList;

    public List<ProviderMetaData> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<ProviderMetaData> providerList) {
        this.providerList = providerList;
    }
    
}
