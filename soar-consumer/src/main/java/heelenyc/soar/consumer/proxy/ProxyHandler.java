package heelenyc.soar.consumer.proxy;

import heelenyc.soar.core.api.bean.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yicheng
 * @since 2016年4月28日
 *
 */
public class ProxyHandler implements InvocationHandler{

    /**
     * @param api
     */
    public ProxyHandler(Class<?> api) {
        
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //  发送reques
        Request req = new 
        return null;
    }
    
}
