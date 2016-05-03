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
public class BulkReply implements RedisReply<byte[]> {

    public static final BulkReply NIL_REPLY = new BulkReply();

    private static final char MARKER = '$';

    private final byte[] data;

    private final int len;

    public BulkReply() {
        this.data = null;
        this.len = -1;
    }

    public BulkReply(byte[] data) {
        this.data = data;
        this.len = data.length;
    }

    public BulkReply(String data) {
        this.data = data.getBytes();
        this.len = this.data.length;
    }

    @Override
    public byte[] data() {
        return this.data;
    }

    @Override
    public void write(ByteBuf out) throws IOException {
        // 1.Write header
        out.writeByte(MARKER);
        out.writeBytes(String.valueOf(len).getBytes());
        out.writeBytes(CRLF);

        // 2.Write data
        if (len > 0) {
            out.writeBytes(data);
            out.writeBytes(CRLF);
        }
    }

    @Override
    public String toString() {
        return "BulkReply [data=" + Arrays.toString(data) + ", len=" + len + "]";
    }
}
