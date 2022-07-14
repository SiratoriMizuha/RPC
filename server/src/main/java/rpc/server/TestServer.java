package rpc.server;

import com.alibaba.nacos.api.exception.NacosException;
import rpc.common.service.HelloService;
import rpc.common.service.impl.KryoSerializer;
import rpc.server.serverservice.impl.HelloImpl;
import rpc.server.serverservice.impl.NettyServer;
import rpc.server.serverservice.ServiceScan;

/**
 * Hello world!
 *
 */
@ServiceScan
public class TestServer
{
    public static void main(String[] args) throws NacosException {
        HelloService helloService = new HelloImpl();
//        ServiceProvider serviceProvider =new ServiceProviderImpl();
//        serviceProvider.register(helloService);
        NettyServer server=new NettyServer("127.0.0.1",9000);
        server.start();
    }
}
