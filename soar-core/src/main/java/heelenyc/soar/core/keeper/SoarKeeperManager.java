package heelenyc.soar.core.keeper;

import heelenyc.commonlib.LogUtils;
import heelenyc.commonlib.StringUtils;
import heelenyc.soar.core.keeper.zk.SimpleZKUtils;
import heelenyc.soar.core.keeper.zk.ZKClientFactory;
import heelenyc.soar.core.keeper.zk.ZkConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class SoarKeeperManager {
    
    private static Logger logger = LogUtils.getLogger(SoarKeeperManager.class);

    public static boolean isValidUri(String uri){
        if (StringUtils.isNotEmpty(uri) && uri.startsWith("/") && !uri.endsWith("/")) {
            return true;
        }else {
            return false;
        }
    }
    /**
     * @param targetUri
     * @param localHostport
     * @throws Exception
     */
    public static void publisService(String targetUri, String localHostport, int protocol) throws Exception {
        // 发布服务
        if (!isValidUri(targetUri)) {
            throw new RuntimeException("invalid servier uri : " + targetUri);
        }
        
        LogUtils.info(logger, "publisService targetUri={0} hostport={1} protocol={2}", targetUri,localHostport,protocol);
        CuratorFramework client = ZKClientFactory.getZooKeeperClient(ZkConstants.NAMESPACE_SERVICE);

        SimpleZKUtils.replaceEphemeral(client, ZkConstants.getServiceNodePath(targetUri, localHostport, protocol), System.currentTimeMillis() + "");
    }

    /**
     * @param uri
     * @return
     * @throws Exception
     */
    public static List<String> getServiceAddress(String uri, int protocol) throws Exception {
        
        CuratorFramework client = ZKClientFactory.getZooKeeperClient(ZkConstants.NAMESPACE_SERVICE);
        List<String> children = new ArrayList<String>();
        children = client.getChildren().forPath(ZkConstants.getServicePath(uri, protocol));
        return children;
    }

}
