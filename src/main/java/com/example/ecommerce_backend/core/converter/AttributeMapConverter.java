package com.example.ecommerce_backend.core.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Converter
public class AttributeMapConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize attributes", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(dbData, new TypeReference<Map<String, String>>() {});
        } catch (Exception firstPass) {
            // H2's JSON column type wraps the converter output into a JSON string
            // (e.g. {"storage":"256GB"} -> "{\"storage\":\"256GB\"}"). Unwrap it once
            // before falling back, so legacy/double-encoded rows never 500 the API.
            try {
                String inner = MAPPER.readValue(dbData, String.class);
                if (inner == null || inner.isBlank()) {
                    return new HashMap<>();
                }
                return MAPPER.readValue(inner, new TypeReference<Map<String, String>>() {});
            } catch (Exception secondPass) {
                String trimmed = dbData.trim();
                Map<String, String> fallback = new HashMap<>();
                fallback.put("value", trimmed);
                return fallback;
            }
        }
    }
}