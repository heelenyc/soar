package com.heelenyc.soar.provider.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.commonlib.JsonUtils;
import com.heelenyc.simpleredis.api.reply.RedisReply;
import com.heelenyc.simpleredis.handler.AbstractRedisCommandHandler;
import com.heelenyc.simpleredis.reply.SimpleStringReply;
import com.heelenyc.soar.Request;
import com.heelenyc.soar.Response;
import com.heelenyc.soar.Response.ResponseCode;
import com.heelenyc.soar.provider.SoarProvider;
import com.heelenyc.soar.provider.executor.IExecutor;

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
    public RedisCommandHandler(IExecutor<Request> executor,SoarProvider provider) {
        this.executor = executor;
        this.parentProvider = provider;
    }

    @Override
    protected RedisReply<?> get(String key) {
        Response resp = null;
        try {
            Request req = JsonUtils.toT(key, Request.class);
            
            logger.info("received Request : " + req.toString());
            
            if (!parentProvider.getTargetUri().equals(req.getServiceURI())) {
                resp = new Response();
                resp.setEc(ResponseCode.INVALID_URI.getValue());
                resp.setEm("invalid uri");
            }else {
                resp = executor.executor(req);
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
