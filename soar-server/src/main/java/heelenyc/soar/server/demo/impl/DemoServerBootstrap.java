package heelenyc.soar.server.demo.impl;

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
     */
    public static void main(String[] args) {
        SoarProvider provider = new SoarProvider("127.0.0.1",18188);
        provider.registUri("/test", IDemoInterface.class.getName(), new DemoImplement());
        provider.start();
    }

}
