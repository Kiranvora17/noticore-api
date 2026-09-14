package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.dto.SuppressedEmailRequestDto;
import com.noticore.noticore_api.dto.SuppressedEmailResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.service.ISuppressedEmailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppressions")
@RequiredArgsConstructor
public class SuppressedEmailController {

    private final ISuppressedEmailsService iSuppressedEmailsService;

    @PostMapping
    public ResponseEntity<SuppressedEmailResponseDto> addSuppression(
            @Valid @RequestBody SuppressedEmailRequestDto request,
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        SuppressedEmailResponseDto response = iSuppressedEmailsService.addManualSuppression(tenant, request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SuppressedEmailResponseDto>> getAllSuppressions(
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        List<SuppressedEmailResponseDto> response = iSuppressedEmailsService.getAllSuppressions(tenant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<SuppressedEmailResponseDto> removeSuppression(
            @PathVariable String email,
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        SuppressedEmailResponseDto response = iSuppressedEmailsService.removeSuppression(tenant, email);
        return ResponseEntity.ok(response);
    }

}
