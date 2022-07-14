package rpc.server.serverservice;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.service.ServiceRegistry;
import rpc.server.serverservice.impl.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;


public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger= LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() throws NacosException {
        String mainClassName= ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass=Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage=startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)){
            //获得启动类的最外层包
            basePackage=mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet=ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz:classSet){
            if (clazz.isAnnotationPresent(Service.class)){
                String serviceName=clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj=clazz.newInstance();
                }catch (InstantiationException|IllegalAccessException e){
                    logger.error("创建"+clazz+"有错误发生");
                    continue;
                }
                if ("".equals(serviceName)){
                    Class<?>[] interfaces=clazz.getInterfaces();
                    for (Class<?> onInterface:interfaces){
                        publishService(obj,onInterface.getCanonicalName());
                    }
                }else {
                    publishService(obj,serviceName);
                }
            }
        }
    }

    @Override
    public void publishService(Object service, String serviceName) throws NacosException {
        serviceProvider.register(service,serviceName);
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port));
    }
}
