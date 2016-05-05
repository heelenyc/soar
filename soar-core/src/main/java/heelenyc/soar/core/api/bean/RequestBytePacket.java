package heelenyc.soar.core.api.bean;

import heelenyc.commonlib.HexUtils;
import heelenyc.soar.core.serialize.SerializeUtils;


/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public class RequestBytePacket extends TcpBytePacket{

    /**
     * @param resp
     * @throws Exception 
     */
    public RequestBytePacket(Request req) throws Exception {
        byte[] header = new byte[ProtocolToken.HEADER_LENGTH];
        byte[] body = SerializeUtils.serialize(req);
        
        // 写入长度
        header[0] = ((byte) 0);
        header[1] = ((byte) ((body.length << 16) >> 24));
        header[2] = ((byte) ((body.length << 24) >> 24));
        header[3] = ((byte) 0);
        header[4] = ((byte) 0);
        header[5] = ((byte) 0);
        header[6] = ((byte) 0);
        header[7] = ((byte) 0);
        
        setHeader(header);
        setBody(body);
    }
    /**
     * 
     */
    public RequestBytePacket() {
    }
    /**
     * @param headBuffer
     * @param bodyBuffer
     */
    public RequestBytePacket(byte[] headBuffer, byte[] bodyBuffer) {
        setHeader(headBuffer);
        setBody(bodyBuffer);
    }
    @Override
    public String toString() {
        return "RequestBytePacket [header=" + HexUtils.printHexString(super.getHeader()) + ", body=" + HexUtils.printHexString(super.getBody()) + "]";
    }
    
    public Request getBodyAsRequest() throws Exception {
        return (Request) SerializeUtils.deserialize(getBody());
    }
}
