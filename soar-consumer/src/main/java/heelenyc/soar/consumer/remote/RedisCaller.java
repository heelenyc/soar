package heelenyc.soar.consumer.remote;

import heelenyc.commonlib.JedisPoolUtils;
import heelenyc.commonlib.JsonUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.StringUtils;
import heelenyc.commonlib.hash.IHashLocator;
import heelenyc.commonlib.hash.KetamaHashLocator;
import heelenyc.commonlib.hash.ModLocator;
import heelenyc.soar.consumer.SoarConsumer;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.core.keeper.listner.AbstractServiceListner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author yicheng
 * @since 2016年5月4日
 * 
 */
public class RedisCaller implements IRemoteCaller {

    private Logger logger = LogUtils.getLogger(SoarConsumer.class);
    
    private IHashLocator nodeLocator;
    private AbstractServiceListner listner;
    private String uri;
    private String apiClassName;
    private Map<String, JedisPool> redisServiceMap;
    
    /**
     * 
     */
    public RedisCaller(String uri, String hashModel, String apiClassName) {
        try {
            List<String> serviceAddressList = SoarKeeperManager.getServiceAddress(uri,ProtocolToken.REDIS);

            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                LogUtils.warn(logger, "cannot get any instance for service uri {0}", uri);
                // throw new
                // RuntimeException("cannot get any instance for service");
            } else {
                LogUtils.warn(logger, "get instance for service uri {0} : {1}", uri, serviceAddressList);
            }
            this.uri = uri;

            if ("MOD".equalsIgnoreCase(hashModel)) {
                nodeLocator = new ModLocator(serviceAddressList);
                LogUtils.info(logger, "create MOD hashLocator");
            } else {
                nodeLocator = new KetamaHashLocator(serviceAddressList);
                LogUtils.info(logger, "create Ketama hashLocator");
            }
            // 初始化服务地址
            redisServiceMap = new ConcurrentHashMap<String, JedisPool>();
            // 侦听服务
            listenService();

            this.apiClassName = apiClassName;

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LogUtils.info(logger, "consumer release");
                    try {
                        for (JedisPool pool : redisServiceMap.values()) {
                            pool.close();
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }

                }
            });

        } catch (Exception e) {
            LogUtils.error(logger, e, "construct SoarConsumer error for {0} {1}", uri, hashModel);
            System.exit(0);
        }
    }

    @Override
    public Response call(Request req) {
        Jedis redisDao = null;
        String hashKey = null;
        try {

            hashKey = req.hashKey().toString();
            String reqStr = JsonUtils.toJSON(req);
            redisDao = getRedisService(hashKey);
            String ret = redisDao.get(reqStr);

            Response response = JsonUtils.toT(ret, Response.class);

            return response;
            
        } catch (Exception e) {
            LogUtils.error(logger, e, e.getMessage());
            throw new RuntimeException(StringUtils.format("RedisCaller error for {0} ", req));
            
        } finally {
            if (redisDao != null) {
                returnRedisService(hashKey, redisDao);
            }
        }
    }

    private Jedis getRedisService(String key) {
        String targetHostPort = nodeLocator.getNodeByKey(key);
        if (redisServiceMap.get(targetHostPort) == null) {
            redisServiceMap.put(targetHostPort, JedisPoolUtils.getJedisPool(targetHostPort, JedisPoolUtils.CONFIG_MEDIUM));
        }
        return redisServiceMap.get(targetHostPort).getResource();
    }

    private void returnRedisService(String key, Jedis redis) {
        String targetHostPort = nodeLocator.getNodeByKey(key);
        if (redisServiceMap.get(targetHostPort) != null) {
            redisServiceMap.get(targetHostPort).returnResource(redis);
        }
    }
    
    @Override
    public void listenService() {
        // 增加 listner
        listner = new AbstractServiceListner(getUri()) {

            @Override
            public void onRecover(String uri, String hostport) {
                LogUtils.info(logger, "onRecover {0} {1}", uri, hostport);
                nodeLocator.addNode(hostport);
            }

            @Override
            public void onPublish(String uri, String hostport) {
                LogUtils.info(logger, "onPublish {0} {1}", uri, hostport);
                nodeLocator.addNode(hostport);
            }

            @Override
            public void onIsolate(String uri, String hostport) {
                LogUtils.info(logger, "onIsolate {0} {1}", uri, hostport);
                nodeLocator.removeNode(hostport);
            }
        };
    }

    public String getUri() {
        return uri;
    }

    public String getApiClassName() {
        return apiClassName;
    }

    public AbstractServiceListner getListner() {
        return listner;
    }

}
