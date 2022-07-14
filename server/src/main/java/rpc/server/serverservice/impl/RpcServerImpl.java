package rpc.server.serverservice.impl;

import rpc.server.serverservice.ServiceProvider;
import rpc.server.threadPool.RequestHandler;
import rpc.server.threadPool.RequestHandlerThread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServerImpl {

    private ExecutorService threadPool;


    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    //拥塞队列容量
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceProvider serviceProvider;
    public RpcServerImpl(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port) {
        try (ServerSocket serverSocket=new ServerSocket(port)){
            System.out.println("服务器正在启动");
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                threadPool.execute(new RequestHandlerThread(socket,requestHandler, serviceProvider));
            }
            threadPool.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
