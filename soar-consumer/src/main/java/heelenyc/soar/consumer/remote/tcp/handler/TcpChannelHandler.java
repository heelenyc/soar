package heelenyc.soar.consumer.remote.tcp.handler;

import heelenyc.soar.consumer.remote.tcp.TcpCallClient;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.api.bean.ResponseBytePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年5月5日
 *
 */
public class TcpChannelHandler extends SimpleChannelInboundHandler<ResponseBytePacket>{
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private TcpCallClient parentClient;
    private Map<Long, Object> syncLock ;
    private Map<Long, Response> syncResponse ;

    /**
     * @param parent
     */
    public TcpChannelHandler(TcpCallClient parent) {
        this.parentClient = parent;
        syncLock = parent.getSyncLock();
        syncResponse = parent.getSyncResponse();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseBytePacket msg) throws Exception {
        
        // 如何同步返回结果
        if (msg != null) {
            Response resp = msg.getBodyAsResponse();
            // logger.info("received Response : " + resp);
            if (resp != null) {
                syncResponse.put(resp.getId(), resp);
                synchronized (syncLock.get(resp.getId())) {
                    syncLock.get(resp.getId()).notifyAll();
                }
            }
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("TcpResponseHandler exceptionCaught", cause);
        // super.exceptionCaught(ctx, cause);
        // 需要关闭吗？
        ctx.close();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道关闭时
        parentClient.setConnected(false);
        super.channelInactive(ctx);
    }

    public TcpCallClient getParentClient() {
        return parentClient;
    }

}
