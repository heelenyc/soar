package heelenyc.soar.provider.remote.redis.reply;

import heelenyc.soar.provider.remote.redis.api.RedisReply;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
public class IntegerReply implements RedisReply<Integer> {

    public static final IntegerReply OK = new IntegerReply(1);
    public static final IntegerReply ERROR = new IntegerReply(0);

    private static final char MARKER = ':';

    private final int data;

    public IntegerReply(int data) {
        this.data = data;
    }

    @Override
    public Integer data() {
        return this.data;
    }

    @Override
    public void write(ByteBuf out) throws IOException {
        out.writeByte(MARKER);
        out.writeBytes(String.valueOf(data).getBytes());
        out.writeBytes(CRLF);
    }

    @Override
    public String toString() {
        return "IntegerReply [data=" + data + "]";
    }
}