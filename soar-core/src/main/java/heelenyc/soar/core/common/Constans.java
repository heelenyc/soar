package heelenyc.soar.core.common;

/**
 * @author yicheng
 * @since 2014年9月18日
 * 
 */
public final class Constans {

    // 最大消息帧长度 1m
    public static final int MESSAGE_MAX_FRAME_LENGTH = 1024 * 1024;
    // 消息长度字段的偏移位置
    public static final int MESSAGE_LENGTH_FIELD_OFFSET = 4;
    // 消息包中的长度字段字节数
    public static final int MESSAGE_LENGTH_FIELD_LENGTH = 4;

    // 附件的字符编码
    public static final String ATTACHMENT_KEY_CHARACTSET = "UTF-8";

    // public static final int NET_CONF_BACKLOG = 100;
    // public static final int NET_CONF_READ_TIMEOUT = 600;
    //
    // public static final String REMOTEIP = "127.0.0.1";
    // public static final int PORT = 18080;
    // public static final int LOCAL_PORT = 12088;
    // public static final String LOCAL_IP = "127.0.0.1";
    //

    //
    // public static final long HEARTBEAT_PERIOD_INMS = 60000;
    //
    // public static final String[] WHITELIST = { "localhost", "127.0.0.1" };
    // public static final String[] BLACKLIST = { "192.168.0.1" };
    // public static final boolean IS_WHITELIST_FIRST = true;
    // public static final boolean IS_OTHER_ALLOWED = true;
    //

}
