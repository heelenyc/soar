package heelenyc.soar.core.api.bean;

import heelenyc.commonlib.HexUtils;


/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public class ResponseBytePacket extends TcpBytePacket{


    @Override
    public String toString() {
//        return "MsgBytePacket [header=" + Arrays.toString(header) + ", body=" + Arrays.toString(body) + "]";
        return "ResponseBytePacket [header=" + HexUtils.printHexString(getHeader()) + ", body=" + HexUtils.printHexString(getBody()) + "]";
    }
}
