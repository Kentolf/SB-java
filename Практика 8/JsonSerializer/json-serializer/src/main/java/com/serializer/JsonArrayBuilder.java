package com.serializer;

import java.util.ArrayList;
import java.util.List;

public class JsonArrayBuilder {
    private final List<String> items = new ArrayList<>();

    public void add(String value) {
        items.add(value);
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String item : items) {
            if (!first) sb.append(",");
            sb.append(item);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
