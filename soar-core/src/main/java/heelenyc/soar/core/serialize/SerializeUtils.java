package heelenyc.soar.core.serialize;


/**
 * @author yicheng
 * @since 2016年5月3日
 * 
 */
public class SerializeUtils {
    
    private static ISerializer serializer = new HessianSerializeUtils();
    /**
     * 
     * 纯hessian序列化
     * @param object
     * @return
     * @throws Exception
     */

    public static byte[] serialize(Object object) throws Exception {

        return serializer.serialize(object);

    }

    /**
     * 
     * 纯hessian反序列化
     * @param bytes
     * @return
     * @throws Exception
     */

    public static Object deserialize(byte[] bytes) throws Exception {
        return serializer.deserialize(bytes);
    }
    
    public static void main(String[] args) throws Exception {
        
//        Object obj = new Integer(1);
//        
//        byte[] b = SerializeUtils.serialize(new Integer[]{1,2});
//        
//        System.out.println(b.length);
    }
}
