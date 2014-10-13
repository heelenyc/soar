package com.heelenyc.soar.core.keeper.dao.mongo;

import com.heelenyc.soar.core.common.MongoCollection;
import com.heelenyc.soar.core.common.MongoCollectionFactory;
import com.heelenyc.soar.core.service.entity.ProviderServiceMetaData;

/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public class ServiceManagerDao {

    private MongoCollection<ProviderServiceMetaData> serviceDao = MongoCollectionFactory.soarKeeper.service(ProviderServiceMetaData.class);

    
}
