package heelenyc.soar.provider.remote.redis.coder;

/**
 * @author yicheng
 * @since 2016年2月4日
 *
 */
public enum RedisProtocolState {

    TO_READ_PREFIX,
    TO_READ_LINENUM,
    TO_READ_ARGS,
}
