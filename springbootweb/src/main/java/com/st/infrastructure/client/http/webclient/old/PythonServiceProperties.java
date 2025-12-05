package com.st.infrastructure.client.http.webclient.old;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "python")
public class PythonServiceProperties {
    private String embeddingService;

    public String getEmbeddingService() { return embeddingService; }
    public void setEmbeddingService(String embeddingService) { this.embeddingService = embeddingService; }
}
