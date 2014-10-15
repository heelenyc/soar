package com.heelenyc.soar.core.exception;

/**
 * @author yicheng
 * @since 2014年10月15日
 *
 */
public class InvalidTargetUriException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 407484501732168994L;

    public InvalidTargetUriException(String targetUri){
        super("invalid target uri : " + targetUri);
    }
}
