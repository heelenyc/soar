package heelenyc.soar.consumer;

import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.hash.IHashLocator;
import heelenyc.commonlib.hash.KetamaHashLocator;
import heelenyc.commonlib.hash.ModLocator;
import heelenyc.soar.consumer.proxy.DynamicProxy;
import heelenyc.soar.core.keeper.SoarKeeperManager;
import heelenyc.soar.core.keeper.listner.AbstractServiceListner;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * 需要关注服务，并且还要代理接口
 * @author yicheng
 * @since 2016年4月28日
 * 
 */
public class SoarConsumer {

    private Logger logger = LogUtils.getLogger(SoarConsumer.class);
    private IHashLocator nodeLocator;
    private AbstractServiceListner listner;
    private String consumUri;
    private String apiClassName;
    
    // 单例
    private volatile Object instance;
    
    @SuppressWarnings("unchecked")
    public <T> T getInstance(){
        return (T) instance;
    }

    /**
     * 
     */
    public SoarConsumer(String uri, String hashModel,String apiClassName) {
        try {
            List<String> serviceAddressList = SoarKeeperManager.getServiceAddress(uri);
            
            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                LogUtils.warn(logger, "cannot get any instance for service uri {0}", uri);
                // throw new RuntimeException("cannot get any instance for service");
            } else {
                LogUtils.warn(logger, "cannot get instance for service uri {0} : {1}", uri, serviceAddressList);
            }
            this.consumUri = uri;
            
            if ("MOD".equalsIgnoreCase(hashModel)) {
                nodeLocator = new ModLocator(serviceAddressList);
                LogUtils.info(logger, "create MOD hashLocator");
            } else {
                nodeLocator = new KetamaHashLocator(serviceAddressList);
                LogUtils.info(logger, "create Ketama hashLocator");
            }
            
            // 增加 listner
            listner = new AbstractServiceListner(consumUri) {
                
                @Override
                public void onRecover(String uri, String hostport) {
                    LogUtils.info(logger, "onRecover {0} {1}", uri,hostport);
                    nodeLocator.addNode(hostport);
                }
                
                @Override
                public void onPublish(String uri, String hostport) {
                    LogUtils.info(logger, "onPublish {0} {1}", uri,hostport);
                    nodeLocator.addNode(hostport);
                }
                
                @Override
                public void onIsolate(String uri, String hostport) {
                    LogUtils.info(logger, "onIsolate {0} {1}", uri,hostport);
                    nodeLocator.removeNode(hostport);
                }
            };
            
            this.apiClassName =apiClassName;
            
            instance = DynamicProxy.newInstance(apiClassName);
                    
        } catch (Exception e) {
            LogUtils.error(logger, e, "construct SoarConsumer error for {0} {1}", uri, hashModel);
            System.exit(0);
        }
    }

    public AbstractServiceListner getListner() {
        return listner;
    }

    public String getApiClassName() {
        return apiClassName;
    }

    public void setApiClassName(String apiClassName) {
        this.apiClassName = apiClassName;
    }
    
}
