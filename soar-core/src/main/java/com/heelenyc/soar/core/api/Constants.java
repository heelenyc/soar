package com.heelenyc.soar.core.api;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public final class Constants {
    
    public static final String PROVIDER_SERVICE_METADATA = "provider_service_metadata";
    public static final String CONSUMER_SERVICE_METADATA = "consumer_service_metadata";
    public static final String CONFIG_SERVICE_PREFIX = "/";
    public static final String CONFIG_INTERFACE = "interface";
    
    public static final int DEFAULT_TIMEOUT_INMS = 10 * 1000;
    
    public static final String TARGETURI_FORMAT = "moa://%s:%s%s";

    private Constants() {
    }
}
