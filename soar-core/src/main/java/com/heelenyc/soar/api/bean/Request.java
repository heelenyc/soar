package com.heelenyc.soar.api.bean;

import java.util.List;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public class Request {

    // for to call
    private String serviceURI;
    private String method;
    private List<Object> params;
    
    // additinal info
    private String source;  // source host

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

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Request [serviceURI=" + serviceURI + ", method=" + method + ", params=" + params + ", source=" + source + "]";
    }
    
}
