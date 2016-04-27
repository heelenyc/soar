package com.heelenyc.soar.provider.executor;

import java.lang.reflect.Method;

import com.heelenyc.soar.api.bean.Request;
import com.heelenyc.soar.api.bean.Response;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public interface IExecutor<T extends Request> {

    Response executor(T request, Method method, Object imlObject);
}
