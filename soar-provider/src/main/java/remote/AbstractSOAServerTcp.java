package remote;

import service.ProviderServiceFactory;

import com.heelenyc.soar.core.api.remote.SOAServer;

/**
 * @author yicheng
 * @since 2014年10月16日
 *
 */
public abstract class AbstractSOAServerTcp implements SOAServer {

    protected int port ;
    protected ProviderServiceFactory providerServiceFactory;
    
    public AbstractSOAServerTcp(int port , ProviderServiceFactory providerServiceFactory){
        this.port = port;
        this.providerServiceFactory = providerServiceFactory;
    }
}
