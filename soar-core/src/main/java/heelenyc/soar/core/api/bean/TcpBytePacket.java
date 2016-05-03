package heelenyc.soar.core.api.bean;

import heelenyc.commonlib.HexUtils;


/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public abstract class TcpBytePacket {

    private byte[] header;
    private byte[] body;

    public TcpBytePacket() {
    }

    public TcpBytePacket(byte[] header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
//        return "MsgBytePacket [header=" + Arrays.toString(header) + ", body=" + Arrays.toString(body) + "]";
        return "ResponseBytePacket [header=" + HexUtils.printHexString(header) + ", body=" + HexUtils.printHexString(body) + "]";
    }
}
