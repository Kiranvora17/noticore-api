package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.dto.EmailNotificationsDto;
import com.noticore.noticore_api.dto.SendEmailRequestDto;
import com.noticore.noticore_api.dto.SendEmailResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.service.IEmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/notifications/email")
@RequiredArgsConstructor
public class EmailController {

    private final IEmailService iEmailService;

    @PostMapping
    public ResponseEntity<SendEmailResponseDto> sendEmail(
            @RequestBody SendEmailRequestDto sendEmailRequestDto,
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenantsDto = (TenantsDto) httpServletRequest.getAttribute("tenant");
        SendEmailResponseDto res = iEmailService.sendEmail(tenantsDto, sendEmailRequestDto);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<EmailNotificationsDto>> getAll(
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenantsDto = (TenantsDto) httpServletRequest.getAttribute("tenant");
        List<EmailNotificationsDto> notificationsDtos = iEmailService.getAll(tenantsDto);
        return ResponseEntity.ok(notificationsDtos);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<EmailNotificationsDto> getEmailNotification(
            @PathVariable UUID notificationId) {
        EmailNotificationsDto notification = iEmailService
                .getEmailNotification(notificationId);

        return ResponseEntity.ok(notification);
    }
}
