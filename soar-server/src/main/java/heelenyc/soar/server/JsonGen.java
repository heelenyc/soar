package heelenyc.soar.server;

import heelenyc.soar.server.demo.api.ParamsBean;

import java.util.ArrayList;
import java.util.List;

import com.heelenyc.commonlib.JsonUtils;
import com.heelenyc.soar.Request;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class JsonGen {

    public static void main(String[] args) {
        Request request = new Request();
        request.setMethod("add");
        List<Object> params = new ArrayList<Object>();
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        params.add(bean);
        request.setParams(params);
        request.setServiceURI("/test");
        request.setSource("127.0.0.1");

        System.out.println(JsonUtils.toJSON(request));
    }
}
