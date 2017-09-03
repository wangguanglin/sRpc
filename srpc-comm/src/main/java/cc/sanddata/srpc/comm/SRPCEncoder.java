package cc.sanddata.srpc.comm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("rawtypes")
public class SRPCEncoder extends MessageToByteEncoder{
	
	private Class<?> genericClass;
	
	
	public SRPCEncoder(Class<?> genericClass) {
		super();
		this.genericClass = genericClass;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Object inob, ByteBuf out) throws Exception {
		if (genericClass.isInstance(inob)){
			byte[] data = SerializationUtil.serialize(inob);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
		
	}

}
