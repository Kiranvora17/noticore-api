package com.noticore.noticore_api.converter;

import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.Tenants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantsConverter {

    private final ModelMapper modelMapper;

    public TenantsDto convertToDto(Tenants tenants) {
        return modelMapper.map(tenants, TenantsDto.class);
    }

    public Tenants convertToEntity(TenantsDto tenantsDto) {
        return modelMapper.map(tenantsDto, Tenants.class);
    }
}
