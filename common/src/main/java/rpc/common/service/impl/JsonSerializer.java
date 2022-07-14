package rpc.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.SerializerCode;
import rpc.common.service.CommonSerializer;
import rpc.common.service.model.RpcRequest;

import java.io.IOException;
import java.io.Serializable;

public class JsonSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时出现错误：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj=objectMapper.readValue(bytes,clazz);
            if (obj instanceof RpcRequest){
                obj=handleRequest(obj);
            }
            return obj;
        }catch (IOException e){
            logger.error("反序列化失败：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    private Object handleRequest(Object obj) throws IOException{
        RpcRequest rpcRequest=(RpcRequest) obj;
        for (int i=0;i<rpcRequest.getParameters().length;i++){
            Class<?> clazz=rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes=objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i]=objectMapper.readValue(bytes,clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}
