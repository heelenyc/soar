package heelenyc.soar.consumer.api;

import java.lang.reflect.Method;

/**
 * @author yicheng
 * @since 2016年4月29日
 * 
 */
public interface IConsumer {

    String getUri();

    String getApi();

    Object callMethod(Method method, Object[] args);

}
