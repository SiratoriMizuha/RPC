package rpc.server.serverservice.impl;

import com.alibaba.nacos.api.exception.NacosException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.service.CommonDecoder;
import rpc.common.service.CommonEncoder;
import rpc.common.service.CommonSerializer;
import rpc.common.service.impl.NacosServiceDiscovery;
import rpc.common.service.impl.RoundRobinLoadBalancer;
import rpc.server.serverservice.AbstractRpcServer;
import rpc.server.serverservice.ServiceProvider;
import rpc.common.service.ServiceRegistry;

import java.net.InetSocketAddress;

public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer(String host,int port) throws NacosException {
        this(host,port,0);
    }
    public NettyServer(String host,int port,Integer serializer) throws NacosException {
        this.host=host;
        this.port=port;
        serviceRegistry=new NacosServiceDiscovery(new RoundRobinLoadBalancer());
        serviceProvider=new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);


    @Override
    public void start() {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline= ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future=serverBootstrap.bind(host,port).sync();
            ShutdownHook.getShutdownHook().addClearAllHook();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("启动服务器时有错误发生: ", e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
