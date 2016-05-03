package heelenyc.soar.provider.remote.redis.reply;

import heelenyc.soar.provider.remote.redis.api.RedisReply;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
public class SimpleStringReply implements RedisReply<byte[]> {

    public static final SimpleStringReply OK = new SimpleStringReply(new byte[] { 'o', 'k' });

    private static final char MARKER = '+';

    private final byte[] data;

    public SimpleStringReply(byte[] data) {
        this.data = data;
    }
    
    public SimpleStringReply(String data) {
        this.data = data.getBytes();
    }

    @Override
    public byte[] data() {
        return this.data;
    }

    @Override
    public void write(ByteBuf out) throws IOException {
        out.writeByte(MARKER);
        out.writeBytes(data);
        out.writeBytes(CRLF);
    }

    @Override
    public String toString() {
        return "OKMsgReply [data=" + Arrays.toString(data) + "]";
    }
}
