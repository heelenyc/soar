package heelenyc.soar.core.api.bean;

import java.io.Serializable;


/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class Request implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id = System.nanoTime();

    // for to call
    private String serviceURI;
    private String method;
    private Object[] params;
    private int protocol; // java 和 非 java 的

    // additinal info
    private String source; // source host

    public String getServiceURI() {
        return serviceURI;
    }

    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer hashKey() {
        return (getServiceURI() +"@"+ getSource()).hashCode();
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object... params) {
        this.params = params;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Request [id=" + id + ", serviceURI=" + serviceURI + ", method=" + method + ", params=" + params + ", protocol=" + protocol + ", source=" + source + "]";
    }

    public void setId(Long id) {
        this.id = id;
    }


}
