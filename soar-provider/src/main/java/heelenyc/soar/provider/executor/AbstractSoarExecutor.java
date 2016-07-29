package heelenyc.soar.provider.executor;

import heelenyc.commonlib.ClassUtils;
import heelenyc.commonlib.JsonUtils;
import heelenyc.commonlib.LogUtils;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
import heelenyc.soar.core.api.bean.Response.ResponseCode;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public abstract class AbstractSoarExecutor implements IExecutor<Request> {

    private Logger logger = LogUtils.getLogger(AbstractSoarExecutor.class);

    // 执行线程池
    private ExecutorService executor;

    public AbstractSoarExecutor(int threadPoolSize) {
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response executor(final Request request, final Method method, final Object imlObject) {

        Future<Response> future = executor.submit(new Callable<Response>() {

            @Override
            public Response call() throws Exception {
                Object data = null;
                Response resp = new Response();
                resp.setProtocol(request.getProtocol());
                resp.setId(request.getId());
                try {

                    if (method == null) {
                        throw new IllegalArgumentException("cannot find method");
                    }

                    if (request.getProtocol() == ProtocolToken.JAVA) {
                        Class<?>[] paramsTypes = method.getParameterTypes();
                        Object[] rawParams = null;
                        if (request.getParams() != null) {
                            rawParams = (Object[]) request.getParams();
                        }
                        if ((rawParams != null && paramsTypes.length != rawParams.length) || (rawParams == null && paramsTypes.length != 0)) {
                            throw new IllegalArgumentException();
                        }
                        // 调用真正的服务
                        if (rawParams != null) {
                            data = method.invoke(imlObject, rawParams);
                        } else {
                            data = method.invoke(imlObject);
                        }

                        resp.setData(data);
                        resp.setEc(ResponseCode.OK.getValue());
                        resp.setEm("OK");

                    } else {
                        // redis 协议 参数匹配的问题, 主要是pojo类参数
                        List<Object> params = new ArrayList<Object>();
                        Object[] rawParams = null;
                        if (request.getParams() != null) {
                            rawParams = (Object[]) request.getParams();
                        }
                        Class<?>[] paramsTypes = method.getParameterTypes();

                        // logger.info("rawParams: " +
                        // JsonUtils.toJSON(rawParams));
                        // logger.info("paramsTypes: " +
                        // JsonUtils.toJSON(paramsTypes));

                        if ((rawParams != null && paramsTypes.length != rawParams.length) || (rawParams == null && paramsTypes.length != 0)) {
                            throw new IllegalArgumentException();
                        }

                        for (int i = 0; i < paramsTypes.length; i++) {
                            if (!ClassUtils.isCommonPrimitive(paramsTypes[i])) {
                                // TODO 有可能是 数组 map list set pojo等,
                                if (List.class.isAssignableFrom(paramsTypes[i])) {
                                    // list
                                    logger.info("param type at " + i + " for method " + method.getName() + " : " + paramsTypes[i].getName() + " is list!");
                                    params.add(rawParams[i]);
                                } else if (Map.class.isAssignableFrom(paramsTypes[i])) {
                                    // map
                                    logger.info("param type at " + i + " for method " + method.getName() + " : " + paramsTypes[i].getName() + " is map!");
                                    params.add(rawParams[i]);
                                } else if (Set.class.isAssignableFrom(paramsTypes[i])) {
                                    // set
                                    logger.info("param type at " + i + " for method " + method.getName() + " : " + paramsTypes[i].getName() + " is map!");
                                    params.add(rawParams[i]);
                                } else if (paramsTypes[i].isArray()) {
                                    // 数组
                                    logger.info("param type at " + i + " for method " + method.getName() + " : " + paramsTypes[i].getName() + " is array!");
                                    params.add(rawParams[i]);
                                } else {
                                    // pojo 类
                                    logger.info("param type at " + i + " for method " + method.getName() + " : " + paramsTypes[i].getName() + " is not isPrimitive!");

                                    BeanInfo beanInfo = Introspector.getBeanInfo(paramsTypes[i]);
                                    Object obj = paramsTypes[i].newInstance();
                                    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                                    Map<String, Object> map = (Map<String, Object>) rawParams[i];
                                    for (PropertyDescriptor property : propertyDescriptors) {
                                        String key = property.getName();
                                        if (map.containsKey(key)) {
                                            Object value = map.get(key);
                                            // 得到property对应的setter方法
                                            Method setter = property.getWriteMethod();
                                            setter.invoke(obj, value);
                                        }
                                    }
                                    params.add(obj);
                                }
                            } else {
                                // 基础类型或者封装类型 直接添加
                                params.add(rawParams[i]);
                            }
                        }

                        // 调用真正的服务
                        if (paramsTypes.length == 0) {
                            data = method.invoke(imlObject);
                        } else {
                            data = method.invoke(imlObject, params.toArray());
                        }
                        // data可能是任何类型数据，但是json协议出去之后，只能在客户端反序列化，根据情况而定

                        if (request.getProtocol() == ProtocolToken.JAVA) {
                            resp.setData(data);
                        } else {
                            Class<?> returnType = method.getReturnType();
                            if (!ClassUtils.isCommonPrimitive(returnType)) {
                                LogUtils.info(logger, "returnType for {0} is not isCommonPrimitive", request);
                                resp.setData(JsonUtils.toJSON(data));
                            } else {
                                resp.setData(data);
                            }
                        }
                        resp.setEc(ResponseCode.OK.getValue());
                        resp.setEm("OK");
                    }
                } catch (IllegalArgumentException e) {
                    resp.setData("invalid params!");
                    resp.setEc(ResponseCode.INVALID_PARAMS.getValue());
                    resp.setEm(e.getMessage());
                    LogUtils.error(logger, e, "IllegalArgumentException request : {0}", request);
                } catch (InvocationTargetException e) {
                    resp.setData("generic type of pojo is not surpported!");
                    resp.setEc(ResponseCode.SERVER_ERORR.getValue());
                    resp.setEm(e.getMessage());
                    LogUtils.error(logger, e, "InvocationTargetException request : {0}", request);
                } catch (IllegalAccessException e) {
                    resp.setData(null);
                    resp.setEc(ResponseCode.SERVER_ERORR.getValue());
                    resp.setEm(e.getMessage());
                    LogUtils.error(logger, e, "IllegalAccessException request : {0}", request);
                } catch (Exception e) {
                    resp.setData(null);
                    resp.setEc(ResponseCode.SERVER_ERORR.getValue());
                    resp.setEm(e.getMessage());
                    LogUtils.error(logger, e, "Exception request : {0}", request);
                }
                return resp;
            }
        });

        Response resp = null;
        try {
            resp = future.get(ProtocolToken.TIME_OUT_IN_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            resp = Response.ERROR_RESP;
            resp.setId(request.getId());
        } catch (ExecutionException e) {
            resp = Response.ERROR_RESP;
            resp.setId(request.getId());
        } catch (TimeoutException e) {
            logger.error("execute timeout for request :" + request);
            resp = Response.TIMEOUT_RESP;
            resp.setId(request.getId());
        }
        return resp;
    }

}
