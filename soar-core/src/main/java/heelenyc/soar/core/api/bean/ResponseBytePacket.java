package heelenyc.soar.core.api.bean;

import heelenyc.commonlib.HexUtils;
import heelenyc.soar.core.serialize.SerializeUtils;


/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public class ResponseBytePacket extends TcpBytePacket{


    public static RequestBytePacket emptyReponsePacket = new RequestBytePacket(new byte[]{0,0,0,0,0,0,0,0},new byte[]{});
    /**
     * @param resp
     * @throws Exception 
     */
    public ResponseBytePacket(Response resp) throws Exception {
        byte[] header = new byte[ProtocolToken.HEADER_LENGTH];
        byte[] body = SerializeUtils.serialize(resp);
        
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
    
    public ResponseBytePacket(byte[] headBuffer, byte[] bodyBuffer) {
        setHeader(headBuffer);
        setBody(bodyBuffer);
    }

    @Override
    public String toString() {
        return "ResponseBytePacket [header=" + HexUtils.printHexString(getHeader()) + ", body=" + HexUtils.printHexString(getBody()) + "]";
    }
    
    public Response getBodyAsResponse() throws Exception {
        return (Response) SerializeUtils.deserialize(getBody());
    }
}
