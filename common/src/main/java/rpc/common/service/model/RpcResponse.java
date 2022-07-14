package rpc.common.service.model;

import rpc.common.EnumCode.ResponseCode;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RpcResponse implements Serializable {
    private String requestId;
    //响应状态码
    private Integer statusCode;

    //响应message
    private String message;

    //响应数据
    private Object data;

    public static RpcResponse success(Object data,String requestId){
        RpcResponse rpcResponse=new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setData(data);
        rpcResponse.setStatusCode(ResponseCode.SUCCESS.getCode());
        return rpcResponse;
    }

    public static RpcResponse fail(ResponseCode responseCode){
        RpcResponse rpcResponse=new RpcResponse();
        rpcResponse.setMessage(responseCode.getMessage());
        rpcResponse.setStatusCode(responseCode.getCode());
        return rpcResponse;

    }
}
