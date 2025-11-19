package com.team7.ConcerTUNE.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
@Component
public class JsonToLongListConverter implements AttributeConverter<List<Long>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            // List<Long> 객체를 JSON 문자열로 직렬화
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List<Long> to JSON string", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        try {
            // JSON 문자열을 List<Long> 객체로 역직렬화
            return objectMapper.readValue(dbData, new TypeReference<List<Long>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON string to List<Long>", e);
        }
    }
}