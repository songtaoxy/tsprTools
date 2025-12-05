package com.st.infrastructure.client.http.webclient;

import java.util.List;

/**
 * 概述:
 *  用于 LLM 聊天/生成请求的标准数据模型.
 *
 * 功能清单:
 *  1. 承载 prompt、system、history、参数等字段.
 *  2. 作为 Java -> Python 调用的请求体 (JSON).
 *
 * 使用示例:
 *  ChatRequest req = new ChatRequest();
 *  req.setPrompt("什么是 RAG?");
 *  req.setModel("deepseek-chat");
 *
 * 注意事项:
 *  1. history 为可选。
 *  2. model 若为空，Python 端应有默认模型。
 */
public class ChatRequest {

    private String model;            // 模型名称，如 deepseek-chat
    private String prompt;           // 用户输入
    private String system;           // 系统提示 (可选)
    private List<Message> history;   // 历史对话 (可选)

    private Double temperature;      // 可选参数
    private Integer maxTokens;       // 可选参数

    // ---- inner class ----
    public static class Message {
        private String role;    // "user" / "assistant"
        private String content; // 消息内容

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
    }

    // ---- getter / setter ----
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public String getSystem() {
        return system;
    }
    public void setSystem(String system) {
        this.system = system;
    }
    public List<Message> getHistory() {
        return history;
    }
    public void setHistory(List<Message> history) {
        this.history = history;
    }
    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    public Integer getMaxTokens() {
        return maxTokens;
    }
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}
