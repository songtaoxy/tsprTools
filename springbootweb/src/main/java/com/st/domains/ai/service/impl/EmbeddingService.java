package com.st.domains.ai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.infrastructure.client.http.webclient.*;
import com.st.infrastructure.client.http.webclient.old.PythonServiceClient;
import com.st.infrastructure.client.http.webclient.old.PythonServiceProperties;
import com.st.infrastructure.client.http.webclient.response.ApiResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmbeddingService {

    @Autowired
    private PythonServiceClient pythonServiceClient;

    @Autowired
    private PythonServiceProperties pythonServiceProperties;

    @Autowired
    AiHttpClient aiHttpClient;

    public EmbeddingResponse doEmbedding(String text) {

        String baseUrl = pythonServiceProperties.getEmbeddingService();
        String path = "/api/v1/embedding";

        EmbeddingRequest request = new EmbeddingRequest();
        request.setText(text);

        Map<String, String> headers = new HashMap<>();
        headers.put("traceId", MDC.get("traceId"));

        return pythonServiceClient.postJson(
                baseUrl,
                path,
                request,
                headers,
                EmbeddingResponse.class
        ).block();
    }

/*    public void test1(){
        TypeReference<ApiResponse<EmbeddingResponse>> typeRef =
                new TypeReference<ApiResponse<EmbeddingResponse>>() {};
        ApiResponse<EmbeddingResponse> resp = aiHttpClient
                .postJson(baseUrl, "/api/v1/embedding", req, headers, typeRef)
                .block();
        EmbeddingResponse data = resp.getData();

    }*/

    public EmbeddingResponse callEmbedding(String text, String traceId) {
        EmbeddingRequest req = new EmbeddingRequest();
        req.setText(text);
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("X-Trace-Id", traceId);
        TypeReference<ApiResponse<EmbeddingResponse>> typeRef =
                new TypeReference<ApiResponse<EmbeddingResponse>>() {};
        ApiResponse<EmbeddingResponse> resp = aiHttpClient
                .postJson("http://python-ai:8000", "/api/v1/embedding", req, headers, typeRef)
                .block();
        return resp.getData();
    }


    public Flux<ChatEvent> streamChat(ChatRequest req, String traceId) {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("X-Trace-Id", traceId);
        return aiHttpClient.postJsonStream("http://python-llm:8001", "/api/v1/chat/stream", req, headers)
                .map(line -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(line, ChatEvent.class);
                    } catch (Exception e) {
                        throw new AiServiceException("解析流式事件失败: " + line, "INTERNAL_ERROR", traceId, e);
                    }
                });
    }


}

