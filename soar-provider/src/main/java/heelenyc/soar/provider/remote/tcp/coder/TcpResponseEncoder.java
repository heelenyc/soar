package heelenyc.soar.provider.remote.tcp.coder;

import heelenyc.soar.core.api.bean.ResponseBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author yicheng
 * @since 2016年1月11日
 *
 */
public class TcpResponseEncoder extends MessageToByteEncoder<ResponseBytePacket> {
    
    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void encode(ChannelHandlerContext ctx, ResponseBytePacket msg, ByteBuf out) throws Exception {
        // logger.info("TcpResponseEncoder: " + msg);
        msg.write(out);
    }

}
