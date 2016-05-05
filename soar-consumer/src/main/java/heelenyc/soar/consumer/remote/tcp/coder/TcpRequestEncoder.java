package heelenyc.soar.consumer.remote.tcp.coder;

import heelenyc.soar.core.api.bean.RequestBytePacket;
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
public class TcpRequestEncoder extends MessageToByteEncoder<RequestBytePacket> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void encode(ChannelHandlerContext ctx, RequestBytePacket msg, ByteBuf out) throws Exception {
        logger.debug("TcpRequestEncoder: " + msg);
        msg.write(out);
    }

}
