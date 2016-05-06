package heelenyc.soar.core.keeper.zk;

import heelenyc.commonlib.IpUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.StringUtils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import redis.clients.jedis.Jedis;

/**
 * @author yicheng
 * @since 2015年8月20日
 * 
 */
public class SimpleZKUtils {

    private static Logger logger = Logger.getLogger(SimpleZKUtils.class);

    public static String generatePath(String... nodename) {
        StringBuilder bf = new StringBuilder();

        for (String arg : nodename) {
            if (StringUtils.isNotEmpty(arg)) {
                bf.append("/");
                bf.append(arg);
            }
        }
        return bf.toString();
    }

    public static boolean replace(CuratorFramework client, String path, String value) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            client.setData().forPath(path, value.getBytes());
        } else {
            client.create().creatingParentsIfNeeded().forPath(path, value.getBytes());
        }
        return true;
    }

    public static boolean replaceEphemeral(CuratorFramework client, String path, String value) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            client.setData().forPath(path, value.getBytes());
        } else {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, value.getBytes());
        }
        return true;
    }

    /**
     * 如果节点不存在，则创建，否则do nothing
     * 
     * @param client
     * @param path
     * @param value
     * @return
     * @throws Exception
     */
    public static boolean createIfNoNode(CuratorFramework client, String path, String value) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            // 节点存在 , noop！
        } else {
            client.create().creatingParentsIfNeeded().forPath(path, value.getBytes());
        }
        return true;
    }

    public static String getStringData(CuratorFramework client, String path) {
        try {
            return new String(client.getData().forPath(path));
        } catch (Exception e) {
            LogUtils.error(logger, e, "get data error for path {0}", path);
            return null;
        }
    }

    public static String getStringData(CuratorFramework client, String path, String defautValue) {
        try {
            return new String(client.getData().forPath(path));
        } catch (Exception e) {
            LogUtils.error(logger, e, "get data error for path {0}", path);
            return defautValue;
        }
    }

    public static boolean checkExists(CuratorFramework client, String path, boolean defaultValue) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtils.error(logger, e, "checkExists error for path {0}", path);
            return defaultValue;
        }
    }

    public static boolean checkExists(CuratorFramework client, String path) {
        return checkExists(client, path, false);
    }

    public static boolean isRedisOK(String host, String port) {
        Jedis redis = null;
        try {
            redis = new Jedis(host, Integer.valueOf(port), 3000);
            String result = redis.ping();
            if ("PONG".equalsIgnoreCase(result)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtils.error(logger, e, "isRedisOK error ! host : {0} port : {1}", host, port);
            return false;
        } finally {
            if (redis != null) {
                try {
                    redis.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    public static boolean getCheckLock(CuratorFramework client, long lastcheckExpireTimeInMS, long lastcheckStaleInMs, String lockPath, String lastCheckTimestampPath) {
        long nowTimestamp = System.currentTimeMillis();
        // /checkinfo/ 下有两个节点 lock 和 lastcheckTimestamp
        long lastcheckTimestamp = 0;
        try {
            byte[] lastcheckTimestampByte = client.getData().forPath(lastCheckTimestampPath);
            lastcheckTimestamp = Long.valueOf(new String(lastcheckTimestampByte));
            LogUtils.debug(logger, "lastchecktime {0} ms ago !", System.currentTimeMillis() - lastcheckTimestamp);
        } catch (NoNodeException e) {
            LogUtils.info(logger, "lastcheckTimestamp not exits, will assign 0!");
        } catch (Exception e) {
            LogUtils.error(logger, e, "get checkinfo/lastcheckTimestamp error, will assign 0!");
        }
        // 如果 上次检测时间过期 进行抢锁，否则直接false
        if (lastcheckExpireTimeInMS < nowTimestamp - lastcheckTimestamp) {
            // lastcheckTimestamp expired
            try {
                // 注意创建的是临时节点
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(lockPath);
            } catch (NodeExistsException e) {
                // 这个地方有问题，节点存在有两个情况：1、锁被别人抢了，2、上次检测之后，锁删除失败
                // 第二点是异常情况，解决办法，lock为临时节点，如果上次检测时间明显超时，依旧获取所
                if (lastcheckStaleInMs < nowTimestamp - lastcheckTimestamp) {
                    LogUtils.warn(logger, "lastchecktime is stale, get lock derectly ! lastchecktime {0} ms ago ! ", nowTimestamp - lastcheckTimestamp);
                    return true;
                }
                return false;
            } catch (Exception e) {
                LogUtils.error(logger, e, "create checkinfo/lock error!");
                // 创建锁出现其他异常，获取锁
            }
        } else {
            // 检测时间还没有超时，不用检测
            LogUtils.info(logger, "last check is {0} ms ago , give up check directly!", nowTimestamp - lastcheckTimestamp);
            return false;
        }
        return true;
    }

    /**
     * 删除 lock 节点 更新 lastcheckTimestamp
     * 
     * @return
     */
    public static boolean releaseCheckLock(CuratorFramework client, String lockPath, String lastCheckTimestampPath, String checkHostPath) {
        // 删除lock
        try {
            client.delete().forPath(lockPath);
        } catch (Exception e) {
            LogUtils.error(logger, e, "delete {0} error!", lockPath);
            // 如果lock删除失败，不更新时间
            return false;
        }
        // 更新时间和主机信息
        try {
            SimpleZKUtils.replace(client, lastCheckTimestampPath, String.valueOf(System.currentTimeMillis()));
            SimpleZKUtils.replace(client, checkHostPath, String.valueOf(IpUtils.HOST_NAME));
        } catch (Exception e) {
            LogUtils.error(logger, e, "update checkinfo error!");
        }
        return true;
    }
}
