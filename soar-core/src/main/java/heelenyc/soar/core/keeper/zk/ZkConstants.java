package heelenyc.soar.core.keeper.zk;

import heelenyc.soar.core.api.bean.ProtocolToken;

/**
 * @author yicheng
 * @since 2016年5月6日
 * 
 */
public class ZkConstants {
    
    // 节点状态
    public static final String STATE_WORKING = "working";
    public static final String STATE_ISOLATED = "isolated";

    // 域名
    public static final String NAMESPACE_SERVICE = "service";
    // 一级节点：服务uri  注意uri是带/分级的
    // 二级节点
    public static final String PATH_PROTOCOL_TCP = "tcp";
    public static final String PATH_PROTOCOL_REDIS = "redis";

    // 三级节点：hostport
    /**
     * @param targetUri
     * @param localHostport
     * @param protocol
     * @return
     */
    public static String getServiceNodePath(String targetUri, String hostport, int protocol) {
        return targetUri + "/" + getProtocolPath(protocol) + "/" + hostport;
    }
    
    public static String getServicePath(String targetUri, int protocol) {
        return targetUri + "/" + getProtocolPath(protocol) ;
    }

    private static String getProtocolPath(int protocol) {
        if (protocol == ProtocolToken.JAVA) {
            return PATH_PROTOCOL_TCP;
        } else if (protocol == ProtocolToken.JAVA) {
            return PATH_PROTOCOL_REDIS;
        } else {
            return "unknow";
        }
    }
}
