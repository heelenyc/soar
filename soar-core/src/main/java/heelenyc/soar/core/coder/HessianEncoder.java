package heelenyc.soar.core.coder;

import heelenyc.soar.core.api.coder.Encoder;
import heelenyc.soar.core.common.Constans;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import com.caucho.hessian.io.Hessian2Output;

/**
 * @author yicheng
 * @since 2014年9月19日
 * 
 */
public class HessianEncoder implements Encoder {

    static final byte[] LENGTH_PLACEHOLDER = new byte[Constans.MESSAGE_LENGTH_FIELD_LENGTH];

	/**
     * 
     */
	public HessianEncoder() {
	}

	@Override
	public void encode(Object msg, ByteBuf out) throws Exception {

		// begin position
		int lengthPos = out.writerIndex();
		// hold a 4 byte for the size
		out.writeBytes(LENGTH_PLACEHOLDER);
		// 写入数据实体
		Hessian2Output output = new Hessian2Output(new ByteBufOutputStream(out));
		output.writeObject(msg);
		output.flushBuffer();
		// write size
		out.setInt(lengthPos, out.writerIndex() - lengthPos - LENGTH_PLACEHOLDER.length);
		
		output.close();
	}

}
