package rpc.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.ResponseCode;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

public class RcpMessageChecker {
    public static final String INTERFACE_NAME="interfaceName";
    private static final Logger logger= LoggerFactory.getLogger(RcpMessageChecker.class);

    private RcpMessageChecker(){

    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if (rpcResponse==null){
            logger.error("调用服务失败，serviceName:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getStatusCode()==null||!rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())){
            logger.error("服务调用失败,serviceName:{},RpcResponse:{}",rpcRequest.getInterfaceName(),rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
    }
}
