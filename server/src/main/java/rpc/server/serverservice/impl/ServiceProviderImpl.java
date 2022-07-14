package rpc.server.serverservice.impl;

import rpc.common.EnumCode.RpcError;
import rpc.common.exception.RpcException;
import rpc.server.serverservice.Service;
import rpc.server.serverservice.ServiceProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void register(T service, String serviceName) {
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        Class<?>[] interfaces=service.getClass().getInterfaces();
        if (interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?>i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service =serviceMap.get(serviceName);
        if (service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
