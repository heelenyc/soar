package com.heelenyc.soar.provider;

import org.apache.log4j.Logger;

import com.heelenyc.commonlib.LogUtils;
import com.heelenyc.simpleredis.server.AbstractRedisServer;
import com.heelenyc.soar.Request;
import com.heelenyc.soar.provider.executor.IExecutor;
import com.heelenyc.soar.provider.executor.SingleThreadSoarExecutor;
import com.heelenyc.soar.provider.handler.RedisCommandHandler;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class SoarProvider {

    private Logger logger = LogUtils.getLogger(SoarProvider.class);
    private AbstractRedisServer server;// = new AbstractRedisServer(new
                                       // RedisCommandHandler(this));
    private String targetUri;
    private String localHostport;
    private String apiClassName;
    private IExecutor<Request> executor;

    /**
     * @param redisHandler
     */
    public SoarProvider(String targetUri , String localHostPort, String apiClassName, Object serviceImp) {
        try {
            Class<?> api = Class.forName(apiClassName);
            executor = new SingleThreadSoarExecutor(api, serviceImp);
            RedisCommandHandler handler = new RedisCommandHandler(executor,this);
            server = new AbstractRedisServer(handler);
            
            this.localHostport = localHostPort;
            this.targetUri = targetUri;
            this.apiClassName = apiClassName;

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider construct error !");
            throw new RuntimeException("SoarProvider construct error !", e);
        }
    }
    
    public void start(){
        try {
            
            server.start(localHostport);

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider start error !");
            throw new RuntimeException("SoarProvider start error !", e);
        }
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public String getLocalHostport() {
        return localHostport;
    }

    public void setLocalHostport(String localHostport) {
        this.localHostport = localHostport;
    }

    public String getApiClassName() {
        return apiClassName;
    }

    public void setApiClassName(String apiClassName) {
        this.apiClassName = apiClassName;
    }

}
