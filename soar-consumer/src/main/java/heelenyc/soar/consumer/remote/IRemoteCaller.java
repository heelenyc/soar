package heelenyc.soar.consumer.remote;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;

/**
 * @author yicheng
 * @since 2016年5月4日
 *
 */
public interface IRemoteCaller {

    Response call(Request req);

    /**
     * 
     */
    void listenService();
    
    String getUri();

    String getApiClassName();
}
