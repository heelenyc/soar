package com.heelenyc.soar.provider.executor;

import com.heelenyc.soar.Request;
import com.heelenyc.soar.Response;

/**
 * @author yicheng
 * @since 2016年3月18日
 *
 */
public interface IExecutor<T extends Request> {

    Response executor(T request);
}
