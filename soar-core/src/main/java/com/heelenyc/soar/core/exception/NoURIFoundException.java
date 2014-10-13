package com.heelenyc.soar.core.exception;

/**
 * @author yicheng
 * @since 2014年10月13日
 *
 */
public class NoURIFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8243213623774166104L;

    /**
     * 
     */
    public NoURIFoundException(String uri) {
        super("cannot found " + uri);
    }
}
