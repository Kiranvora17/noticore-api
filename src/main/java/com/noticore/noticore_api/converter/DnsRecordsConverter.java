package com.noticore.noticore_api.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noticore.noticore_api.dto.DnsRecordDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Converter
@RequiredArgsConstructor
public class DnsRecordsConverter implements AttributeConverter<Set<DnsRecordDto>, String> {


    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Set<DnsRecordDto> dnsRecordDto) {
        try {
            return objectMapper.writeValueAsString(dnsRecordDto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<DnsRecordDto> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<Set<DnsRecordDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
