package heelenyc.soar.core.api.bean;

import heelenyc.commonlib.HexUtils;


/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public class RequestBytePacket extends TcpBytePacket{

    @Override
    public String toString() {
        return "RequestBytePacket [header=" + HexUtils.printHexString(super.getHeader()) + ", body=" + HexUtils.printHexString(super.getBody()) + "]";
    }
}
