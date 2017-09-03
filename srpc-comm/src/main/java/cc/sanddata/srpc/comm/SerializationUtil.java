package cc.sanddata.srpc.comm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 
 * @ClassName: SerializationUtil 
 * @Description: 序列化工具，基于ProtoStuff实现
 * @author wanggl
 * @date 2017年8月24日 下午10:28:47 
 *
 */
public class SerializationUtil {
	
	private static Map<Class<?>,Schema<?>> cacheSchema = new ConcurrentHashMap<Class<?>,Schema<?>>();
	
	private static Objenesis objenesis = new ObjenesisStd(true);
			
	private SerializationUtil(){}
	
	/**
	 * 
	 * @Title: getSchema 
	 * @author wanggl
	 * @Description: 获取类的schema
	 * @param @param cls
	 * @param @return 
	 * @return Schema<T>
	 * @throws
	 */
	@SuppressWarnings({ "unchecked"})
	private static <T> Schema<T> getSchema(Class<T> cls) {
		
		Schema<T> schema = (Schema<T>)cacheSchema.get(cls);
		if ( schema == null ) {
			schema = RuntimeSchema.createFrom(cls);
			if (schema!=null){
				cacheSchema.put(cls, schema);
			}
		} 
		
		return schema;
	}
	
	/**
	 * 
	 * @Title: serialize 
	 * @author wanggl
	 * @Description: 序列化  obj==>byteArray
	 * @param @param obj
	 * @param @return 
	 * @return byte[]
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serialize(T obj){
		Class<T> cls = (Class<T>) obj.getClass();
		
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			Schema<T> schema = getSchema(cls);
			return ProtobufIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(),e);
		} finally {
			buffer.clear();
		}
	}
	
	/**
	 * 
	 * @Title: deserialize 
	 * @author wanggl
	 * @Description: 反序列化  字节数组==》对象
	 * @param @param data
	 * @param @param cls
	 * @param @return 
	 * @return T
	 * @throws
	 */
	public static <T> T deserialize(byte[] data,Class<T> cls){
		try {
			T message = (T) objenesis.newInstance(cls);
			Schema<T> schema = getSchema(cls);
			ProtobufIOUtil.mergeFrom(data, message, schema);
			return message;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(),e);
		}
	}
}
