package com.heelenyc.soar.consumer.remote;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.soar.core.exception.InvalidTargetUriException;

/**
 * @author yicheng
 * @since 2014年10月15日
 *
 */
public class SOAClientTcp extends AbstractSOAClient {

    private Logger logger = LoggerFactory.getLogger(SOAClientTcp.class);
    
    /**
     * @param targetURIStr
     * @throws InvalidTargetUriException
     */
    public SOAClientTcp(String targetURIStr) throws InvalidTargetUriException {
        super(targetURIStr);
        
        
    }

    @Override
    public Object invoke(String serviceURI, Method method, Object[] args, int timeout) {
        return null;
    }

}
