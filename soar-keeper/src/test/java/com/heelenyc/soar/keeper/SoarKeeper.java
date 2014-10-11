package com.heelenyc.soar.keeper;

import com.heelenyc.soar.core.service.entity.ProviderMetaData;
import com.heelenyc.soar.core.service.entity.ServiceMetaData;
import com.heelenyc.soar.keeper.api.Keeper;
import com.heelenyc.soar.keeper.dao.mongo.ServiceManagerDao;


/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public class SoarKeeper implements Keeper {

    private ServiceManagerDao dao = new ServiceManagerDao();
    
    @Override
    public boolean publishService(String serviceURI,ProviderMetaData metaData) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean republishService(String serviceURI,ProviderMetaData metaData) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ServiceMetaData queryService(String serviceURI) {
        // TODO Auto-generated method stub
        return null;
    }

}
