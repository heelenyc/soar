package com.heelenyc.soar.consumer.service;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.soar.core.exception.InitializationException;

/**
 * @author yicheng
 * @since 2014年10月14日
 * 
 */
public class PropertyBasedConsumerServiceFactory extends ConsumerServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PropertyBasedConsumerServiceFactory.class);
    private Properties props;

    public PropertyBasedConsumerServiceFactory(String propertyFile) throws InitializationException {
        props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile));
        } catch (IOException e) {
            logger.error("ioexcetion for " + propertyFile, e);
        }
        
    }
}
