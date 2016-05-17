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
import heelenyc.soar.core.api.bean.Response.ResponseCode;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.core.keeper.listner.AbstractServiceListner;
import io.netty.util.internal.ConcurrentSet;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private Map<String, Long> preHeatMap = new ConcurrentHashMap<String, Long>(); // 预热
    private ScheduledExecutorService checkexecutor = Executors.newScheduledThreadPool(1);

    // 执行线程池
    private ExecutorService executor = Executors.newFixedThreadPool(ProtocolToken.THEADPOOL_CONSUMER_SIZE);

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

            checkexecutor.scheduleWithFixedDelay(new Runnable() {

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

                    if (!preHeatMap.isEmpty()) {
                        logger.warn("attempt to process preheating :  " + preHeatMap.keySet());
                    }
                    for (Entry<String, Long> preheatItem : preHeatMap.entrySet()) {
                        if (System.currentTimeMillis() - preheatItem.getValue() > ProtocolToken.PREHEATING_TIMESPAN_IN_MS) {
                            logger.warn("to remove preheating host :  " + preheatItem.getKey());
                            preHeatMap.remove(preheatItem.getKey());
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
    public Response call(final Request req) {

        // 放到线程池中执行
        Future<Response> future = executor.submit(new Callable<Response>() {

            @Override
            public Response call() throws Exception {
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
                    if (StringUtils.isNotEmpty(targetHostPort)) {
                        blacList.add(targetHostPort);
                        nodeLocator.removeNode(targetHostPort);
                    }
                    throw new RuntimeException(StringUtils.format("TcpCaller ocurr ClosedChannelException error for {0} at {1} ", req, targetHostPort));
                } catch (Exception e) {
                    LogUtils.error(logger, e, e.getMessage());
                    throw new RuntimeException(StringUtils.format("TcpCaller error for {0} ", req));
                }
            }
        });
        // 要考虑超时
        try {
            return future.get(ProtocolToken.TIME_OUT_IN_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LogUtils.error(logger, e, "call error!");
            return new Response(req.getId(),ResponseCode.SERVER_ERORR.getValue(),"server error");
        } catch (ExecutionException e) {
            LogUtils.error(logger, e, "call error!");
            return new Response(req.getId(),ResponseCode.SERVER_ERORR.getValue(),"server error");
        } catch (TimeoutException e) {
            LogUtils.error(logger, e, "call error!");
            return new Response(req.getId(),ResponseCode.TIME_OUT.getValue(),"server error");
        }

    }

    private TcpCallClient getTcpCallClient(Request req) throws Exception {
        String key = req.hashKey().toString();
        String targetHostPort = nodeLocator.getNodeByKey(key);
        // LogUtils.info(logger, "getTcpCallClient for key {0}  : {1}", key,
        // targetHostPort);

        // 如果locator没有，肯定不正常
        if (StringUtils.isEmpty(targetHostPort)) {
            throw new RuntimeException(StringUtils.format("TcpCaller cannot get a node for {0} ", req));
        }

        // 如果是预热列列表，决定是否走预热的机器
        targetHostPort = sureTargetHost(targetHostPort);
        while (StringUtils.isEmpty(targetHostPort)) {
            // 再hash
            String reHashKey = key + Math.round(Math.random() * 100);
            targetHostPort = nodeLocator.getNodeByKey(reHashKey);
            // 如果locator没有，肯定不正常
            if (StringUtils.isEmpty(targetHostPort)) {
                throw new RuntimeException(StringUtils.format("TcpCaller cannot get a node for {0} ", req));
            }
            targetHostPort = sureTargetHost(targetHostPort);
        }

        if (targetHostPort != null && tcpCallerClientMap.get(targetHostPort) == null) {
            String[] items = targetHostPort.split(":");
            tcpCallerClientMap.put(targetHostPort, new TcpCallClient(Integer.valueOf(items[1]), items[0]));
        }
        return tcpCallerClientMap.get(targetHostPort);
    }

    /**
     * 是否走越热机器逻辑： 预热机器 XX 秒内增加到全量请求，默认是20
     * 
     * @param targetHostPort
     * @return
     */
    private String sureTargetHost(String targetHostPort) {
        // LogUtils.info(logger, "sureTargetHost {0}", targetHostPort);
        if (preHeatMap.containsKey(targetHostPort)) {
            Long preHeattTime = preHeatMap.get(targetHostPort);
            Long span = System.currentTimeMillis() - preHeattTime;
            if (span > Math.random() * ProtocolToken.PREHEATING_TIMESPAN_IN_MS) {
                // LogUtils.info(logger,
                // "{0} preHeating and hit ,preheating time : {1}ms",
                // targetHostPort, span);
                return targetHostPort;
            } else {
                // LogUtils.info(logger,
                // "{0} preHeating and not hit ,preheating time : {1}ms",
                // targetHostPort, span);
                return null;
            }
        } else {
            return targetHostPort;
        }
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
                // 应该有预热逻辑
                preheat(hostport);
            }

            @Override
            public void onIsolate(String uri, String hostport, int protocol) {
                LogUtils.info(logger, "onIsolate {0} {1}", uri, hostport);
                // 隔离的逻辑
                nodeLocator.removeNode(hostport);
            }
        };
    }

    /**
     * 预热逻辑
     * 
     * @param hostport
     */
    private void preheat(String hostport) {
        if (nodeLocator.size() >= 1 && !nodeLocator.contains(hostport)) {
            // 原来有机器的情况下才需要预热，否则根本没机器，不需要预热
            preHeatMap.put(hostport, System.currentTimeMillis());
        }
        nodeLocator.addNode(hostport);
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
