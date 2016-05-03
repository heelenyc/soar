package heelenyc.soar.provider.remote.global;

import heelenyc.commonlib.NamedThreadFactory;
import heelenyc.soar.provider.remote.api.ClientInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yicheng
 * @since 2016年1月12日
 * 
 */
public class Context {

    private static final Map<String, ClientInfo> clientMap = new ConcurrentHashMap<String, ClientInfo>();
    
    private static final AtomicLong total_commands_processed = new AtomicLong(0);
    
    private static long instantaneous_ops_per_sec = 0l;
    private static long pre_total_commands_processed = 0l;
    private static long pre_qps_timestamp = System.currentTimeMillis();
    
    private static ScheduledExecutorService es = Executors.newScheduledThreadPool(1, new NamedThreadFactory("redis-Context"));
    
    static{
        es.scheduleWithFixedDelay(new Runnable() {
            
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long total = getTotalCommandProcessed();
                
                instantaneous_ops_per_sec = (total - pre_total_commands_processed) / ((now - pre_qps_timestamp ) / 1000);  
                
                pre_total_commands_processed = total;
                pre_qps_timestamp = now;
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    
    public static long getOps() {
        return instantaneous_ops_per_sec;
    }
    
    public static long getConnectedClients() {
        return clientMap.size();
    }

    public static void add(String hostPort) {
        if (isValide(hostPort) && !clientMap.containsKey(hostPort)) {
            ClientInfo info = new ClientInfo(hostPort);
            clientMap.put(hostPort, info);
        }
    }

    public static void incTotalCommandProcessed() {
        total_commands_processed.incrementAndGet();
    }
    
    public static long getTotalCommandProcessed(){
        return total_commands_processed.get();
    }
    
    public static void remove(String hostPort) {
        clientMap.remove(hostPort);
    }
    
    public static Collection<ClientInfo> getAllClient(){
        return clientMap.values();
    }
    
    public static ClientInfo getClient(String hostPort){
        return clientMap.get(hostPort);
    }

    /**
     * @param hostPort
     * @return
     */
    private static boolean isValide(String hostPort) {
        if (hostPort != null && !"".equals(hostPort.trim())) {
            return true;
        }
        return false;
    }
}
