package heelenyc.soar.provider.executor;

import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;

import java.lang.reflect.Method;

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
