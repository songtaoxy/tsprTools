package com.st.infrastructure.client.http.webclient.response;

/**
 * 概述:
 *  通用 API 响应包装类, 对应 JSON 协议层中的 {code,msg,data,traceId,ts}.
 * 功能清单:
 *  1. 承载所有 Java <- Python 的标准响应.
 *  2. 提供 isOk 方法快速判断是否成功.
 *  3. 与 Jackson 搭配, 自动反序列化.
 * 使用示例:
 *  ApiResponse<EmbeddingResponse> resp = client.postJson(..., new TypeReference<ApiResponse<EmbeddingResponse>>(){});
 *  if (!resp.isOk()) { ... }
 * 注意事项:
 *  1. data 为泛型, 需要配合 TypeReference 使用.
 * 入参:
 *  无, 仅作为反序列化类型使用.
 * 出参:
 *  反序列化得到的通用响应对象.
 * 异常说明:
 *  反序列化失败时, 上层 WebClient 调用会抛出异常, 非本类内部抛出.
 */
public class ApiResponse<T> {
    private String code;
    private String msg;
    private T data;
    private String traceId;
    private Long ts;
    public boolean isOk() {
        return "OK".equalsIgnoreCase(code);
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public String getTraceId() {
        return traceId;
    }
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    public Long getTs() {
        return ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }
}

