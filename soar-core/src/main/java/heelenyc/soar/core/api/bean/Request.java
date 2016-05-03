package heelenyc.soar.core.api.bean;


/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class Request {

    // for to call
    private String serviceURI;
    private String method;
    private Object params;
    private int protocol; // java 和 非 java 的

//    public Request(int protocol){
//        setProtocol(protocol);
//    }
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

    @Override
    public String toString() {
        return "Request [serviceURI=" + serviceURI + ", method=" + method + ", params=" + params + ", source=" + source + "]";
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

    public void setParams(Object params) {
        this.params = params;
    }


}
