package heelenyc.soar.consumer.remote;

import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;

/**
 * @author yicheng
 * @since 2016年5月4日
 * 
 */
public class RemoteCallerDecorater {

    private IRemoteCaller tcpCaller;

    public RemoteCallerDecorater(String uri, int hashModel, String apiClassName) {
        try {
            tcpCaller = new TcpCaller(uri, hashModel, apiClassName);
        } catch (Exception e) {
            throw new RuntimeException("RemoteCallerDecorater error", e);
        }
    }

    public Response call(Request req) {
        if (req == null) {
            throw new RuntimeException("RemoteCallerDecorater null requset");
        }
        if (req.getProtocol() == ProtocolToken.JAVA) {
            return tcpCaller.call(req);
        } else {
            throw new RuntimeException("RemoteCallerDecorater invalid protocol " + req.getProtocol());
        }
    }
}
