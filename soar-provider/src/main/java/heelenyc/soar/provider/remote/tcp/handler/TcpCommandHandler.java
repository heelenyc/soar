package heelenyc.soar.provider.remote.tcp.handler;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.api.bean.Response.ResponseCode;
import heelenyc.soar.core.api.bean.ResponseBytePacket;
import heelenyc.soar.provider.SoarProvider;
import heelenyc.soar.provider.executor.IExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class TcpCommandHandler extends AbstractTcpCommandHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private IExecutor<Request> executor ;
    private SoarProvider parentProvider;
    /**
     * @param soarProvider
     */
    public TcpCommandHandler(SoarProvider provider) {
        this.executor = provider.getExecutor();
        this.parentProvider = provider;
    }

    @Override
    protected ResponseBytePacket handler(Request req) throws Exception {
        Response resp = null;
        try {
            logger.info("received Request : " + req.toString());
            
            if (!parentProvider.hasUri(req.getServiceURI())) {
                resp = new Response();
                resp.setEc(ResponseCode.INVALID_URI.getValue());
                resp.setEm("invalid uri");
            }else {
                resp = executor.executor(req,parentProvider.getMethod(req.getMethod()),parentProvider.getImpObj(req.getServiceURI()));
            }
            
        } catch (Exception e) {
            resp = new Response();
            resp.setEc(ResponseCode.SERVER_ERORR.getValue());
            resp.setEm(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        ResponseBytePacket responseBytePacket = new ResponseBytePacket(resp);
        return responseBytePacket;
    }
}
