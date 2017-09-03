package cc.sanddata.srpc.comm;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SRPCDecoder extends ByteToMessageDecoder{
	
	private Class<?> genericClass;
	
	public SRPCDecoder(Class<?> genericClass) {
		super();
		this.genericClass = genericClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if(in.readableBytes()<4){
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if(dataLength<0){
			ctx.close();
		}
		if(in.readableBytes() < dataLength){
			in.resetReaderIndex();
		}
		//将ByteBuf转换为byte
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		Object obj = SerializationUtil.deserialize(data, genericClass);
		out.add(obj);
	}

}
