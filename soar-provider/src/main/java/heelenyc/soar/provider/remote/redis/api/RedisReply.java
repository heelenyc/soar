package heelenyc.soar.provider.remote.redis.api;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author yicheng
 * @since 2016年1月11日
 *
 */
public interface RedisReply<T> {

    byte[] CRLF = new byte[] { '\r', '\n' };

    T data();

    void write(ByteBuf out) throws IOException;

}
