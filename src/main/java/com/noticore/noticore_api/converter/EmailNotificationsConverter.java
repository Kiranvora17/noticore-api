package com.noticore.noticore_api.converter;

import com.noticore.noticore_api.dto.EmailNotificationsDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationsConverter {

    private final ModelMapper modelMapper;

    public EmailNotificationsDto covertToDto(EmailNotifications emailNotifications) {
        return modelMapper.map(emailNotifications, EmailNotificationsDto.class);
    }

    public EmailNotifications convertToEntity(EmailNotificationsDto emailNotificationsDto) {
        return modelMapper.map(emailNotificationsDto, EmailNotifications.class);
    }
}
