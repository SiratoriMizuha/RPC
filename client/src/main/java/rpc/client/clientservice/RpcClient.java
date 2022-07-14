package rpc.client.clientservice;

import rpc.common.service.CommonSerializer;
import rpc.common.service.model.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//客户端可以调用的方法接口
public interface RpcClient {
    //向服务器获得方法
    Object sendRequest(RpcRequest rpcRequest);


}
