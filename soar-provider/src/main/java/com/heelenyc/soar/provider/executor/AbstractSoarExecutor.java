package com.heelenyc.soar.provider.executor;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.heelenyc.commonlib.ClassUtils;
import com.heelenyc.commonlib.LogUtils;
import com.heelenyc.soar.api.bean.Request;
import com.heelenyc.soar.api.bean.Response;
import com.heelenyc.soar.api.bean.Response.ResponseCode;

/**
 * @author yicheng
 * @since 2016年3月18日
 * 
 */
public abstract class AbstractSoarExecutor implements IExecutor<Request> {

    private Logger logger = LogUtils.getLogger(AbstractSoarExecutor.class);

//    public AbstractSoarExecutor() {
//    }

    @Override
    public Response executor(Request request, Method method, Object imlObject) {
        Object data = null;
        Response resp = new Response();
        try {
            // 参数匹配的问题, 主要是pojo类参数
            List<Object> params = new ArrayList<Object>();
            Object[] rawParams = request.getParams().toArray();
            Class<?>[] paramsTypes = method.getParameterTypes();

            //logger.info("rawParams: " + JsonUtils.toJSON(rawParams));
            //logger.info("paramsTypes: " + JsonUtils.toJSON(paramsTypes));
            
            if (rawParams == null) {
                throw new IllegalArgumentException();
            }
            
            if (paramsTypes.length != rawParams.length) {
                throw new IllegalArgumentException();
            }
            
            for (int i = 0; i < paramsTypes.length; i++) {
                if (!ClassUtils.isCommonPrimitive(paramsTypes[i])) {
                    logger.info("type " + paramsTypes[i].getName() + " is not isPrimitive!");
                    
                    BeanInfo beanInfo = Introspector.getBeanInfo(paramsTypes[i]);  
                    Object obj = paramsTypes[i].newInstance();
                    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
          
                    @SuppressWarnings("unchecked")
                    Map<String, Object>map = (Map<String, Object>) rawParams[i];
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
                }else {
                    params.add(rawParams[i]);
                }
            }

            data = method.invoke(imlObject, params.toArray());

            resp.setData(data);
            resp.setEc(ResponseCode.OK.getValue());
            resp.setEm("OK");
        } catch (IllegalArgumentException e) {
            resp.setData(null);
            resp.setEc(ResponseCode.INVALID_PARAMS.getValue());
            resp.setEm(e.getMessage());
            LogUtils.error(logger, e, "IllegalArgumentException request : {0}", request);
        } catch (InvocationTargetException e) {
            resp.setData(null);
            resp.setEc(-1);
            resp.setEm(e.getMessage());
            LogUtils.error(logger, e, "InvocationTargetException request : {0}", request);
        } catch (IllegalAccessException e) {
            resp.setData(null);
            resp.setEc(-1);
            resp.setEm(e.getMessage());
            LogUtils.error(logger, e, "IllegalAccessException request : {0}", request);
        } catch (Exception e) {
            resp.setData(null);
            resp.setEc(-1);
            resp.setEm(e.getMessage());
            LogUtils.error(logger, e, "Exception request : {0}", request);
        }
        return resp;
    }

}
