package rpc.client;

import rpc.client.clientservice.RpcClient;
import rpc.client.clientservice.impl.NettyClient;
import rpc.client.proxy.RpcClientProxy;
import rpc.common.service.ByeService;
import rpc.common.service.CommonSerializer;
import rpc.common.service.HelloService;
import rpc.common.service.impl.KryoSerializer;
import rpc.common.service.model.HelloModel;
/**
 * Hello world!
 *
 */
public class TestClient
{
    public static void main( String[] args )
    {
        NettyClient client=new NettyClient(CommonSerializer.getByCode(0));
        RpcClientProxy rpcClientProxy=new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloModel object = new HelloModel(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));

    }
}
