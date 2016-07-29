package heelenyc.soar.server.demo.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class DemoServerBootstrap {

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        try {

            // SoarProvider provider1 = new SoarProvider("127.0.0.1",18188);
            // provider1.registUri("/test", IDemoInterface.class.getName(), new
            // DemoImplement());

            // TimeUnit.SECONDS.sleep(20);

            // SoarProvider provider2 = new SoarProvider("127.0.0.1",18190);
            // provider2.registUri("/test", IDemoInterface.class.getName(), new
            // DemoImplement());
            //
            // TimeUnit.SECONDS.sleep(20);
            //
            // SoarProvider provider3 = new SoarProvider("127.0.0.1",18192);
            // provider3.registUri("/test", IDemoInterface.class.getName(), new
            // DemoImplement());

            // spring 方式
            String springXmlFile = "demo-service.xml";
            @SuppressWarnings({ "unused", "resource" })
            ApplicationContext context = new ClassPathXmlApplicationContext(springXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
