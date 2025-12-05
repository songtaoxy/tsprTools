package com.st.infrastructure.client.http.webclient;

public class ChatEvent {
    private String type;
    private String data;
    private Usage usage;
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        public int getPromptTokens() {
            return promptTokens;
        }
        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }
        public int getCompletionTokens() {
            return completionTokens;
        }
        public void setCompletionTokens(int completionTokens) {
            this.completionTokens = completionTokens;
        }
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Usage getUsage() {
        return usage;
    }
    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}

