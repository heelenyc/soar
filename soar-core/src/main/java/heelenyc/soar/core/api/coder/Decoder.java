package heelenyc.soar.core.api.coder;

import io.netty.buffer.ByteBuf;

/**
 * @author yicheng
 * @since 2014年9月19日
 *
 */
public interface Decoder {


    Object decode(ByteBuf in) throws Exception;
}
