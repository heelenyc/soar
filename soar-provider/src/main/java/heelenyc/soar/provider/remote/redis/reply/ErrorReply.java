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
public class ErrorReply implements RedisReply<byte[]> {

    public static final ErrorReply NO_SUPPORT_REPLY = new ErrorReply("no support".getBytes());
    public static final ErrorReply SERVER_ERROR_REPLY = new ErrorReply("server error".getBytes());

    private static final char MARKER = '-';

    private final byte[] data;

    public ErrorReply(byte[] data) {
        this.data = data;
    }
    
    public ErrorReply(String data) {
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
        return "ErrorMsgReply [data=" + Arrays.toString(data) + "]";
    }

}
