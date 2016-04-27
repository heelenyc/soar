package heelenyc.soar.provider.executor;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;

import java.lang.reflect.Method;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public interface IExecutor<T extends Request> {

    Response executor(T request, Method method, Object imlObject);
}
