package com.heelenyc.soar.consumer.route;

import java.util.List;

import com.heelenyc.soar.core.api.route.RouteService;
import com.heelenyc.soar.core.keeper.SoarKeeper;
import com.heelenyc.soar.core.service.entity.ProviderMetaData;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public abstract class AbstractRouteService implements RouteService {

    private SoarKeeper keeper = SoarKeeper.getInstance();
    
    @Override
    public List<ProviderMetaData> getProviderList(String serviceURI) {
        return keeper.queryService(serviceURI).getProviderList();
    }

}
