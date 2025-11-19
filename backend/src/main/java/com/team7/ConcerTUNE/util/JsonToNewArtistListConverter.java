package com.team7.ConcerTUNE.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.ConcerTUNE.temp.dto.NewArtistRequestDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JsonToNewArtistListConverter implements AttributeConverter<List<NewArtistRequestDto>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<NewArtistRequestDto> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            // 객체 리스트를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // 직렬화 실패 시 예외 처리
            throw new RuntimeException("Error converting List<NewArtistRequestDto> to JSON string: " + e.getMessage(), e);
        }
    }

    @Override
    public List<NewArtistRequestDto> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        try {
            // TypeReference를 사용하여 List<NewArtistRequestDto> 제네릭 타입을 유지하며 역직렬화
            return objectMapper.readValue(dbData, new TypeReference<List<NewArtistRequestDto>>() {});
        } catch (IOException e) {
            // 역직렬화 실패 시 예외 처리
            throw new RuntimeException("Error converting JSON string to List<NewArtistRequestDto>: " + e.getMessage(), e);
        }
    }
}