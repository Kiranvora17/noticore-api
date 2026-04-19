package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.TenantsDto;

public interface ITenantsService {

    TenantsDto getOrCreate(String username);
}
