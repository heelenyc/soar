package heelenyc.soar.consumer;

import heelenyc.commonlib.IpUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.soar.consumer.api.IConsumer;
import heelenyc.soar.consumer.proxy.ServiceProxy;
import heelenyc.soar.consumer.remote.RemoteCallerDecorater;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * 需要关注服务，并且还要代理接口
 * 
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public class SoarConsumer implements IConsumer {

    private Logger logger = LogUtils.getLogger(SoarConsumer.class);
    
    private String uri;
    private String apiClassName;
    private RemoteCallerDecorater caller;

    // 单例
    private volatile Object instance;

    @SuppressWarnings("unchecked")
    public <T> T getInstance() {
        return (T) instance;
    }

    /**
     * 
     */
    public SoarConsumer(String uri, String hashModel, String apiClassName) {
        try {
            caller = new RemoteCallerDecorater(uri, hashModel, apiClassName);
            this.uri = uri;
            this.apiClassName = apiClassName;
            
            // 初始化代理实例
            instance = ServiceProxy.newInstance(this);
        } catch (Exception e) {
            LogUtils.error(logger, e, "construct SoarConsumer error for {0} {1}", uri, hashModel);
            System.exit(0);
        }
    }

  

    public void setApiClassName(String apiClassName) {
        this.apiClassName = apiClassName;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getApi() {
        return apiClassName;
    }

    

    @Override
    public Object callMethod(Method method, Object[] args) {

            Request req = new Request();
            // 客户端是java
            req.setProtocol(ProtocolToken.JAVA);
            req.setMethod(method.getName());
            req.setParams(args);
            req.setServiceURI(getUri());
            req.setSource(IpUtils.IP_LAN);

            Response response = caller.call(req);
            
            if (response != null) {
                return response.getData();
            } else {
                return null;
            }
    }

    public RemoteCallerDecorater getCaller() {
        return caller;
    }

}
