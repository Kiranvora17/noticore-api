package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantsRepository extends JpaRepository<Tenants, UUID> {

    Tenants findByRapidapiUsername(String username);
}
