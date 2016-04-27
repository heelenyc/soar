package com.heelenyc.soar;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class Response {
    
    private int ec;
    private String em;
    
    private Object data;

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
        return "Response [ec=" + ec + ", em=" + em + ", data=" + data + "]";
    }
    
    public enum ResponseCode{
        
        OK(200),INVALID_PARAMS(401),INVALID_URI(402),SERVER_ERORR(403);
        
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
    
    
}
