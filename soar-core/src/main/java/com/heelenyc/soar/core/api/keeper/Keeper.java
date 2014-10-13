package com.heelenyc.soar.core.api.keeper;

import com.heelenyc.soar.core.service.entity.ProviderMetaData;
import com.heelenyc.soar.core.service.entity.ProviderServiceMetaData;


/**
 * keeper 配置操作接口类
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public interface Keeper {

    boolean publishService(String serviceURI,ProviderMetaData metaData);
    
    boolean republishService(String serviceURI,ProviderMetaData metaData);
    
    ProviderServiceMetaData queryService(String serviceURI);
    
}
