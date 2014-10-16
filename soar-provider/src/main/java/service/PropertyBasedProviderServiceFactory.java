package service;

import java.util.Map;

import com.heelenyc.soar.core.api.Constants;
import com.heelenyc.soar.core.common.utils.PropertiesUtil;

/**
 * 根据配置生成服务元数据，创建线程池，放到soacontext中共享
 * @author yicheng
 * @since 2014年10月16日
 *
 */
public class PropertyBasedProviderServiceFactory extends ProviderServiceFactory {

    /**
     * port 不在配置文件里指定，配置文件里指定都是与服务相关的
     * @param tcpPort
     */
    public PropertyBasedProviderServiceFactory(int tcpPort,String propertyFile) {
        super(tcpPort);
        //读取配置，创建服务实例
        PropertiesUtil props = new PropertiesUtil(propertyFile);
        
        Map<String, Object> serviceConfigs = props.getPropsByPrefix(Constants.CONFIG_SERVICE_PREFIX);
        
        
        for(String serviceUri : serviceConfigs.keySet()){
            
        }
    }

}
