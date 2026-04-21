package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.TenantsConverter;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.repository.TenantsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantsServiceImpl implements ITenantsService {

    private final TenantsRepository tenantsRepository;
    private final TenantsConverter tenantsConverter;

    @Override
    @Transactional
    public TenantsDto getOrCreate(String username) {
        log.info("inside method: getOrcreate()");
        Tenants tenant = tenantsRepository.findByRapidapiUsername(username);

        if(tenant == null) {
            log.info("tenant not found with username: {}", username);
            log.info("initializing tenant creation for username: {} at {}.", username, LocalDateTime.now());

            Tenants t = new Tenants();
            t.setRapidapiUsername(username);
            tenantsRepository.save(t);
            log.info("tenanat creation for user: {} is completed.", username);
            return tenantsConverter.convertToDto(t);
        } else {
            log.info("tenant found with username: {}", username);
            return tenantsConverter.convertToDto(tenant);
        }
    }
}
