package heelenyc.soar.provider.remote.redis.handler;

import heelenyc.soar.provider.remote.api.ClientInfo;
import heelenyc.soar.provider.remote.global.Constants;
import heelenyc.soar.provider.remote.global.Context;
import heelenyc.soar.provider.remote.redis.api.RedisCommand;
import heelenyc.soar.provider.remote.redis.api.RedisReply;
import heelenyc.soar.provider.remote.redis.reply.BulkReply;
import heelenyc.soar.provider.remote.redis.reply.ErrorReply;
import heelenyc.soar.provider.remote.redis.reply.SimpleStringReply;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
@ChannelHandler.Sharable
public abstract class AbstractRedisCommandHandler extends SimpleChannelInboundHandler<RedisCommand> {
    
    public static final AttributeKey<ClientInfo> CLIENT_INFO_KEY = Constants.CLIENT_INFO_KEY;
    public static final AttributeKey<String> CLIENT_ADDR_KEY = Constants.CLIENT_ADDR_KEY;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        logger.error("AbstractRedisCommandHandler exceptionCaught " + clientinfo.toString(), cause);
        // super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisCommand cmdMsg) throws Exception {

        ClientInfo clientinfo = ctx.channel().attr(CLIENT_INFO_KEY).get();

        RedisReply<?> reply = ErrorReply.SERVER_ERROR_REPLY;
        try {
            Context.incTotalCommandProcessed();
            if (cmdMsg.getAction().equalsIgnoreCase("set")) {
                reply = set(cmdMsg.getArg(0), cmdMsg.getArg(1));
            } else if (cmdMsg.getAction().equalsIgnoreCase("get")) {
                reply = get(cmdMsg.getArg(0));
            } else if (cmdMsg.getAction().equalsIgnoreCase("config")) {
                if (cmdMsg.getArg(0).equalsIgnoreCase("set")) {
                    reply = configSet(cmdMsg.getArg(1), cmdMsg.getArg(2));
                } else if (cmdMsg.getArg(0).equalsIgnoreCase("get")) {
                    reply = configGet(cmdMsg.getArg(1));
                } else {
                    reply = ErrorReply.NO_SUPPORT_REPLY;
                }
            } else if (cmdMsg.getAction().equalsIgnoreCase("info")) {
                reply = info();
            } else if (cmdMsg.getAction().equalsIgnoreCase("stats")) {
                reply = stats();
            } else if (cmdMsg.getAction().equalsIgnoreCase("rpush")) {

                // logger.info("RedisCommandHandler: " + cmdMsg);

                reply = rpush(cmdMsg.getArg(0), cmdMsg.getRemainArgs(1));
            } else if (cmdMsg.getAction().equalsIgnoreCase("lpop")) {
                reply = lpop();
            } else if (cmdMsg.getAction().equalsIgnoreCase("client") && cmdMsg.getArg(0).equalsIgnoreCase("list")) {
                reply = clientList();
            } else if (cmdMsg.getAction().equalsIgnoreCase("quit")) {
                reply = SimpleStringReply.OK;
            } else if (cmdMsg.getAction().equalsIgnoreCase("ping")) {
                if (cmdMsg.getArgList().size() > 0) {
                    reply = new BulkReply(cmdMsg.getArg(0));
                } else {
                    reply = new SimpleStringReply("PONG");
                }
            } else {

                reply = ErrorReply.NO_SUPPORT_REPLY;
            }

            // 更新会话信息
            if (clientinfo != null) {
                clientinfo.setLastCmd(cmdMsg.getAction());
                clientinfo.setLastOpTime(System.currentTimeMillis());
                clientinfo.incOps();
            }

        } catch (IllegalArgumentException e) {
            reply = new ErrorReply(e.getMessage().getBytes());
        } catch (Exception e) {
            reply = ErrorReply.SERVER_ERROR_REPLY;
            throw e;
        } finally {
            ctx.writeAndFlush(reply);
            // 关闭连接
            if (cmdMsg.getAction().equalsIgnoreCase("quit")) {
                ctx.close();
            }
        }
    }

    protected RedisReply<?> set(String key, String value) {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> get(String key) {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> info() {
        StringBuffer sb = new StringBuffer();
        sb.append("# Clients \r\n");
        sb.append("connected_clients:" + Context.getConnectedClients() + "\r\n");
        sb.append("# Stats \r\n");
        sb.append("total_commands_processed:" + Context.getTotalCommandProcessed() + "\r\n");
        sb.append("instantaneous_ops_per_sec:" + Context.getOps() + "\r\n");
        
        return new BulkReply(sb.toString());
    }
    
    protected RedisReply<?> stats() {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> lpop() {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> rpush(String key, String[] value) {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> configSet(String param, String value) {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> configGet(String param) {
        return ErrorReply.NO_SUPPORT_REPLY;
    }

    protected RedisReply<?> clientList() {
        Collection<ClientInfo> list = Context.getAllClient();
        StringBuffer sb = new StringBuffer();
        for (ClientInfo client : list) {
            sb.append(client.toString());
            sb.append('\r');
            sb.append('\n');
        }
        return new BulkReply(sb.toString().getBytes());
    }

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
