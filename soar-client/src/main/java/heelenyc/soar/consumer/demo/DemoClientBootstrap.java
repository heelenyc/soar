package heelenyc.soar.consumer.demo;

import java.util.Arrays;

import heelenyc.soar.consumer.SoarConsumer;
import heelenyc.soar.core.demo.api.IDemoInterface;
import heelenyc.soar.core.demo.api.ParamsBean;

/**
 * @author yicheng
 * @since 2016年4月29日
 *
 */
public class DemoClientBootstrap {

    /**
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws Exception {
        SoarConsumer consumer = new SoarConsumer("/test", "mod", IDemoInterface.class.getName());
        
        IDemoInterface service = consumer.getInstance();
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        
        Double ret = service.addList(Arrays.asList(bean,bean));
        
        System.out.println(ret);
        
        // System.out.println(Arrays.asList(Class.forName(IDemoInterface.class.getName()).getInterfaces()));
        
    }

}
