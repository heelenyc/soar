package heelenyc.soar.core.coder;

import heelenyc.soar.core.api.coder.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import com.caucho.hessian.io.Hessian2Input;

/**
 * @author yicheng
 * @since 2014年9月19日
 * 
 */
public class HessianDecoder implements Decoder {

    private static HessianDecoder hessianDecoder = new HessianDecoder();
    
    @Override
    public Object decode(ByteBuf in) throws Exception {
        // read size
        int objectSize = in.readInt();
        Hessian2Input input = new Hessian2Input(new ByteBufInputStream(in, objectSize));
        Object obj = input.readObject();
        input.close();
        return obj;
    }

    public static Decoder getInstance() {
        return hessianDecoder;
    }

}
