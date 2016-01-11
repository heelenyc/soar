package heelenyc.soar.core.api.coder;

import io.netty.buffer.ByteBuf;

/**
 * @author yicheng
 * @since 2014年9月19日
 * 
 */
public interface Encoder {
    
    void encode(Object msg, ByteBuf out) throws Exception;
}
