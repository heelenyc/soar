package heelenyc.soar.core.api.bean;

/**
 * @author yicheng
 * @since 2016年5月3日
 * 
 */
public class ProtocolToken {

    public static final int JAVA = 1;
    public static final int REDIS = 2;

    public static final int HASH_MOD = 1;
    public static final int HASH_KETAMA = 2;

    public static final int HEADER_LENGTH = 8;

    public static final int TIME_OUT_IN_MS = 3000;
    
    public static final int PREHEATING_TIMESPAN_IN_MS = 20 * 1000;
    
    public static final int THEADPOOL_SIZE = 100;
}
