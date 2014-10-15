package com.heelenyc.soar.core.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2014年10月15日
 *
 */
public class NetUtil {
    
    private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static String getLocalIPAddress() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }
    
    public static void main(String[] args){
        System.out.println(getLocalIPAddress());
    }
}
