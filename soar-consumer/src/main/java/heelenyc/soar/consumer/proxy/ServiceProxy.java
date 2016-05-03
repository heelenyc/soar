package heelenyc.soar.consumer.proxy;

import heelenyc.soar.consumer.api.IConsumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public class ServiceProxy {

    /**
     * @param api
     * @return
     * @throws ClassNotFoundException
     */
    public static Object newInstance(IConsumer consumer) throws ClassNotFoundException {
        Class<?> api = Class.forName(consumer.getApi());
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { api }, new ProxyHandler(consumer));
        //return Proxy.newProxyInstance(api.getClassLoader(), new Class<?>[] { api }, new ProxyHandler(consumer));
    }

    private static class ProxyHandler implements InvocationHandler {

        private IConsumer consumer;

        /**
         * @param soarConsumer
         */
        public ProxyHandler(IConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return consumer.callMethod(method, args);
        }

    }
}
