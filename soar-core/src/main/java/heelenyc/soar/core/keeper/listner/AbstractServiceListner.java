package heelenyc.soar.core.keeper.listner;

/**
 * 默认使用zk
 * 
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public abstract class AbstractServiceListner {

    private String serviceUri;

    /**
     * 需要观察的服务
     */
    public AbstractServiceListner(String uri) {
        // watch zookeeper and call hander
        this.serviceUri = uri;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public abstract void onPublish(String uri, String hostport);

    public abstract void onIsolate(String uri, String hostport);

    public abstract void onRecover(String uri, String hostport);
}
