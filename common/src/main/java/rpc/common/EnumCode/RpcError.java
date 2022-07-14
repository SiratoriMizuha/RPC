package rpc.common.EnumCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    UNKNOWN_PROTOCOL("无法识别协议包类型"),
    UNKNOWN_PACKAGE_TYPE("无法识别数据包类型"),
    UNKNOWN_SERIALIZER("无法识别反序列化器"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接Nacos时出现错误"),
    REGISTER_SERVICE_FAILED("注册服务时有错误发生"),
    SERIALIZER_NOT_FOUND("未设置序列化器"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务器失败"),
    RESPONSE_NOT_MATCH("回复结果不匹配"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("未找到 @ServiceScan 注解"),
    UNKNOWN_ERROR("未知错误"),
    ;

    private final String message;
}
