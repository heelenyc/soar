package heelenyc.soar.provider.remote.tcp.handler;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.RequestBytePacket;
import heelenyc.soar.core.api.bean.ResponseBytePacket;
import heelenyc.soar.core.serialize.SerializeUtils;
import heelenyc.soar.provider.remote.api.ClientInfo;
import heelenyc.soar.provider.remote.global.Constants;
import heelenyc.soar.provider.remote.global.Context;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
@ChannelHandler.Sharable
public abstract class AbstractTcpCommandHandler extends SimpleChannelInboundHandler<RequestBytePacket> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final AttributeKey<ClientInfo> CLIENT_INFO_KEY = Constants.CLIENT_INFO_KEY;
    public static final AttributeKey<String> CLIENT_ADDR_KEY = Constants.CLIENT_ADDR_KEY;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String hostPort = insocket.getAddress().getHostAddress() + ":" + insocket.getPort();
            Context.add(hostPort);
            ClientInfo clientInfo = Context.getClient(hostPort);
            // 添加 属性
            ctx.channel().attr(CLIENT_INFO_KEY).set(clientInfo);
            ctx.channel().attr(CLIENT_ADDR_KEY).set(hostPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            String hostPort = ctx.channel().attr(CLIENT_ADDR_KEY).get();
            Context.remove(hostPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ClientInfo clientinfo = ctx.channel().attr(CLIENT_INFO_KEY).get();
        logger.error("AbstractTcpCommandHandler exceptionCaught " + clientinfo.toString(), cause);
        // super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestBytePacket requestBytePacket) throws Exception {

        ClientInfo clientinfo = ctx.channel().attr(CLIENT_INFO_KEY).get();
        ResponseBytePacket respbyteBytePacket = null;
        try {
            Request request = requestBytePacket.getBodyAsRequest();
            respbyteBytePacket = handler(request);

            // 更新会话信息
            if (clientinfo != null) {
                clientinfo.setLastCmd("tcp call");
                clientinfo.setLastOpTime(System.currentTimeMillis());
                clientinfo.incOps();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (respbyteBytePacket != null) {
                ctx.writeAndFlush(respbyteBytePacket);
            }else {
                ctx.writeAndFlush(ResponseBytePacket.emptyReponsePacket);
            }
        }
    }

    /**
     * @param request
     * @return
     * @throws Exception 
     */
    protected abstract ResponseBytePacket handler(Request request) throws Exception;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        ClientInfo clientinfo = ctx.channel().attr(CLIENT_INFO_KEY).get();

        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            // 如果是IdleStateEvent 事件 处理之
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
            case READER_IDLE:
                logger.warn("READER_IDLE  for clientsession : " + clientinfo.toString());
                break;
            case WRITER_IDLE:
                logger.warn("WRITER_IDLE  for clientsession : " + clientinfo.toString());
                break;
            case ALL_IDLE:
                logger.warn("ALL_IDLE  for clientsession : " + clientinfo.toString());
                break;
            default:
                break;
            }
            // 出现idle 关闭会话
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
