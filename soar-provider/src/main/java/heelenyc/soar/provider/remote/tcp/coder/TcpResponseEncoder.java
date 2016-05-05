package heelenyc.soar.provider.remote.tcp.coder;

import heelenyc.soar.core.api.bean.ResponseBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年1月11日
 *
 */
public class TcpResponseEncoder extends MessageToByteEncoder<ResponseBytePacket> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void encode(ChannelHandlerContext ctx, ResponseBytePacket msg, ByteBuf out) throws Exception {
        logger.debug("RedisReplyEncoder: " + msg);
        msg.write(out);
    }

}
