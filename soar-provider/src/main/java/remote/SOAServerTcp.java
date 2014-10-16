package remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.ProviderServiceFactory;

import com.heelenyc.soar.core.api.remote.SOAServer;

/**
 * @author yicheng
 * @since 2014年10月16日
 *
 */
public class SOAServerTcp extends AbstractSOAServerTcp implements SOAServer  {

    private Logger logger = LoggerFactory.getLogger(SOAServerTcp.class);
    /**
     * @param port
     * @param providerServiceFactory
     */
    public SOAServerTcp(int port, ProviderServiceFactory providerServiceFactory) {
        super(port, providerServiceFactory);
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }

}
