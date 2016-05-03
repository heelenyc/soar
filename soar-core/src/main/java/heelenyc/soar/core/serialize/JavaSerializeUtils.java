package heelenyc.soar.core.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author yicheng
 * @since 2016年5月3日
 * 
 */
public class JavaSerializeUtils implements ISerializer {
    /**
     * 
     * java序列化
     * 
     * @param obj
     * @return
     * @throws Exception
     */

    public byte[] serialize(Object obj) throws Exception {

        if (obj == null)
            throw new NullPointerException();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(obj);
        return os.toByteArray();
    }

    /**
     * 
     * java反序列化
     * @param by
     * @return
     * @throws Exception
     */

    public Object deserialize(byte[] by) throws Exception {
        if (by == null)
            throw new NullPointerException();
        ByteArrayInputStream is = new ByteArrayInputStream(by);
        ObjectInputStream in = new ObjectInputStream(is);
        return in.readObject();

    }
}
