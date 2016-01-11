package heelenyc.soar.core.coder;

import heelenyc.soar.core.api.coder.Decoder;
import heelenyc.soar.core.api.message.SoarHeader;
import heelenyc.soar.core.api.message.SoarPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    
    private Log logger = LogFactory.getLog(this.getClass());

    private Decoder decoder ;
    
    public MessageDecoder(Decoder decoder,int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        // get a decoder impl
        this.decoder = decoder;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        SoarPackage message = new SoarPackage();
        SoarHeader header = new SoarHeader();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt(); // attachment.size()
        if (size > 0) {
            Map<String, Object> attch = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                // read key
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                // read value
                attch.put(key, decoder.decode(frame));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }
        // if a body serialized , must be bigger than 4 bytes, decode it, other wise , this message has a null body
        if (frame.readableBytes() > 4) {
            message.setBody(decoder.decode(frame));
        }
        message.setHeader(header);
        logger.debug("decode : " + message);
        return message;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }
}
