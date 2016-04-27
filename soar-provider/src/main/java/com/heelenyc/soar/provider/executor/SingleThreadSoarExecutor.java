package com.heelenyc.soar.provider.executor;

import com.heelenyc.soar.Request;
import com.heelenyc.soar.Response;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public class SingleThreadSoarExecutor extends AbstractSoarExecutor {

    //private Logger logger = LogUtils.getLogger(SingleThreadSoarExecutor.class);


    public SingleThreadSoarExecutor(Class<?> api, Object imlObject) {
        super(api, imlObject);
    }

    @Override
    public Response executor(Request request) {
        return super.executor(request);
    }

}
