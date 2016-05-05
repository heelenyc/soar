package heelenyc.soar.provider.remote.global;

import heelenyc.soar.provider.remote.api.ClientInfo;
import io.netty.util.AttributeKey;

/**
 * @author yicheng
 * @since 2016年1月12日
 *
 */
public class Constants {

    public static final String CLIENTINFO_CHL_ATTR = "clientinfo";
    public static final String CLIENTADDR_CHL_ATTR = "clientaddr";
    
    public static final AttributeKey<ClientInfo> CLIENT_INFO_KEY = AttributeKey.valueOf(Constants.CLIENTINFO_CHL_ATTR);
    public static final AttributeKey<String> CLIENT_ADDR_KEY = AttributeKey.valueOf(Constants.CLIENTADDR_CHL_ATTR);
}
