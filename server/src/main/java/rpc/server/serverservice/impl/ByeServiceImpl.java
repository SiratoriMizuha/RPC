package rpc.server.serverservice.impl;

import rpc.common.service.ByeService;
import rpc.server.serverservice.Service;

@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye,"+name;
    }
}
