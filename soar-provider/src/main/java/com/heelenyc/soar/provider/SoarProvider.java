package com.heelenyc.soar.provider;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import com.heelenyc.commonlib.LogUtils;
import com.heelenyc.simpleredis.server.AbstractRedisServer;
import com.heelenyc.soar.api.bean.Request;
import com.heelenyc.soar.provider.executor.IExecutor;
import com.heelenyc.soar.provider.executor.SingleThreadSoarExecutor;
import com.heelenyc.soar.provider.handler.RedisCommandHandler;

/**
 * 一个provider对应一个端口，但是可以对应多个service-uri，就是说多个uri可以共享一个端口，通常是一个服务家族
 * 
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class SoarProvider {

    private Logger logger = LogUtils.getLogger(SoarProvider.class);
    private AbstractRedisServer server;// = new AbstractRedisServer(new
                                       // RedisCommandHandler(this));
    private Map<String, Object> uri2Impobj;
    private Map<String, Method> methods;
    private String localHostport;
    private ConcurrentSkipListSet<String> apiClassNameList;
    private IExecutor<Request> executor;

    /**
     * @param redisHandler
     */
    public SoarProvider(String localHostPort) {
        try {
            this.localHostport = localHostPort;
            executor = new SingleThreadSoarExecutor();
            apiClassNameList = new ConcurrentSkipListSet<String>();
            uri2Impobj = new ConcurrentHashMap<String, Object>();
            methods = new ConcurrentHashMap<String, Method>();

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider construct error !");
            throw new RuntimeException("SoarProvider construct error !", e);
        }
    }

    public boolean registUri(String targetUri, String apiClassName, Object serviceImp) {
        try {
            apiClassNameList.add(apiClassName);
            uri2Impobj.put(targetUri, serviceImp);
            Class<?> api = Class.forName(apiClassName);
            for (Method m : api.getMethods()) {
                if (methods.containsKey(m.getName())) {
                    throw new RuntimeException("not support over-load ! (more than one methods have the same name in " + api.getName() + ")");
                }
                methods.put(m.getName(), m);
            }
            
            // 向中心报告
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
        return true;
    }

    public void start() {
        try {

            RedisCommandHandler handler = new RedisCommandHandler(this);
            server = new AbstractRedisServer(handler);
            server.start(localHostport);

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider start error !");
            throw new RuntimeException("SoarProvider start error !", e);
        }
    }



    public String getLocalHostport() {
        return localHostport;
    }

    public void setLocalHostport(String localHostport) {
        this.localHostport = localHostport;
    }


    public IExecutor<Request> getExecutor() {
        return executor;
    }

    /**
     * @param method
     * @return
     */
    public Method getMethod(String methodName) {
        return methods.get(methodName);
    }

    /**
     * @param method
     * @return
     */
    public Object getImpObj(String uri) {
        return uri2Impobj.get(uri);
    }

    /**
     * @param serviceURI
     * @return
     */
    public boolean hasUri(String serviceURI) {
        return uri2Impobj.containsKey(serviceURI);
    }

}
