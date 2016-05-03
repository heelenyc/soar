package heelenyc.soar.provider.remote.redis.coder;

import heelenyc.soar.provider.remote.redis.api.RedisCommand;
import heelenyc.soar.provider.remote.redis.handler.AbstractRedisCommandHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
public class RedisCommandDecoder extends ReplayingDecoder<RedisProtocolState> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Decoded command and arguments */
    private RedisCommand cmd;
    private int argSize;
    private int argIndex = 0;

    public RedisCommandDecoder() {
        super(RedisProtocolState.TO_READ_PREFIX);
    }

    /**
     * Decode in block-io style, rather than nio. because reps protocol has a
     * dynamic body len
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        switch (state()) {
        case TO_READ_PREFIX:
            byte prefix = in.readByte();
            if (prefix == '*') {
                checkpoint(RedisProtocolState.TO_READ_LINENUM);
            } else {
                // 理论不会到这里  
                logger.error("unexpected prefix for redis request : " + prefix);
                ctx.close();
            }
            break;
        case TO_READ_LINENUM:
            decodeNumOfArgs(in);
            checkpoint(RedisProtocolState.TO_READ_ARGS);
            break;
        case TO_READ_ARGS:
            while (argIndex < argSize) {
                if (in.readByte() == '$') {
                    int lenOfBulkStr = readInt(in);
                    // logger.info("RedisCommandDecoder LenOfBulkStr[" + argIndex + "]: " + lenOfBulkStr);

                    byte[] dest = new byte[lenOfBulkStr];
                    in.readBytes(dest);
                    // Skip CRLF(\r\n)
                    in.skipBytes(2);
                    // 这次参数读取完成，修改内部状态变量
                    if (argIndex == 0) {
                        // action
                        cmd = new RedisCommand(new String(dest),ctx.channel().attr(AbstractRedisCommandHandler.CLIENT_ADDR_KEY).get());
                    } else {
                        cmd.getArgList().add(dest);
                    }
                    argIndex++;
                    // 内部状态变化后  要移动读指针
                    checkpoint();

                } else {
                    throw new IllegalStateException("Invalid argument");
                }
            }
            // 解析完成
            checkpoint(RedisProtocolState.TO_READ_PREFIX);
            if (isComplete()) {
                sendCmdToHandler(out);
                clean();
            } else {
                clean();
                throw new IllegalStateException("decode command failed : " + cmd + ", redisArgSize : " + argSize + ", cmd.args.size() : " + cmd.getArgList().size());
            }
            break;
        default:
            throw new IllegalStateException("invalide state default!");
        }
    }

    private void decodeNumOfArgs(ByteBuf in) {
        // Ignore negative case
        argSize = readInt(in);
        logger.debug("RedisCommandDecoder NumOfArgs: " + argSize);
    }

    /**
     * cmds != null means header decode complete arg > 0 means arguments decode
     * has begun arg == cmds.length means complete!
     */
    private boolean isComplete() {
        return cmd != null && cmd.getAction() != null && !"".equals(cmd.getAction().trim()) && cmd.getArgList().size() == argSize - 1;
    }

    
    private void sendCmdToHandler(List<Object> out) {
        // logger.info("RedisCommandDecoder: Send command to next handler , cmd : "
        // + JsonUtils.toJSON(cmd));
        out.add(cmd);
    }

    /**
     * 清楚内部变量状态值
     */
    private void clean() {
        this.cmd = null;
        this.argSize = 0;
        this.argIndex = 0;
    }

    /**
     * 读取字符型的int值，包括结尾的 \r\n
     * @param in
     * @return
     */
    private int readInt(ByteBuf in) {
        int integer = 0;
        char c;
        while ((c = (char) in.readByte()) != '\r') {
            integer = (integer * 10) + (c - '0');
        }
        // skip \n
        if (in.readByte() != '\n') {
            throw new IllegalStateException("Invalid number");
        }
        return integer;
    }

}
