package heelenyc.soar.consumer.remote.tcp.coder;

import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.ResponseBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
public class TcpResponseDecoder extends ReplayingDecoder<TcpProtocolState> {

    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Decoded command and arguments */
    private byte[] headBuffer;
    private byte[] bodyBuffer;
    private int BODY_LEN;

    public TcpResponseDecoder() {
        super(TcpProtocolState.TO_READ_HEADER);
    }

    /**
     * Decode in block-io style, rather than nio. because reps protocol has a
     * dynamic body len
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        switch (state()) {
        case TO_READ_HEADER:
            // header 长度固定
            headBuffer = new byte[ProtocolToken.HEADER_LENGTH];
            in.readBytes(headBuffer);
            checkpoint(TcpProtocolState.TO_READ_BODY);
            // 获取长度
            BODY_LEN = ((headBuffer[1] & 0x000000ff) << 8) + (headBuffer[2] & 0x000000ff);
            break;
        case TO_READ_BODY:
            bodyBuffer = new byte[BODY_LEN];
            in.readBytes(bodyBuffer);
            // 全部完成
            // 回归初始状态
            checkpoint(TcpProtocolState.TO_READ_HEADER);
            // 处理后续
            out.add(new ResponseBytePacket(headBuffer,bodyBuffer));
            BODY_LEN = 0;
            headBuffer = null;
            bodyBuffer = null;
            break;
        default:
            throw new IllegalStateException("invalide state default!");
        }
    }



    

//    /**
//     * 读取字符型的int值，包括结尾的 \r\n
//     * @param in
//     * @return
//     */
//    private int readInt(ByteBuf in) {
//        int integer = 0;
//        char c;
//        while ((c = (char) in.readByte()) != '\r') {
//            integer = (integer * 10) + (c - '0');
//        }
//        // skip \n
//        if (in.readByte() != '\n') {
//            throw new IllegalStateException("Invalid number");
//        }
//        return integer;
//    }

}
