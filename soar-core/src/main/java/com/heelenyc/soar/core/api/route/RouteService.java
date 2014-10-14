package com.heelenyc.soar.core.api.route;

import java.util.List;

import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;
import com.heelenyc.soar.core.service.entity.ProviderMetaData;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public interface RouteService {

    List<ProviderMetaData> getProviderList(String serviceUri);

    ProviderMetaData getTargetProvider(String serviceUri,ConsumerServiceMetaData consumerMetaData) throws Throwable;
}
