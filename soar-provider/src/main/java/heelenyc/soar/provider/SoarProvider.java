package heelenyc.soar.provider;

import heelenyc.commonlib.LogUtils;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

/**
 * service uri 为粒度的，担负监控等责任
 * 
 * @author yicheng
 * @since 2016年6月8日
 * 
 */
public class SoarProvider extends SoarProviderServer {

    private static Logger logger = LogUtils.getLogger(SoarProviderServer.class);
    private String serviceUri;
    private String apiInterface;
    private Object instance;

    /**
     * @param
     */
    public SoarProvider() {

    }

    @PostConstruct
    public void init(){
        try {
            registUri(getServiceUri(), getApiInterface(), getInstance());
        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider registUri error !");
            throw new RuntimeException("SoarProvider registUri error !", e);
        }
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public String getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(String apiInterface) {
        this.apiInterface = apiInterface;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
