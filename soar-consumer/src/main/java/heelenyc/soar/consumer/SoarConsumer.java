package heelenyc.soar.consumer;

import heelenyc.commonlib.ClassUtils;
import heelenyc.commonlib.IpUtils;
import heelenyc.commonlib.JedisPoolUtils;
import heelenyc.commonlib.JsonUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.hash.IHashLocator;
import heelenyc.commonlib.hash.KetamaHashLocator;
import heelenyc.commonlib.hash.ModLocator;
import heelenyc.soar.consumer.api.IConsumer;
import heelenyc.soar.consumer.proxy.ServiceProxy;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.core.keeper.listner.AbstractServiceListner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 需要关注服务，并且还要代理接口
 * 
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public class SoarConsumer implements IConsumer {

    private Logger logger = LogUtils.getLogger(SoarConsumer.class);
    private IHashLocator nodeLocator;
    private AbstractServiceListner listner;
    private String consumUri;
    private String apiClassName;

    private Map<String, JedisPool> redisServiceMap;

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
            List<String> serviceAddressList = SoarKeeperManager.getServiceAddress(uri);

            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                LogUtils.warn(logger, "cannot get any instance for service uri {0}", uri);
                // throw new
                // RuntimeException("cannot get any instance for service");
            } else {
                LogUtils.warn(logger, "cannot get instance for service uri {0} : {1}", uri, serviceAddressList);
            }
            this.consumUri = uri;

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

            instance = ServiceProxy.newInstance(this);

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

    public AbstractServiceListner getListner() {
        return listner;
    }

    public void setApiClassName(String apiClassName) {
        this.apiClassName = apiClassName;
    }

    @Override
    public String getUri() {
        return consumUri;
    }

    @Override
    public String getApi() {
        return apiClassName;
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

    @Override
    public Object callMethod(Method method, Object[] args) {

        Request req = new Request();
        req.setMethod(method.getName());
        if (args == null) {
            req.setParams(Arrays.asList());
        }else {
            req.setParams(Arrays.asList(args));
        }
        req.setServiceURI(getUri());
        req.setSource(IpUtils.IP_LAN);

        Jedis redisDao = getRedisService(req.hashKey().toString());
        String ret = redisDao.get(JsonUtils.toJSON(req));
        returnRedisService(req.hashKey().toString(), redisDao);

        Response response = JsonUtils.toT(ret, Response.class);

//        // 作为java端  data需要反序列化
//        Class<?> returnType = method.getReturnType();
//        if (!ClassUtils.isCommonPrimitive(returnType)) {
//            // 考虑 数组 list set map  共四种
//            String dataStr = response.getData().toString();
//            LogUtils.info(logger, "returnType for {0} is not isCommonPrimitive", req);
//            if (List.class.isAssignableFrom(returnType)) {
//                // 获取 List<E> 的元素
//                Class<?> retType = returnType.getMethod("get", null).getReturnType();
//                return JsonUtils.toTList(dataStr, returnType.g);
//            }
//            return JsonUtils.toT(dataStr, returnType);
//        } else {
//            // 基础类型直接返回，由invoke强转
            return response.getData();
//        }
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

}
