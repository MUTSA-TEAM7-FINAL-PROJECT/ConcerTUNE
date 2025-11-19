package com.team7.ConcerTUNE.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, Integer>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Map 객체를 DB에 저장하기 위한 JSON String으로 변환합니다. (직렬화)
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // 직렬화 실패 시 예외 처리
            throw new RuntimeException("Error converting Map to JSON string: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new HashMap<>();
        }
        try {
            // TypeReference를 사용하여 제네릭 타입 정보(Map<String, Integer>)를 유지하며 역직렬화
            return objectMapper.readValue(dbData,
                    objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Integer.class));
        } catch (IOException e) {
            // 역직렬화 실패 시 예외 처리
            throw new RuntimeException("Error converting JSON string to Map: " + e.getMessage(), e);
        }
    }
}