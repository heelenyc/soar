package heelenyc.soar.consumer.demo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import heelenyc.soar.consumer.SoarConsumer;
import heelenyc.soar.consumer.remote.IRemoteCaller;
import heelenyc.soar.consumer.remote.RedisCaller;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.Response;
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
        testTcp();
        
//         testRedis();
    }

    /**
     * 
     */
    private static void testRedis() {
        try {

            IRemoteCaller redisCaller = new RedisCaller("/test", ProtocolToken.HASH_KETAMA, IDemoInterface.class.getName());

            long start = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                Request request = null;
                try {
                    if (i % 1000 == 0) {
                        System.out.println("+++++++++++++++++++++++++=========>  " + i);
                    }
                        
                    ParamsBean bean = new ParamsBean();
                    bean.setOp1(Math.random() * 100);
                    bean.setOp2(Math.random() * 100);
                    
                    request = new Request();
                    request.setMethod("addInt");
                    request.setParams(1,2);
                    request.setProtocol(ProtocolToken.REDIS);
                    request.setServiceURI("/test");
                    request.setSource("127.0.0.1");
                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
                    
                    TimeUnit.SECONDS.sleep(1);
                    
//                    request = new Request();
//                    request.setMethod("add");
//                    request.setParams(bean);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("addDouble");
//                    request.setParams(1.1d,2.0d);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getDoubleList");
//                    //request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getBeanList");
//                    request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
//                    
//                    request = new Request();
//                    request.setMethod("getDoubleSet");
//                    request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getBeanSet");
//                    request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getDoubleMap");
//                    request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getBeanMap");
//                    request.setParams();
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getDoublePArray");
//                    request.setParams(null);
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
////                    
//                    request = new Request();
//                    request.setMethod("getBeanPArray");
//                    request.setParams();
//                    request.setProtocol(ProtocolToken.REDIS);
//                    request.setServiceURI("/test");
//                    request.setSource("127.0.0.1");
//                    System.out.println(request.getMethod() + ":" + redisCaller.call(request).getData());
//                    
//                  request = new Request();
//                  request.setMethod("addList");
//                  request.setParams(Arrays.asList(bean,bean));
//                  request.setProtocol(ProtocolToken.REDIS);
//                  request.setServiceURI("/test");
//                  request.setSource("127.0.0.1");
//                  System.out.println(request.getMethod() + ":" + redisCaller.call(request));
//                    
//                  request = new Request();
//                  request.setMethod("addListDouble");
//                  request.setParams(Arrays.asList(1.0d,3.0d));
//                  request.setProtocol(ProtocolToken.REDIS);
//                  request.setServiceURI("/test");
//                  request.setSource("127.0.0.1");
//                  System.out.println(request.getMethod() + ":" + redisCaller.call(request));
                    
                    
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

    /**
     * 
     */
    private static void testTcp() {
      try {

          SoarConsumer consumer = new SoarConsumer("/test", ProtocolToken.HASH_MOD, IDemoInterface.class.getName());

          long start = System.currentTimeMillis();
          IDemoInterface service = consumer.getInstance();
          for (int i = 0; i < 10; i++) {
              try {
                  if (i % 1000 == 0) {
                      System.out.println("+++++++++++++++++++++++++=========>  " + i);
                  }
                      
                  ParamsBean bean = new ParamsBean();
                  bean.setOp1(Math.random() * 100);
                  bean.setOp2(Math.random() * 100);
                  
                  System.out.println("addDouble  :" + service.addDouble(1.0d,2.0d));
//                  System.out.println("add  :" + service.add(bean));
//                  System.out.println("addInt  :" + service.addInt(1, 2));
//                  System.out.println("echo  :" + service.echo("hello world!"));
//                  System.out.println("addList  :" + service.addList(Arrays.asList(bean,bean)));
//                  System.out.println("addListDouble  :" + service.addListDouble(Arrays.asList(2.0d,1.0d)));
//                  System.out.println("getBeanList  :" + service.getBeanList());
//                  System.out.println("getBeanMap  :" + service.getBeanMap());
//                  System.out.println("getBeanPArray  :" + service.getBeanPArray());
//                  System.out.println("getBeanSet  :" + service.getBeanSet());
//                  System.out.println("getDoubleList  :" + service.getDoubleList());
//                  System.out.println("getDoubleMap  :" + service.getDoubleMap());
//                  System.out.println("getDoublePArray  :" + service.getDoublePArray());
//                  System.out.println("getDoubleSet  :" + service.getDoubleSet());
                  
                  TimeUnit.SECONDS.sleep(3);
                  
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
