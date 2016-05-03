package heelenyc.soar.provider.remote.api;

/**
 * @author yicheng
 * @since 2016年1月11日
 *
 */
public interface IRemoteServer {

    void stop();

    /**
     * @param address
     * @param port
     * @throws Exception
     */
    void start(String hostPort) throws Exception;
    
}
