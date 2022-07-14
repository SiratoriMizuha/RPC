package rpc.server.threadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.ResponseCode;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public Object handle(RpcRequest rpcRequest,Object service){
        Object result=null;
        try{
            result=invokeTargetMethod(rpcRequest,service);
        }catch (IllegalAccessException | InvocationTargetException e){
            logger.error("调用或发送时有错误发生：", e);
        }return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service)throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            System.out.println(service.getClass().getName());
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service,rpcRequest.getParameters());
    }
}
