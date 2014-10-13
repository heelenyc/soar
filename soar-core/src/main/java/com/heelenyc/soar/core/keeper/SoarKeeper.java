package com.heelenyc.soar.core.keeper;

import com.heelenyc.soar.core.api.keeper.Keeper;
import com.heelenyc.soar.core.keeper.dao.mongo.ServiceManagerDao;
import com.heelenyc.soar.core.service.entity.ProviderMetaData;
import com.heelenyc.soar.core.service.entity.ProviderServiceMetaData;


/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public class SoarKeeper implements Keeper {
    
    private static SoarKeeper keeper = new SoarKeeper();
    
    private SoarKeeper(){
    }
    
    public static SoarKeeper getInstance(){
        return keeper;
    }

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
    public ProviderServiceMetaData queryService(String serviceURI) {
        // TODO Auto-generated method stub
        return null;
    }

}
