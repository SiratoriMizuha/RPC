package rpc.common.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private String RequestId;
    //使用接口或服务名称
    private String interfaceName;
    //使用调用方法名称
    private String methodName;
    //调用方法的参数
    private Object[] parameters;
    //调用方法的参数类型
    private Class<?>[] paramTypes;

}
