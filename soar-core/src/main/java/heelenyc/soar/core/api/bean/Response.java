package heelenyc.soar.core.api.bean;

import java.io.Serializable;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class Response implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public transient static Response TIMEOUT_RESP = new Response(ResponseCode.TIME_OUT.getValue(),"timeout");
    public transient static Response ERROR_RESP = new Response(ResponseCode.SERVER_ERORR.getValue(),"server error");
    
    private Long id;
    private int ec;
    private String em;
    private int protocol; // java 和 非 java 的
    
    private Object data;

    public Response(){};
    /**
     * @param value
     * @param string
     */
    public Response(int ec, String em) {
        this.ec = ec;
        this.em = em;
    }

    public int getEc() {
        return ec;
    }

    public void setEc(int ec) {
        this.ec = ec;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response [id=" + id + ", ec=" + ec + ", em=" + em + ", protocol=" + protocol + ", data=" + data + "]";
    }
    
    public enum ResponseCode{
        
        OK(200),INVALID_PARAMS(401),INVALID_URI(402),SERVER_ERORR(403),TIME_OUT(405);
        
        int code;
        /**
         * 
         */
        private ResponseCode(int code) {
            this.code = code;
        }
        
        public int getValue(){
            return this.code;
        }
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
}
