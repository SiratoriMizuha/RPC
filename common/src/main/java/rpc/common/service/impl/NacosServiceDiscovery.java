package rpc.common.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.service.LoadBalancer;
import rpc.common.service.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if (loadBalancer==null)this.loadBalancer=new RoundRobinLoadBalancer();
        else this.loadBalancer=loadBalancer;
    }


    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance>instances=NacosUntil.getAllInstance(serviceName);
            Instance instance=loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生",e);
        }
        return null;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUntil.registerService(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生",e);
        }
    }
}
