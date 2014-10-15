package com.heelenyc.soar.consumer.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.soar.core.api.Constants;
import com.heelenyc.soar.core.common.utils.NetUtil;
import com.heelenyc.soar.core.common.utils.PropertiesUtil;
import com.heelenyc.soar.core.exception.InitializationException;
import com.heelenyc.soar.core.service.entity.ConsumerServiceMetaData;

/**
 * @author yicheng
 * @since 2014年10月14日
 * 
 */
public class PropertyBasedConsumerServiceFactory extends ConsumerServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PropertyBasedConsumerServiceFactory.class);

    @SuppressWarnings("rawtypes")
    public PropertyBasedConsumerServiceFactory(String propertyFile) throws InitializationException {
        PropertiesUtil props = new PropertiesUtil(propertyFile);
        
        //遍历每个服务的配置，写入上下文
        Map<String, Object> serviceConfigs = props.getPropsByPrefix(Constants.CONFIG_SERVICE_PREFIX);
        for(String serviceURI : serviceConfigs.keySet()){
            try {
                Map serviceConfig = (Map)serviceConfigs.get(serviceURI);
                // 接口名称
                String interfaceName = serviceConfig.get(Constants.CONFIG_INTERFACE).toString();
                ConsumerServiceMetaData consumerServiceMetaData = new ConsumerServiceMetaData(serviceURI,interfaceName);
                
                // timeout
                consumerServiceMetaData.setTimeout(Constants.DEFAULT_TIMEOUT_INMS);
                
                // 本机ip
                consumerServiceMetaData.setIp(NetUtil.getLocalIPAddress());
                
                // 在
                registerService(consumerServiceMetaData);
                
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                throw new InitializationException(serviceURI);
            }
        }
        

    }
}
