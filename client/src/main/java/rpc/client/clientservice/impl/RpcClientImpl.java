package rpc.client.clientservice.impl;

import rpc.client.clientservice.RpcClient;
import rpc.common.service.model.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClientImpl{
    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try(Socket socket=new Socket(host,port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }

    }
}
