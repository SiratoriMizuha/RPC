package rpc.common.service;

import rpc.common.service.impl.JsonSerializer;
import rpc.common.service.impl.KryoSerializer;

public interface CommonSerializer {
    byte[] serialize(Object obj);
    Object deserialize(byte[] bytes,Class<?> clazz);
    int getCode();
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
