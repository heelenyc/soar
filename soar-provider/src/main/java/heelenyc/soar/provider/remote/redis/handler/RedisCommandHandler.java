package heelenyc.soar.provider.remote.redis.handler;

import heelenyc.commonlib.JsonUtils;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.api.bean.Response.ResponseCode;
import heelenyc.soar.provider.SoarProvider;
import heelenyc.soar.provider.executor.IExecutor;
import heelenyc.soar.provider.remote.redis.api.RedisReply;
import heelenyc.soar.provider.remote.redis.reply.SimpleStringReply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class RedisCommandHandler extends AbstractRedisCommandHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private IExecutor<Request> executor ;
    private SoarProvider parentProvider;
    /**
     * @param soarProvider
     */
    public RedisCommandHandler(SoarProvider provider) {
        this.executor = provider.getExecutor();
        this.parentProvider = provider;
    }

    @Override
    protected RedisReply<?> get(String key) {
        Response resp = null;
        try {
            Request req = JsonUtils.toT(key, Request.class);
            
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
        
        return new SimpleStringReply(JsonUtils.toJSON(resp));
        
    }
}
