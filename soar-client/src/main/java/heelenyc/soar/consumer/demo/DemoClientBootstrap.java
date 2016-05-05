package heelenyc.soar.consumer.demo;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
    public static void main(String[] args) {
        try {

            SoarConsumer consumer = new SoarConsumer("/test", "mod", IDemoInterface.class.getName());

            long start = System.currentTimeMillis();
            IDemoInterface service = consumer.getInstance();
            for (int i = 0; i < 10000; i++) {
                try {
                    ParamsBean bean = new ParamsBean();
                    bean.setOp1(Math.random() * 100);
                    bean.setOp2(Math.random() * 100);
                    
                    Double ret = service.addList(Arrays.asList(bean, bean));
                    System.out.println(ret);
                    if (i % 1000 == 0) {
                        System.out.println("+++++++++++++++++++++++++=========>  " + i);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TimeUnit.SECONDS.sleep(1);
                // Thread.sleep(10);
            }
            long end = System.currentTimeMillis();
            System.out.println("cost time : " + (end - start));

            // Double ret = service.addDouble(1.1d,2.1d);

            // System.out.println(Arrays.asList(Class.forName(IDemoInterface.class.getName()).getInterfaces()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
