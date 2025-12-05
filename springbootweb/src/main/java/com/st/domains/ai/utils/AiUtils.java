package com.st.domains.ai.utils;

import com.st.infrastructure.client.http.webclient.EmbeddingRequest;
import com.st.infrastructure.client.http.webclient.EmbeddingResponse;
import com.st.infrastructure.client.http.webclient.old.PythonServiceClient;
import com.st.infrastructure.client.http.webclient.old.PythonServiceProperties;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiUtils {

        @Autowired
        private PythonServiceClient pythonServiceClient;

        @Autowired
        private PythonServiceProperties pythonServiceProperties;

        public EmbeddingResponse doEmbedding(String text) {

            // ① Python 服务地址
            String baseUrl = pythonServiceProperties.getEmbeddingService(); // http://python-ai:8000

            // ② Path
            String path = "/api/v1/embedding";

            // ③ 业务请求对象
            EmbeddingRequest request = new EmbeddingRequest();
            request.setText(text);

            // ④ Header：例如 traceId
            Map<String, String> headers = new HashMap<>();
            headers.put("traceId", MDC.get("traceId"));

            // ⑤ 调用封装的客户端
            return pythonServiceClient.postJson(
                            baseUrl,
                            path,
                            request,
                            headers,
                            EmbeddingResponse.class)
                    .block();   // 同步等待结果
        }
    }


