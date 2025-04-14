package com.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonSerializer {
    public String serialize(Object object) throws JsonSerializationException {
        try {
            return serializeValue(object);
        }
        catch (Exception e) {
            throw new JsonSerializationException("Error during serialization", e);
        }
    }

    private String serializeValue(Object value) throws Exception {
        if (value == null) {
            return "null";
        }
        else if (value instanceof String) {
            return "\"" + JsonUtils.escapeString((String) value) + "\"";
        }
        else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        else if (value instanceof Collection) {
            return serializeCollection((Collection<?>) value);
        }
        else if (value instanceof Map) {
            return serializeMap((Map<?, ?>) value);
        }
        else if (value.getClass().isArray()) {
            return serializeArray(value);
        }
        else {
            return serializeCustomObject(value);
        }
    }

    private String serializeCustomObject(Object object) throws Exception {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonField.class)) {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                JsonField annotation = field.getAnnotation(JsonField.class);
                String fieldName = annotation.name().isEmpty() ? field.getName() : annotation.name();
                builder.add(fieldName, serializeValue(fieldValue));
            }
        }
        return builder.build();
    }

    private String serializeCollection(Collection<?> collection) throws Exception {
        JsonArrayBuilder builder = new JsonArrayBuilder();
        for (Object item : collection) {
            builder.add(serializeValue(item));
        }
        return builder.build();
    }

    private String serializeMap(Map<?, ?> map) throws Exception {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new JsonSerializationException("Map keys must be strings", null);
            }
            String key = (String) entry.getKey();
            builder.add(key, serializeValue(entry.getValue()));
        }
        return builder.build();
    }

    private String serializeArray(Object array) throws Exception {
        JsonArrayBuilder builder = new JsonArrayBuilder();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            builder.add(serializeValue(item));
        }
        return builder.build();
    }
}
