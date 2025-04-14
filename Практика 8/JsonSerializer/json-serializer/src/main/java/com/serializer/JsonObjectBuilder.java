package com.serializer;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObjectBuilder {
    private final Map<String, String> fields = new LinkedHashMap<>();

    public void add(String key, String value) {
        fields.put(key, value);
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(JsonUtils.escapeString(entry.getKey())).append("\":").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
