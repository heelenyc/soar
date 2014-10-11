package com.heelenyc.soar.keeper.dao.mongo;

import com.heelenyc.soar.core.service.entity.ServiceMetaData;
import com.heelenyc.soar.keeper.common.MongoCollection;
import com.heelenyc.soar.keeper.common.MongoCollectionFactory;

/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public class ServiceManagerDao {

    private MongoCollection<ServiceMetaData> serviceDao = MongoCollectionFactory.soarKeeper.service(ServiceMetaData.class);

    
}
