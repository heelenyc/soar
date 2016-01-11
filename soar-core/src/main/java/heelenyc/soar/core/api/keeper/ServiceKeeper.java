package heelenyc.soar.core.api.keeper;

import java.util.List;

/**
 * @author yicheng
 * @since 2015年2月6日
 * 
 */
public interface ServiceKeeper {

    List<String> getService();

    boolean publishService();

    boolean stopService(String serviceUri);

    boolean startService(String serviceUri);

    boolean insulateService(String serviceUri);

    boolean recoverService(String serviceUri);
}
