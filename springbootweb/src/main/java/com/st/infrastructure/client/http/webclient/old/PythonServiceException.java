package com.st.infrastructure.client.http.webclient.old;

/**
 * 概述:
 *  Java 调用 Python 服务过程中发生的统一异常类型.
 * 功能清单:
 *  1. 携带 serviceName、path、HTTP 状态码、响应体摘要等信息.
 *  2. 方便上层日志打点和统一处理.
 * 入参:
 *  serviceName: 下游 Python 服务名称或标识.
 *  path: 调用的具体路径.
 *  statusCode: HTTP 状态码, 可能为 null(如网络层异常).
 *  responseBody: 响应体(可选, 建议截断保存).
 *  cause: 原始异常(可选).
 * 出参:
 *  异常对象本身.
 * 异常说明:
 *  上层捕获此异常, 可根据 statusCode 和业务约定进行降级或告警.
 */
public class PythonServiceException extends RuntimeException {

    private final String serviceName;
    private final String path;
    private final Integer statusCode;
    private final String responseBody;

    public PythonServiceException(String message,
                                  String serviceName,
                                  String path,
                                  Integer statusCode,
                                  String responseBody,
                                  Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.path = path;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPath() {
        return path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
