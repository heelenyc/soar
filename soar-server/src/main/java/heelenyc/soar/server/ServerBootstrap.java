package heelenyc.soar.server;

import heelenyc.soar.server.demo.api.IDemoInterface;

import com.heelenyc.soar.provider.SoarProvider;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class ServerBootstrap {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SoarProvider provider = new SoarProvider("/test", "127.0.0.1:18188", IDemoInterface.class.getName(), new DemoImplement());
        provider.start();
    }

}
