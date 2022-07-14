package rpc.server.serverservice;

import com.alibaba.nacos.api.exception.NacosException;
import rpc.common.service.CommonSerializer;

import java.util.concurrent.*;

public interface RpcServer {
    //向Nacos注册服务
    void publishService(Object service,String ServiceName) throws NacosException;

    void start();

}
