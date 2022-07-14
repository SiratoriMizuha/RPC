package rpc.client.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.client.clientservice.RpcClient;
import lombok.AllArgsConstructor;
import rpc.client.clientservice.impl.NettyClient;
import rpc.client.clientservice.impl.RpcClientImpl;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
//        RpcRequest rpcRequest=RpcRequest.builder()
//                .interfaceName(method.getDeclaringClass().getName())
//                .methodName(method.getName())
//                .parameters(args)
//                .paramTypes(method.getParameterTypes())
//                .build();
//        RpcClient rpcClient=new RpcClientImpl();
        RpcRequest rpcRequest=new RpcRequest(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),method.getName(),args,method.getParameterTypes());
//        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();
        return client.sendRequest(rpcRequest);
    }
}
