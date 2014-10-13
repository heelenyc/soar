package com.heelenyc.soar.core.api;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public final class Constants {

    public static final String SYSTEM_NAME = "MOA";
    public static final String MOA_VERSION = "1.2.4";

    // File names
    public static final String FILE_MOA_LOG = "moa_log4j.properties";
    public static final String FILE_MOA_SERVER = "moa_server.properties";
    public static final String FILE_MOA_CLIENT = "moa_client.properties";

    // Log file
    public static final String FILE_MOA_LOG_STAT_PREFIX = "moa-stat-";

    // Protocols
    public static final String PROTOCOL_MEMCACHED = "memcached";
    public static final String PROTOCOL_TCP = "tcp";
    public static final String PROTOCOL_REDIS = "redis";
    public static final String PROTOCOLS = "memcached,tcp,redis";

    // Keys
    public static final String KEY_MOA_PORT = "moaPort";
    public static final String KEY_MOA_LOG_PATH = "moaLogPath";
    public static final String KEY_PREFIX_ROOT = "/";
    public static final String KEY_PREFIX_SERVICE = "/service/";
    public static final String KEY_PREFIX_DAO = "/dao/";
    public static final String KEY_PUBLISH_DELAY = "moaPublishDelay";

    public static final String KEY_RUN_MODE = "runMode";
    public static final String KEY_RECOVERY_HOSTPORTS = "recoveryHostPorts";
    public static final String KEY_INTERFACE = "interface";
    public static final String KEY_INSTANCE = "instance";
    public static final String KEY_EXCHANGE_PATTERN = "exchangePattern";
    public static final String KEY_PROTOCOL = "protocol";
    public static final String KEY_PROTOCOLS = "protocols";
    public static final String KEY_MOA_PROTOCOL_VERSION = "version";
    public static final String KEY_SERIALIZE_TYPE = "serializeType";
    public static final String KEY_CORE_POOL_SIZE = "corePoolSize";
    public static final String KEY_MAX_POOL_SIZE = "maxPoolSize";
    public static final String KEY_ASYNC_WORKER_COUNT = "asyncWorkerCount";
    public static final String KEY_ROUTING_STRATEGY = "routingStrategy";
    public static final String KEY_CALLBACK_HANDLER = "callbackHandler";
    public static final String KEY_PROCESS_TIMEOUT = "processTimeout";
    public static final String KEY_TIMEOUT = "timeout";
    public static final String KEY_TARGET_URIS = "targetUris";
    public static final String KEY_ROUTING_ID = "routingId";
    public static final String KEY_ROUTER = "router";
    public static final String KEY_HASH_ALGORITHM = "hashAlgorithm";
    public static final String KEY_SOURCE = "source";

    // Values
    public static final int VAL_PORT = 10000;
    public static final int VAL_PORT_MEMCACHED = VAL_PORT;
    public static final int VAL_PORT_TCP = VAL_PORT + 5;
    public static final String VAL_LOG_PATH = "/home/logs/moa/";
    public static final String VAL_RUN_MODE_TEST = "TEST";
    public static final String VAL_RUN_MODE_ONLINE = "ONLINE";
    public static final int VAL_CORE_POOL_SIZE = 10;
    public static final int VAL_MAX_POOL_SIZE = 600;
    public static final int VAL_ASYNC_WORKER_COUNT = 100;
    public static final int VAL_CONN_TIMEOUT = 3000;
    public static final int VAL_PROCESS_TIMEOUT = 1000;
    public static final int VAL_TIMEOUT = 3000;
    public static final String VAL_EXCHANGE_PATTERN_ONEWAY = "one-way";
    public static final String VAL_EXCHANGE_PATTERN_SYNC = "sync";
    public static final String VAL_EXCHANGE_PATTERN_ASYNC = "async";
    public static final String VAL_EXCHANGE_PATTERN_CALLBACK = "callback";
    public static final String VAL_ROUTER_DIRECTED = "directed";
    public static final String VAL_HASH_ALGORITHM_MOD = "MOD";
    public static final String VAL_HASH_ALGORITHM_KETAMA = "KETAMA_HASH";

    // Keys for data set in {@link MOAContext}
    public static final String KEY_STORE_DEFAULT = "default";
    public static final String KEY_STORE_PROTOCOLS = "_protocols";
    public static final String KEY_STORE_CONSUMER_METADATAS = "_consumer_metadatas";
    public static final String KEY_STORE_CONSUMER_SERVICES = "_consumer_services";
    public static final String KEY_STORE_PROVIDER_METADATAS = "_provider_metadatas";
    public static final String KEY_STORE_PROVIDER_SERVICES = "_provider_services";
    public static final String KEY_STORE_THREAD_POOL = "_thread_pool";

    // IO settings
    public static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int IO_THREAD_COUNT = PROCESSOR_COUNT * 2;

    // MOA protocol header
    public static final byte MOA_PROTOCOL_VERSION_1 = 1;
    public static final byte MOA_PROTOCOL_VERSION_2 = 2;
    public static final byte MOA_PROTOCOL_VERSION = MOA_PROTOCOL_VERSION_2;

    public static final byte HDR_TYPE_PING = 0;
    public static final byte HDR_TYPE_PONG = 1;
    public static final byte HDR_TYPE_REQUEST = 2;
    public static final byte HDR_TYPE_RESPONSE = 3;
    public static final byte HDR_EXCHANGE_ONE_WAY = 0;
    public static final byte HDR_EXCHANGE_SYNC = 1;
    public static final byte HDR_EXCHANGE_CALLBACK = 2;
    public static final byte HDR_SERIALIZE_TYPE_HESSIAN = 0;
    public static final byte HDR_SERIALIZE_TYPE_JAVA = 1;
    public static final byte HDR_SERIALIZE_TYPE_JSON = 2;
    
    public static final byte JSON_COMMAND_START = (byte) '{';

    // Request type
    public static final int REQ_COMMAND = 1;
    public static final int REQ_SERVICE = 2;

    private Constants() {
    }
}
