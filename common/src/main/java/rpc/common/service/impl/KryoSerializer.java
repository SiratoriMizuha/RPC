package rpc.common.service.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.SerializerCode;
import rpc.common.exception.SerializeException;
import rpc.common.service.CommonSerializer;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
             Output output=new Output(byteArrayOutputStream)){
            Kryo kryo=kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
             Input input=new Input(byteArrayInputStream)){
            Kryo kryo=kryoThreadLocal.get();
            Object o=kryo.readObject(input,clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时发生错误:",e);
            throw new SerializeException("反序列化时发生错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
