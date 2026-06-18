package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SesEventDto;

public interface IEmailEventsService {
    void handleEmailEvents(SesEventDto eventDto, String message);
}
