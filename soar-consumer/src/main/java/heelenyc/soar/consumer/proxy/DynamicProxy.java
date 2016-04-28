package heelenyc.soar.consumer.proxy;

import java.lang.reflect.Proxy;

/**
 * @author yicheng
 * @since 2016年4月28日
 *
 */
public class DynamicProxy {

    /**
     * @param api
     * @return
     * @throws ClassNotFoundException 
     */
    public static Object newInstance(String apiClassName) throws ClassNotFoundException {
        Class<?> api = Class.forName(apiClassName);
        return Proxy.newProxyInstance(api.getClassLoader(), api.getClass().getInterfaces(), new ProxyHandler(api));
    }

}
