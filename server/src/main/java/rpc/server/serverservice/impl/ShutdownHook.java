package rpc.server.serverservice.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.service.impl.NacosUntil;
import rpc.server.threadPool.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {
    private static final Logger logger= LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool= ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook=new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        logger.info("关闭后自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUntil.clearRegistry();
            threadPool.shutdown();
        }));
    }
}
