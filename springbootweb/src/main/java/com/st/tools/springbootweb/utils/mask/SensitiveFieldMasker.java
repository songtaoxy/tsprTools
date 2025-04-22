package com.st.tools.springbootweb.utils.mask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.st.tools.springbootweb.utils.log.LogProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 请求参数脱敏
 * <li>配置, ref</li>
 */
@Component
@RequiredArgsConstructor
public class SensitiveFieldMasker {

    private final LogProperties logProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String maskSensitiveFields(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            maskRecursively(root);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            return json; // fallback
        }
    }

    private void maskRecursively(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            for (String field : logProperties.getSensitiveFields()) {
                if (obj.has(field)) {
                    obj.put(field, "******");
                }
            }
            obj.fieldNames().forEachRemaining(field -> maskRecursively(obj.get(field)));
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                maskRecursively(item);
            }
        }
    }
}
