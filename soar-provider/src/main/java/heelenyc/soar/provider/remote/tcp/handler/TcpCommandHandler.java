package heelenyc.soar.provider.remote.tcp.handler;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.api.bean.Response.ResponseCode;
import heelenyc.soar.core.api.bean.ResponseBytePacket;
import heelenyc.soar.provider.SoarProviderServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class TcpCommandHandler extends AbstractTcpCommandHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * @param soarProvider
     */
    public TcpCommandHandler() {
    }

    @Override
    protected ResponseBytePacket handler(Request req) throws Exception {
        Response resp = null;
        try {
            logger.info("received Request : " + req.toString());
            
            if (!SoarProviderServer.hasUri(req.getServiceURI())) {
                resp = new Response();
                resp.setEc(ResponseCode.INVALID_URI.getValue());
                resp.setEm("invalid uri");
            }else {
                resp = SoarProviderServer.getExecutor().executor(req,SoarProviderServer.getMethod(req.getMethod()),SoarProviderServer.getImpObj(req.getServiceURI()));
            }
            
        } catch (Exception e) {
            resp = Response.ERROR_RESP;
            logger.error(e.getMessage(), e);
        }
        ResponseBytePacket responseBytePacket = new ResponseBytePacket(resp);
        return responseBytePacket;
    }
}
