package com.heelenyc.soar.core.service.entity;



/**
 * 服务元数据
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public abstract class ServiceMetaData {

    private String serviceURI;
    private String interfaceName;
    private Class<?> serviceInterface;

    public String getServiceURI() {
        return serviceURI;
    }

    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
}
