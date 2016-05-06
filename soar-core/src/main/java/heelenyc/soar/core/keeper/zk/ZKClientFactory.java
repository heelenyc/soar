package heelenyc.soar.core.keeper.zk;

import heelenyc.commonlib.LogUtils;

import java.io.IOException;
import java.util.Properties;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

/**
 * @author yicheng
 * @since 2015年8月13日
 * 
 */
public class ZKClientFactory {

    private static Logger logger = Logger.getLogger(ZKClientFactory.class);
    private static CuratorFramework client = null;

    // static {
    // init();
    // }

    ZKClientFactory() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (client != null) {
                    client.close();
                }
            }
        });
    }

    public static boolean loadKeeperConfigAndStart() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("zookeeper-config.properties"));

            String zookeeperConnectionString = properties.getProperty("connectString");
            int sessionTimeoutMs = Integer.valueOf(properties.getProperty("sessionTimeoutMs", "3000"));
            int connectionTimeoutMs = Integer.valueOf(properties.getProperty("connectionTimeoutMs", "3000"));
            int baseSleepTimeMs = Integer.valueOf(properties.getProperty("baseSleepTimeMs", "1000"));
            int maxRetries = Integer.valueOf(properties.getProperty("maxRetries", "3"));
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
            client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
            client.start();
            
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Make sure zookeeper-config.properties correctly");
        }
    }

    public static CuratorFramework getZooKeeperClient() {
        return getZooKeeperClient("default");

    }

    /**
     * 自带命名空间、保证业务隔离
     * 
     * @param namespace
     * @return
     */
    public static CuratorFramework getZooKeeperClient(String namespace) {
        if (client == null) {
            loadKeeperConfigAndStart();
        }
        return client.usingNamespace(namespace);
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework test_client = getZooKeeperClient();
        try {

            @SuppressWarnings("resource")
            final PathChildrenCache cache = new PathChildrenCache(test_client, "/test", true);

            cache.getListenable().addListener(new PathChildrenCacheListener() {

                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                    case INITIALIZED:
                        LogUtils.info(logger, "INITIALIZED event data: {0}", event.getData());
                        break;
                    case CHILD_ADDED:
                        LogUtils.info(logger, "CHILD_ADDED event data: {0}", event.getData());
                        break;
                    case CHILD_UPDATED:
                        LogUtils.info(logger, "CHILD_UPDATED event data: {0}", event.getData());
                        break;
                    case CHILD_REMOVED:
                        LogUtils.info(logger, "CHILD_REMOVED event data: {0}", event.getData());
                        break;
                    default:
                        break;
                    }
                }
            });

            cache.start(StartMode.BUILD_INITIAL_CACHE);
            // cache.start(StartMode.POST_INITIALIZED_EVENT);
            // cache.start(StartMode.NORMAL);

            Thread.sleep(1000 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
