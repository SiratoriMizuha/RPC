package rpc.server.serverservice.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.service.HelloService;
import rpc.common.service.model.HelloModel;
import rpc.server.serverservice.Service;

@Service
public class HelloImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloImpl.class);
    @Override
    public String hello(HelloModel helloModel) {
        System.out.println("接收到"+helloModel.getMessage());
        return "这是掉用的返回值，id=" + helloModel.getId().toString();
    }
}
