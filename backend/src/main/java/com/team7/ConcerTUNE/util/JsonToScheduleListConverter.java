package com.team7.ConcerTUNE.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // 💡 추가
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // 💡 추가
import com.team7.ConcerTUNE.temp.dto.ScheduleDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Converter
public class JsonToScheduleListConverter implements AttributeConverter<List<ScheduleDto>, String> {

    private final ObjectMapper objectMapper;

    public JsonToScheduleListConverter() {
        this.objectMapper = new ObjectMapper();

        this.objectMapper.registerModule(new JavaTimeModule());

        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String convertToDatabaseColumn(List<ScheduleDto> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // 직렬화 실패 시, 상세 정보를 포함한 런타임 예외 발생
            throw new RuntimeException("Error converting ScheduleDto list to JSON string", e);
        }
    }

    @Override
    public List<ScheduleDto> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ScheduleDto>>() {});
        } catch (IOException e) {
            // 역직렬화 실패 시, 상세 정보를 포함한 런타임 예외 발생
            throw new RuntimeException("Error converting JSON string to ScheduleDto list", e);
        }
    }
}