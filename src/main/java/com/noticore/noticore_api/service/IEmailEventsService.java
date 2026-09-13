package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.BrevoEventDto;

public interface IEmailEventsService {
    void handleEmailEvents(BrevoEventDto eventDto, String message);
}
