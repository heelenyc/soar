package heelenyc.soar.server.demo.impl;

import java.util.concurrent.TimeUnit;

import heelenyc.soar.core.demo.api.IDemoInterface;
import heelenyc.soar.provider.SoarProvider;

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
    public static void main(String[] args) throws InterruptedException {
        SoarProvider provider1 = new SoarProvider("127.0.0.1",18188);
        provider1.registUri("/test", IDemoInterface.class.getName(), new DemoImplement());
        
        TimeUnit.SECONDS.sleep(20);
        
        SoarProvider provider2 = new SoarProvider("127.0.0.1",18190);
        provider2.registUri("/test", IDemoInterface.class.getName(), new DemoImplement());
    }

}
