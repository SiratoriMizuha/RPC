package rpc.server.serverservice.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.service.model.RpcRequest;
import rpc.common.service.model.RpcResponse;
import rpc.server.serverservice.ServiceProvider;
import rpc.server.threadPool.RequestHandler;

import java.util.UUID;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;

    static {
        requestHandler=new RequestHandler();
        serviceProvider =new ServiceProviderImpl();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器接受请求：{}",rpcRequest);
            String interfaceName=rpcRequest.getInterfaceName();
            Object service= serviceProvider.getService(interfaceName);
            Object result=requestHandler.handle(rpcRequest,service);
            ChannelFuture future=channelHandlerContext.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
