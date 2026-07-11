package com.noticore.noticore_api.converter;

import com.noticore.noticore_api.dto.SuppressedEmailResponseDto;
import com.noticore.noticore_api.entity.SuppressedEmails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuppressedEmailsConverter {

    private final ModelMapper modelMapper;

    public SuppressedEmailResponseDto convertToDto(SuppressedEmails suppressedEmails) {
        return modelMapper.map(suppressedEmails, SuppressedEmailResponseDto.class);
    }
}
