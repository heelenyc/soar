package heelenyc.soar.consumer.remote;

import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.StringUtils;
import heelenyc.commonlib.hash.IHashLocator;
import heelenyc.commonlib.hash.KetamaHashLocator;
import heelenyc.commonlib.hash.ModLocator;
import heelenyc.soar.consumer.remote.tcp.TcpCallClient;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.core.keeper.listner.AbstractServiceListner;
import io.netty.util.internal.ConcurrentSet;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author yicheng
 * @since 2016年5月4日
 * 
 */
public class TcpCaller implements IRemoteCaller {

    private Logger logger = LogUtils.getLogger(TcpCaller.class);

    private IHashLocator nodeLocator;
    private AbstractServiceListner listner;
    private String uri;
    private String apiClassName;
    private Map<String, TcpCallClient> tcpCallerClientMap;

    private Set<String> blacList = new ConcurrentSet<String>(); // 不可用服务的黑名单
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * 
     */
    public TcpCaller(String uri, int hashModel, String apiClassName) {
        try {
            List<String> serviceAddressList = SoarKeeperManager.getServiceAddress(uri, ProtocolToken.JAVA);

            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                LogUtils.error(logger, "cannot get any instance for service uri {0}", uri);
                throw new RuntimeException("cannot get any instance for service uri :" + uri);
            } else {
                LogUtils.info(logger, "TcpCaller getserviceAddressList {2} for targetUri={0} apiClassName={1} ", uri, apiClassName, serviceAddressList);
            }
            this.uri = uri;

            if (ProtocolToken.HASH_MOD == hashModel) {
                nodeLocator = new ModLocator(serviceAddressList);
                LogUtils.info(logger, "create MOD hashLocator");
            } else {
                nodeLocator = new KetamaHashLocator(serviceAddressList);
                LogUtils.info(logger, "create Ketama hashLocator");
            }
            // 初始化服务地址
            tcpCallerClientMap = new ConcurrentHashMap<String, TcpCallClient>();
            // 侦听服务
            listenService();

            this.apiClassName = apiClassName;

            executor.scheduleWithFixedDelay(new Runnable() {

                @Override
                public void run() {
                    if (!blacList.isEmpty()) {
                        logger.warn("attempt to recover  " + blacList);
                    }
                    for (String add : blacList) {
                        if (tcpCallerClientMap.get(add) != null && tcpCallerClientMap.get(add).isConnected()) {
                            logger.warn("to recover  " + add);
                            nodeLocator.addNode(add);
                            blacList.remove(add);
                        }
                    }
                }
            }, 10, 10, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LogUtils.info(logger, "consumer release");
                    try {
                        for (TcpCallClient client : tcpCallerClientMap.values()) {
                            client.close();
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
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
        TcpCallClient tcpCallerClient = null;
        String hashKey = null;
        try {
            hashKey = req.hashKey().toString();
            tcpCallerClient = getTcpCallClient(req);
            Response response = tcpCallerClient.sendRequest(req);
            return response;

        } catch (ClosedChannelException e) {
            // 连接关闭
            String targetHostPort = nodeLocator.getNodeByKey(hashKey);
            blacList.add(targetHostPort);
            nodeLocator.removeNode(targetHostPort);
            throw new RuntimeException(StringUtils.format("TcpCaller error for {0} ", req));
        } catch (Exception e) {
            LogUtils.error(logger, e, e.getMessage());
            throw new RuntimeException(StringUtils.format("TcpCaller error for {0} ", req));
        } finally {

        }
    }

    private TcpCallClient getTcpCallClient(Request req) throws Exception {
        String key = req.hashKey().toString();
        String targetHostPort = nodeLocator.getNodeByKey(key);

        if (StringUtils.isEmpty(targetHostPort)) {
            throw new RuntimeException(StringUtils.format("TcpCaller cannot get a node for {0} ", req));
        }

        if (targetHostPort != null && tcpCallerClientMap.get(targetHostPort) == null) {
            String[] items = targetHostPort.split(":");
            tcpCallerClientMap.put(targetHostPort, new TcpCallClient(Integer.valueOf(items[1]), items[0]));
        }
        return tcpCallerClientMap.get(targetHostPort);
    }

    @Override
    public void listenService() {
        // 增加 listner
        listner = new AbstractServiceListner(getUri(), ProtocolToken.JAVA) {

            @Override
            public void onRemove(String uri, String hostport, int protocol) {
                LogUtils.info(logger, "onRemove {0} {1}", uri, hostport);
                nodeLocator.removeNode(hostport);
            }

            @Override
            public void onPublish(String uri, String hostport, int protocol) {
                LogUtils.info(logger, "onPublish {0} {1}", uri, hostport);
                nodeLocator.addNode(hostport);
            }

            @Override
            public void onIsolate(String uri, String hostport, int protocol) {
                LogUtils.info(logger, "onIsolate {0} {1}", uri, hostport);
                // 隔离的逻辑
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
