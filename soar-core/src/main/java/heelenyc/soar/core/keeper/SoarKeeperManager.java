package heelenyc.soar.core.keeper;

import heelenyc.soar.core.api.bean.ProtocolToken;

import java.util.Arrays;
import java.util.List;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class SoarKeeperManager {

    /**
     * @param targetUri
     * @param localHostport
     */
    public static void publisService(String targetUri, String localHostport) {

    }

    /**
     * @param uri
     * @return
     */
    public static List<String> getServiceAddress(String uri, int protocol) {
        switch (protocol) {
        case ProtocolToken.JAVA:
            return Arrays.asList("127.0.0.1:18188");
        case ProtocolToken.REDIS:
            return Arrays.asList("127.0.0.1:18189");
        default:
            return Arrays.asList();
        }

    }

}
