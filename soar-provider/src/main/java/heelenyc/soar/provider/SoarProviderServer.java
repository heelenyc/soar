package heelenyc.soar.provider;

import heelenyc.commonlib.IpUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.provider.executor.IExecutor;
import heelenyc.soar.provider.executor.SingleThreadSoarExecutor;
import heelenyc.soar.provider.remote.redis.SimpleRedisServer;
import heelenyc.soar.provider.remote.redis.handler.RedisCommandHandler;
import heelenyc.soar.provider.remote.tcp.SimpleTcpServer;
import heelenyc.soar.provider.remote.tcp.handler.TcpCommandHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

/**
 * 一个provider对应一个端口，但是可以对应多个service-uri，就是说多个uri可以共享一个端口，通常是一个服务家族
 * 
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public abstract class SoarProviderServer {

    private static Logger logger = LogUtils.getLogger(SoarProviderServer.class);
    private static SimpleRedisServer redisServer;
    private static SimpleTcpServer tcpServer;
    private static String localHost;
    private static int port;
    private static IExecutor<Request> executor;

    private static Map<String, Object> uri2Impobj;
    private static Map<String, Method> methods;
    private static Set<String> apiClassNameList;

    static {
        try {
            SoarProviderServer.localHost = IpUtils.IP_LAN;
            SoarProviderServer.port = Integer.valueOf(System.getProperty("port"));
            // 请求执行器
            executor = new SingleThreadSoarExecutor();

            apiClassNameList = new ConcurrentSkipListSet<String>();
            uri2Impobj = new ConcurrentHashMap<String, Object>();
            methods = new ConcurrentHashMap<String, Method>();

            // 起端口服务
            start();

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider server start error !");
            throw new RuntimeException("SoarProvider server start error !", e);
        }
    }

    public boolean registUri(String targetUri, String apiClassName, Object serviceImp) {
        try {
            Class<?> api = Class.forName(apiClassName);
            for (Method m : api.getMethods()) {
                if (methods.containsKey(m.getName())) {
                    throw new RuntimeException("not support over-load ! (more than one methods have the same name in " + api.getName() + ")");
                }
                methods.put(m.getName(), m);
            }
            uri2Impobj.put(targetUri, serviceImp);
            apiClassNameList.add(apiClassName);

            // 向中心报告
            SoarKeeperManager.publisService(targetUri, getTcpHostport(), ProtocolToken.JAVA);
            SoarKeeperManager.publisService(targetUri, getRedisHostport(), ProtocolToken.REDIS);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
        return true;
    }

    /**
     * @return
     */
    private static String getRedisHostport() {
        return localHost + ":" + (port + 1);
    }

    /**
     * @return
     */
    private static String getTcpHostport() {
        return localHost + ":" + port;
    }

    public static void start() {
        try {
            TcpCommandHandler tcpHandler = new TcpCommandHandler();
            tcpServer = new SimpleTcpServer(tcpHandler);
            tcpServer.start(getTcpHostport());

            RedisCommandHandler handler = new RedisCommandHandler();
            redisServer = new SimpleRedisServer(handler);
            redisServer.start(getRedisHostport());

        } catch (Exception e) {
            LogUtils.error(logger, e, "SoarProvider start error !");
            // throw new RuntimeException("SoarProvider start error !", e);
            System.exit(1);
        }
    }

    public static IExecutor<Request> getExecutor() {
        return executor;
    }

    /**
     * @param method
     * @return
     */
    public static Method getMethod(String methodName) {
        return methods.get(methodName);
    }

    /**
     * @param method
     * @return
     */
    public static Object getImpObj(String uri) {
        return uri2Impobj.get(uri);
    }

    /**
     * @param serviceURI
     * @return
     */
    public static boolean hasUri(String serviceURI) {
        return uri2Impobj.containsKey(serviceURI);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        SoarProviderServer.port = port;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        SoarProviderServer.localHost = localHost;
    }

}
