package heelenyc.soar.core.keeper.listner;

import heelenyc.commonlib.LogUtils;
import heelenyc.soar.core.keeper.zk.ZKClientFactory;
import heelenyc.soar.core.keeper.zk.ZkConstants;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.log4j.Logger;

/**
 * 默认使用zk
 * 
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public abstract class AbstractServiceListner {

    private Logger logger = LogUtils.getLogger(AbstractServiceListner.class);
    private String serviceUri;
    private int protocol;

    private PathChildrenCache watcher;

    /**
     * 需要观察的服务
     */
    public AbstractServiceListner(final String uri, final int protocol) {
        this.serviceUri = uri;
        this.protocol = protocol;

        // 初始化watcher
        CuratorFramework client = ZKClientFactory.getZooKeeperClient(ZkConstants.NAMESPACE_SERVICE);
        String path = ZkConstants.getServicePath(uri, protocol);
        LogUtils.info(logger, "AbstractServiceListner watch : {0}", path);
        watcher = new PathChildrenCache(client, path, true);
        watcher.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                case INITIALIZED:
                    LogUtils.info(logger, "INITIALIZED", uri, protocol);
                    break;
                case CHILD_ADDED:
                    // 新加节点 或者节点被修改需要更新
                    LogUtils.info(logger, "CHILD_ADDED : {2}", uri, protocol, event.getData());
                    process(uri, protocol, event.getData());
                    break;
                case CHILD_UPDATED:
                    LogUtils.info(logger, "CHILD_UPDATED : {2}", uri, protocol, event.getData());
                    process(uri, protocol, event.getData());
                    break;
                case CHILD_REMOVED:
                    LogUtils.info(logger, "CHILD_REMOVED : {2}", uri, protocol, event.getData());
                    String nodePath = event.getData().getPath();
                    String hostport = nodePath.substring(nodePath.lastIndexOf('/') + 1);
                    onRemove(uri, hostport, protocol);
                    break;
                default:
                    break;
                }
            }
        });
        try {
            watcher.start();
        } catch (Exception e) {
            LogUtils.error(logger, e, "wactch {0} erorr!", path);
            System.exit(0);
        }
    }

    /**
     * @param uri
     * @param protocol2
     * @param data
     */
    protected void process(String uri, int protocol, ChildData data) {
        
        String nodePath = data.getPath();
        String hostport = nodePath.substring(nodePath.lastIndexOf('/') + 1);
        String dataStr = new String(data.getData());
        if(ZkConstants.STATE_PUBLISHED.equals(dataStr)){
            onPublish(uri, hostport, protocol);
        }
        if(ZkConstants.STATE_ISOLATED.equals(dataStr)){
            onIsolate(uri, hostport, protocol);
        }
    }

    public abstract void onPublish(String uri, String hostport, int protocol);

    public abstract void onIsolate(String uri, String hostport, int protocol);

    public abstract void onRemove(String uri, String hostport, int protocol);

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }
}
