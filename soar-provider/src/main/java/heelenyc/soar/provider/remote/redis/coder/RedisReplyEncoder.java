package heelenyc.soar.provider.remote.redis.coder;

import heelenyc.soar.provider.remote.redis.api.RedisReply;
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
public class RedisReplyEncoder extends MessageToByteEncoder<RedisReply<Void>> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void encode(ChannelHandlerContext ctx, RedisReply<Void> msg, ByteBuf out) throws Exception {
        logger.debug("RedisReplyEncoder: " + msg);
        msg.write(out);
    }

}
