package com.heelenyc.soar.consumer.remote;

import java.net.URI;
import java.net.URISyntaxException;

import com.heelenyc.soar.core.api.remote.SOAClient;
import com.heelenyc.soar.core.exception.InvalidTargetUriException;

/**
 * @author yicheng
 * @since 2014年10月15日
 *
 */
public abstract class AbstractSOAClient implements SOAClient {
    
    protected URI targetURI;
    
    public AbstractSOAClient(String targetURIStr) throws InvalidTargetUriException{
        try {
            this.targetURI = new URI(targetURIStr);
        } catch (URISyntaxException e) {
            throw new InvalidTargetUriException(targetURIStr);
        }
    }
    
    @Override
    public URI getTargetURI(){
        return targetURI;
    }
}
