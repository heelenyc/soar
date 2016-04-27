package com.heelenyc.soar.provider.executor;

import java.lang.reflect.Method;

import com.heelenyc.soar.api.bean.Request;
import com.heelenyc.soar.api.bean.Response;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class SingleThreadSoarExecutor extends AbstractSoarExecutor {

    // private Logger logger =
    // LogUtils.getLogger(SingleThreadSoarExecutor.class);

    public SingleThreadSoarExecutor() {
    }

    @Override
    public Response executor(Request request, Method method, Object imlObject) {
        return super.executor(request, method, imlObject);
    }

}
