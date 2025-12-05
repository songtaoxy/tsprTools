package com.st.infrastructure.client.http.webclient;

import lombok.Data;

@Data
public class EmbeddingResponse {
    private float[] embedding;
    private String model;
    private int dimension;
}
