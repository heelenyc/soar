package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import remote.SOAServerTcp;

import com.heelenyc.soar.core.service.ServiceFactory;

/**
 * @author yicheng
 * @since 2014年10月16日
 *
 */
public class ProviderServiceFactory extends ServiceFactory {

    private Logger logger = LoggerFactory.getLogger(ProviderServiceFactory.class);
    
    @Override
    public void shutdown() {
        
    }

    public ProviderServiceFactory(int tcpPort){
        // 创建通信服务端
        try {
            new SOAServerTcp(tcpPort,this).start();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
