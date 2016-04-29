package heelenyc.soar.core.keeper;

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
    public static List<String> getServiceAddress(String uri) {
        return Arrays.asList("127.0.0.1:18188");
    }

}
