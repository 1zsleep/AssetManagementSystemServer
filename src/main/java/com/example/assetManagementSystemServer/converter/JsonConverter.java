package com.example.assetManagementSystemServer.converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Converter(autoApply = true)  // autoApply=true表示自动应用于所有同类型字段
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    // 使用Jackson的核心JSON处理对象
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将Map转换为数据库列值（Java -> DB）
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            log.info("正在转换Map到JSON");
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // 实际项目中建议使用自定义异常
            throw new RuntimeException("无法将Map转换为JSON: " + e.getMessage());
        }
    }

    /**
     * 将数据库列值转换为Map（DB -> Java）
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new HashMap<>();
            }
            // 使用TypeReference保留Map的泛型信息
            return mapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("无法将JSON转换为Map: " + e.getMessage());
        }
    }
}