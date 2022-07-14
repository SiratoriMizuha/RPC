package rpc.server.threadPool;

import lombok.AllArgsConstructor;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;
import rpc.server.serverservice.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

@AllArgsConstructor
public class RequestHandlerThread implements Runnable{

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest =(RpcRequest) objectInputStream.readObject();
            String interfaceName =rpcRequest.getInterfaceName();
            Object service= serviceProvider.getService(interfaceName);
            Object result=requestHandler.handle(rpcRequest,service);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
