package heelenyc.soar.provider.remote.api;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yicheng
 * @since 2016年1月12日
 *
 */
public class ClientInfo {

    private String hostPort;
    private String lastCmd;
    private long lastOpTime;
    private AtomicLong ops;
    
    /**
     * @param hostPort
     */
    public ClientInfo(String hostPort) {
        this.hostPort = hostPort;
        this.ops = new AtomicLong();
    }
    
    public String getHostPort() {
        return hostPort;
    }
    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }
    public String getLastCmd() {
        return lastCmd;
    }
    public void setLastCmd(String lastCmd) {
        this.lastCmd = lastCmd;
    }
    public long getLastOpTime() {
        return lastOpTime;
    }
    public void setLastOpTime(long lastOpTime) {
        this.lastOpTime = lastOpTime;
    }
    public long getOps() {
        return ops.get();
    }
    
    public void incOps() {
        this.ops.incrementAndGet();
    }
    
    @Override
    public String toString() {
        return "hostPort=" + hostPort + ", lastCmd=" + lastCmd + ", lastOpTime=" + lastOpTime + ", ops=" + ops.get() ;
    }
    
    
    
}
