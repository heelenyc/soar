package heelenyc.soar.core.serialize;

/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public interface ISerializer {

    byte[] serialize(Object object) throws Exception;
    
    Object deserialize(byte[] bytes) throws Exception;
}
