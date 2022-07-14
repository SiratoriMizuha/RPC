package rpc.client.clientservice.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.client.clientservice.RpcClient;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.service.*;
import rpc.common.service.impl.NacosServiceDiscovery;
import rpc.common.service.impl.RoundRobinLoadBalancer;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final Bootstrap bootstrap;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public NettyClient(){
        this(CommonSerializer.getByCode(0));
    }
    public NettyClient(CommonSerializer serializer){
        this.serializer=serializer;
        this.serviceRegistry=new NacosServiceDiscovery(new RoundRobinLoadBalancer());
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(final RpcRequest rpcRequest) {
        if (serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object>result=new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress=serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel=ChannelProvider.get(inetSocketAddress,serializer);
            if (channel.isActive()){
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()){
                        logger.info(String.format("客户端发送消息：%s",rpcRequest.toString()));
                    }else {
                        logger.error("发送消息时有错误发生",future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key=AttributeKey.valueOf("rpcResponse"+rpcRequest.getRequestId());
                RpcResponse rpcResponse=channel.attr(key).get();
                RcpMessageChecker.check(rpcRequest, rpcResponse);
                return rpcResponse.getData();
            }else {
                System.exit(0);
            }
        }catch (InterruptedException e){
            logger.error("发送消息时有错误发生",e);
        }
        return result.get();
    }

}
