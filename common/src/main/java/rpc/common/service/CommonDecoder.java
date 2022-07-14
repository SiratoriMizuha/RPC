package rpc.common.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.PackageType;
import rpc.common.exception.RpcException;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic=byteBuf.readInt();
        if (magic!=MAGIC_NUMBER){
            logger.error("无法识别的协议包:{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode=byteBuf.readInt();
        Class<?> packageClass;
        if (packageCode== PackageType.REQUEST_PACK.getCode()){
            packageClass= RpcRequest.class;
        }else if (packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass= RpcResponse.class;
        }else {
            logger.error("无法识别的协议包:{}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode=byteBuf.readInt();
        CommonSerializer serializer=CommonSerializer.getByCode(serializerCode);
        if (serializer==null){
            logger.error("无法识别的反序列化器：{}",serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length=byteBuf.readInt();
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes);
        Object obj=serializer.deserialize(bytes,packageClass);
        list.add(obj);
    }
}
