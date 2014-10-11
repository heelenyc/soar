package com.heelenyc.soar.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2014年10月11日
 *
 */
public abstract class ServiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);


    public ServiceFactory() {

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                logger.info("MOA ServiceFactory shutting down...");
                shutdown();
            }
        });
    }

    /**
     * MOA实例关机时，执行的关闭操作。
     */
    public abstract void shutdown();

}
